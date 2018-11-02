package com.wolt.tacotaco.helpers

import android.view.LayoutInflater
import android.view.ViewGroup
import com.crashlytics.android.Crashlytics
import com.wolt.tacotaco.Controller
import com.wolt.tacotaco.Controller.LifecycleStage
import com.wolt.tacotaco.Controller.LifecycleStage.*
import com.wolt.tacotaco.components.Args
import com.wolt.tacotaco.components.Model

internal object LifecycleConductor {

    @Suppress("UnnecessaryVariable")
    fun changeStage(controller: Controller<*, *>?, newStage: LifecycleStage) {
        controller ?: return
        val s1 = controller.stage
        val s2 = newStage
        when {
        // -------------------------------------------------------------------------------------------------------------
            s1 == None && s2 == BottomDeflated -> {
                injectAndNotifyAttach(controller, stage = BottomDeflated)
            }
            s1 == None && s2 == TopDeflated -> {
                injectAndNotifyAttach(controller, stage = TopDeflated)
            }
            s1 == None && s2 == TopInflatedForeground -> {
                injectAndNotifyAttach(controller, stage = TopDeflated)
                inflateAndAttachView(controller)
                notifyForeground(controller)
            }
        // -------------------------------------------------------------------------------------------------------------
            s1 == TopDeflated && s2 == TopInflatedForeground -> {
                inflateAndAttachView(controller)
                notifyForeground(controller)
            }
            s1 == TopDeflated && s2 == BottomDeflated -> {
                // do nothing
            }
            s1 == TopDeflated && s2 == Dead -> {
                notifyDetach(controller)
            }
        // -------------------------------------------------------------------------------------------------------------
            s1 == TopInflatedForeground && s2 == TopInflatedBackground -> {
                notifyBackground(controller)
            }
            s1 == TopInflatedForeground && s2 == BottomInflated -> {
                notifyBackground(controller)
                detachView(controller)
            }
            s1 == TopInflatedForeground && s2 == Dead -> {
                notifyBackground(controller)
                deflateAndDetachView(controller, saveState = false)
                notifyDetach(controller)
            }
        // -------------------------------------------------------------------------------------------------------------
            s1 == TopInflatedBackground && s2 == TopDeflated -> {
                deflateAndDetachView(controller, saveState = true)
            }
            s1 == TopInflatedBackground && s2 == TopInflatedForeground -> {
                inflateAndAttachViewsOfTopChildren(controller)
                notifyForeground(controller)
            }
            s1 == TopInflatedBackground && s2 == BottomInflated -> {
                detachView(controller)
            }
            s1 == TopInflatedBackground && s2 == Dead -> {
                deflateAndDetachView(controller, saveState = false)
                notifyDetach(controller)
            }
        // -------------------------------------------------------------------------------------------------------------
            s1 == BottomInflated && s2 == TopInflatedForeground -> {
                attachView(controller, getContainerView(controller))
                inflateAndAttachViewsOfTopChildren(controller)
                notifyForeground(controller)
            }
            s1 == BottomInflated && s2 == TopInflatedBackground -> {
                attachView(controller, getContainerView(controller))
            }
            s1 == BottomInflated && s2 == BottomDeflated -> {
                deflate(controller, saveHierarchyState = true, saveClientState = true, stage = BottomDeflated)
            }
            s1 == BottomInflated && s2 == Dead -> {
                deflate(controller, saveHierarchyState = false, saveClientState = false, stage = BottomDeflated)
                notifyDetach(controller)
            }
        // -------------------------------------------------------------------------------------------------------------
            s1 == BottomDeflated && s2 == TopDeflated -> {
                // do nothing
            }
            s1 == BottomDeflated && s2 == TopInflatedForeground -> {
                inflateAndAttachView(controller)
                notifyForeground(controller)
            }
            s1 == BottomDeflated && s2 == Dead -> {
                notifyDetach(controller)
            }
        // -------------------------------------------------------------------------------------------------------------
            else -> {
                throw IllegalStateException("${controller.javaClass.simpleName} ${s1.javaClass.simpleName} " +
                        s2.javaClass.simpleName)
            }
        }
    }

    private fun <A : Args, M : Model> injectAndNotifyAttach(controller: Controller<A, M>, stage: LifecycleStage) {
        controller.apply {
            inject()
            interactor?.controller = this
            analytics?.controller = this
            controller.stage = stage
            log(this, "onAttach")
            onAttach(restored)
            interactor?.onAttach(restored)
            analytics?.onAttach(restored)
            lifecycleProvider.onAttach()
            // can't have children at this point
        }
    }

    private fun <A : Args, M : Model> inflateAndAttachView(controller: Controller<A, M>) {
        controller.apply {
            val containerView = getContainerView(this)
            onInflate(LayoutInflater.from(controller.activity), containerView)
            attachView(this, containerView)
            log(this, "onPostInflate")
            onPostInflateInternal()
            controller.stage = TopInflatedForeground
            model?.let { renderModelInternal(oldModel = null, newModel = it, payload = null) }
            getTopChildren().forEach { inflateAndAttachView(it) }
            onHierarchyReady()
        }
    }

    private fun getContainerView(controller: Controller<*, *>): ViewGroup? {
        val parent = controller.parentInternal ?: return null
        return parent.backstacksInternal.entries
                .first { (_, children) -> controller in children }
                .let { (id, _) -> parent.customFindViewById(id, parent.view) }
    }

    private fun attachView(controller: Controller<*, *>, containerView: ViewGroup?) {
        if (controller.parentInternal == null) {
            controller.activity.setContentView(controller.view)
        } else {
            containerView?.addView(controller.view) ?: throw IllegalStateException()
        }
    }

    private fun inflateAndAttachViewsOfTopChildren(controller: Controller<*, *>) {
        controller.apply {
            getTopChildren()
                    .forEach { child ->
                        if (child.view == null) {
                            inflateAndAttachView(child)
                        } else {
                            inflateAndAttachViewsOfTopChildren(child)
                        }
                    }
        }
    }

    private fun notifyForeground(controller: Controller<*, *>) {
        controller.apply {
            stage = TopInflatedForeground
            log(this, "onForeground")
            onForeground()
            interactor?.onForeground()
            analytics?.onForeground()
            lifecycleProvider.onForeground()
            getTopChildren().forEach { notifyForeground(it) }
            dyingChildren.forEach { notifyForeground(it) }
        }
    }

    private fun notifyBackground(controller: Controller<*, *>) {
        controller.apply {
            stage = TopInflatedBackground
            log(this, "onBackground")
            onBackground()
            interactor?.onBackground()
            analytics?.onBackground()
            lifecycleProvider.onBackground()
            getTopChildren().forEach { notifyBackground(it) }
            dyingChildren.forEach { notifyBackground(it) }
        }
    }

    private fun detachView(controller: Controller<*, *>) {
        val view = controller.view ?: throw IllegalStateException()
        (view.parent as ViewGroup).removeView(view)
        controller.stage = BottomInflated
    }

    private fun deflateAndDetachView(controller: Controller<*, *>, saveState: Boolean) {
        val view = controller.view ?: throw IllegalStateException()
        deflate(controller, saveHierarchyState = saveState, saveClientState = saveState, stage = TopDeflated)
        (view.parent as ViewGroup).removeView(view)
    }

    private fun deflate(
            controller: Controller<*, *>,
            saveHierarchyState: Boolean,
            saveClientState: Boolean,
            stage: LifecycleStage
    ) {
        controller.apply {
            setBackstackDelegate.endRunningAnimations()
            if (saveHierarchyState) {
                saveViewHierarchyState()
            }
            if (saveClientState) {
                saveViewClientState()
            }
            log(this, "onDeflate")
            onDeflate()
            lifecycleProvider.onDeflate()
            onPostDeflate()
            this.stage = stage
            val topChildren = getTopChildren()
            getAllChildren().forEach { child ->
                child.view?.let { childView ->
                    deflate(
                            controller = child,
                            saveHierarchyState = saveHierarchyState && childView.parent == null,
                            saveClientState = saveClientState,
                            stage = if (child in topChildren) TopDeflated else BottomDeflated
                    )
                }
            }
        }
    }

    private fun notifyDetach(controller: Controller<*, *>) {
        controller.apply {
            stage = Dead
            log(this, "onDetach")
            onDetach()
            interactor?.onDetach()
            analytics?.onDetach()
            lifecycleProvider.onDetach()
            getAllChildren().forEach { notifyDetach(it) }
        }
    }

    private fun log(controller: Controller<*, *>, msg: String) {
        val fullMsg = "${controller.javaClass.simpleName}:${controller.hashCode()} $msg"
        Crashlytics.log(fullMsg)
    }

}
