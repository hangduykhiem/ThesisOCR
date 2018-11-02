package com.wolt.tacotaco.helpers

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View
import com.wolt.tacotaco.Controller
import com.wolt.tacotaco.Controller.LifecycleStage.*
import com.wolt.tacotaco.components.TransitionAnimation

internal class SetBackstackDelegate(
        private val controller: Controller<*, *>
) {

    private val runningAnimations = mutableMapOf<Int, SwapAnimationWrapper>()

    fun setBackstack(
            backstackId: Int,
            newBackstack: List<Controller<*, *>>,
            animation: TransitionAnimation? = null
    ) {
        val oldBackstack = controller.backstacksInternal[backstackId]
        val enterChild = newBackstack.lastOrNull()
        val exitChild = oldBackstack?.lastOrNull()
        if (newBackstack.isNotEmpty()) {
            controller.backstacksInternal[backstackId] = newBackstack
        } else {
            controller.backstacksInternal.remove(backstackId)
        }
        newBackstack
                .filter { it.stage == None }
                .onEach {
                    it.parentInternal = controller
                    it.activityInternal = controller.activity
                }
                .filterNot { it == enterChild }
                .forEach { LifecycleConductor.changeStage(it, BottomDeflated) }
        if (enterChild != exitChild) {
            val killExitChild = exitChild !in newBackstack
            swapTopChildren(enterChild, exitChild, backstackId, killExitChild, animation)
        }
        oldBackstack
                ?.filter { it !in newBackstack && it != exitChild }
                ?.forEach { LifecycleConductor.changeStage(it, Dead) }
    }

    fun endRunningAnimations() {
        // toList() is to avoid ConcurrentModificationException
        runningAnimations.values.toList().forEach { it.end() }
    }

    private fun swapTopChildren(
            enterChild: Controller<*, *>?,
            exitChild: Controller<*, *>?,
            backstackId: Int,
            killExitChild: Boolean,
            animation: TransitionAnimation? = null
    ) {
        runningAnimations[backstackId]?.end()
        val stage = when (controller.stage) {
            is BottomDeflated -> TopDeflated
            is BottomInflated -> TopDeflated
            is TopDeflated -> TopDeflated
            is TopInflatedBackground -> TopDeflated
            is TopInflatedForeground -> TopInflatedForeground
            else -> throw IllegalStateException()
        }
        LifecycleConductor.changeStage(enterChild, stage)
        if (controller.stage == TopInflatedForeground && animation != null) {
            if (exitChild != null) {
                if (killExitChild) {
                    controller.dyingChildren.add(exitChild)
                }
                dispatchExitingStarted(exitChild)
            }
            runningAnimations[backstackId] = SwapAnimationWrapper(enterChild, exitChild, animation,
                    endCallback = {
                        runningAnimations.remove(backstackId)
                        onSwapEnd(exitChild, killExitChild)
                    }
            ).apply { start() }
        } else {
            onSwapEnd(exitChild, killExitChild)
        }
    }

    private fun onSwapEnd(exitChild: Controller<*, *>?, killExitChild: Boolean) {
        if (exitChild != null) {
            if (killExitChild) {
                controller.dyingChildren.remove(exitChild)
            }
            dispatchExitingEnded(exitChild)
            val stage = when {
                killExitChild -> Dead
                exitChild.stage == TopDeflated -> BottomDeflated
                exitChild.stage == TopInflatedForeground -> BottomInflated
                exitChild.stage == TopInflatedBackground -> BottomInflated
                else -> throw IllegalStateException()
            }
            LifecycleConductor.changeStage(exitChild, stage)
        }
    }

    private fun dispatchExitingStarted(child: Controller<*, *>) {
        child.exitingInternal = true
        child.getTopChildren()
                .filter { it.stage == TopInflatedForeground || it.stage == TopInflatedBackground }
                .forEach { dispatchExitingStarted(it) }
    }

    private fun dispatchExitingEnded(child: Controller<*, *>) {
        child.exitingInternal = false
        child.getTopChildren()
                .filter { it.stage == TopInflatedForeground || it.stage == TopInflatedBackground }
                .forEach { dispatchExitingEnded(it) }
    }

    private inner class SwapAnimationWrapper(
            private val enterController: Controller<*, *>?,
            private val exitController: Controller<*, *>?,
            private val animation: TransitionAnimation,
            private val endCallback: () -> Unit
    ) {

        private var animator: Animator? = null
        private var interrupted = false

        fun start() {
            if (enterController == null && exitController == null) {
                endCallback.invoke()
                return
            }
            if (animation.exitViewOnTop()) {
                exitController?.view?.bringToFront()
            }
            val layoutRequested = enterController?.view?.isLayoutRequested == true
            if (layoutRequested) {
                val container = enterController?.view?.parent as? View
                container?.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
                    override fun onLayoutChange(v: View, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int,
                                                oldTop: Int, oldRight: Int, oldBottom: Int) {
                        v.removeOnLayoutChangeListener(this)
                        if (!interrupted) {
                            playAnimation()
                        }
                    }
                })
            } else {
                playAnimation()
            }
        }

        private fun playAnimation() {
            animator = animation.getAnimator(enterController, exitController)
            animator?.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(ignored: Animator?) {
                    endCallback.invoke()
                }
            })
            animator?.start()
        }

        fun end() {
            val animator = animator
            if (animator != null) {
                animator.end()
            } else {
                interrupted = true
                endCallback.invoke()
            }
        }

    }

}
