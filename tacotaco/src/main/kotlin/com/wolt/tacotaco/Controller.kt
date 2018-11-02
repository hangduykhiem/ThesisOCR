package com.wolt.tacotaco

import android.app.Activity
import android.os.Bundle
import android.os.Looper
import android.os.Parcelable
import android.support.annotation.IdRes
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.ButterKnife
import butterknife.Unbinder
import com.wolt.tacotaco.Controller.LifecycleStage.*
import com.wolt.tacotaco.components.*
import com.wolt.tacotaco.helpers.LifecycleConductor
import com.wolt.tacotaco.helpers.SetBackstackDelegate

abstract class Controller<A : Args, M : Model>(
        val args: A
) : ControllerTreeNode {

    abstract val layoutId: Int
    abstract val interactor: Interactor<A, M>?
    open val analytics: ControllerAnalytics<A, M>? = null
    var view: View? = null
        private set
    val activity: Activity
        get() = activityInternal
    val exiting: Boolean
        get() = exitingInternal
    val lifecycleProvider = LifecycleProvider()
    override val parent: ControllerTreeNode?
        get() = parentInternal
    override val backstacks: Map<Int, List<ControllerTreeNode>>
        get() = backstacksInternal
    override val tag: String = javaClass.name

    internal var stage: LifecycleStage = None
    internal var restored = false
    internal var model: M? = null
    internal var parentInternal: Controller<*, *>? = null
    internal lateinit var activityInternal: Activity
    internal var exitingInternal: Boolean = false
    internal val backstacksInternal = mutableMapOf<Int, List<Controller<*, *>>>()
    internal val dyingChildren = mutableListOf<Controller<*, *>>()
    internal var savedHierarchyState: SparseArray<Parcelable>? = null
    internal var savedClientState: Bundle? = null
    @Suppress("LeakingThis")
    internal val setBackstackDelegate = SetBackstackDelegate(this)
    private lateinit var butterknifeUnbinder: Unbinder
    private val viewProperties: MutableList<ViewProperty<*>> = mutableListOf()

    abstract fun inject()

    open fun onAttach(restored: Boolean) {}

    open fun onPostInflate(savedViewState: Bundle?) {}

    open fun onForeground() {}

    open fun onBackground() {}

    open fun onSaveViewState(outState: Bundle) {}

    open fun onDeflate() {}

    open fun onDetach() {}

    open fun onLowMemory() {}

    open fun onBackPressed(): Boolean {
        return backstacksInternal.values.map { it.last() }.any { it.onBackPressed() }
    }

    open fun renderModel(oldModel: M?, newModel: M, payload: ChangePayload?) {}

    open fun handleTransition(transition: Transition) {
        dispatchTransitionToParent(transition)
    }

    fun dispatchTransitionToParent(transition: Transition) {
        parentInternal?.handleTransitionInternal(transition) ?: throw IllegalStateException()
    }

    fun sendCommand(command: Command) {
        analytics?.onCommand(command)
        interactor?.handleCommand(command)
    }

    fun getBackstack(backstackId: Int) = backstacksInternal[backstackId] ?: emptyList()

    fun setBackstack(backstackId: Int, backstack: List<Controller<*, *>>, animation: TransitionAnimation? = null) {
        setBackstackDelegate.setBackstack(backstackId, backstack, animation)
    }

    fun <V : View> bindView(id: Int): ViewProperty<V> {
        val property = ViewProperty { customFindViewById<V>(id, view) ?: throw IllegalArgumentException() }
        viewProperties.add(property)
        return property
    }

    @Suppress("UNCHECKED_CAST")
    internal fun setRestoredModel(model: Model?) {
        this.model = model as M?
    }

    internal fun updateModel(model: M, changePayload: ChangePayload?) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            throw IllegalStateException("Non-UI thread")
        }
        val oldModel = this.model
        this.model = model
        analytics?.onUpdateModel(oldModel)
        if (view != null) {
            renderModelInternal(oldModel, model, changePayload)
        }
    }

    internal fun getAllChildren() = backstacksInternal.values.flatten()

    internal fun getTopChildren() = backstacksInternal.values.map { it.last() }

    internal fun onInflate(inflater: LayoutInflater, container: ViewGroup?) {
        view = inflater.inflate(layoutId, container, false)
        butterknifeUnbinder = ButterKnife.bind(this, view!!)
    }

    internal fun onPostInflateInternal() {
        val state = savedClientState
        savedClientState = null
        onPostInflate(state)
    }

    internal fun onHierarchyReady() {
        val state = savedHierarchyState
        savedHierarchyState = null
        if (state != null) {
            view?.restoreHierarchyState(state) ?: throw IllegalStateException()
        }
    }

    internal fun onPostDeflate() {
        viewProperties.forEach { it.unbind() }
        butterknifeUnbinder.unbind()
        view = null
    }

    internal fun saveViewHierarchyState() {
        savedHierarchyState = SparseArray<Parcelable>().also {
            view?.saveHierarchyState(it) ?: throw IllegalStateException()
        }
    }

    internal fun saveViewClientState() {
        savedClientState = Bundle().also { onSaveViewState(it) }
    }

    internal fun onLowMemoryInternal() {
        onLowMemory()
        getAllChildren().forEach { it.onLowMemoryInternal() }
    }

    internal fun renderModelInternal(oldModel: M?, newModel: M, payload: ChangePayload?) {
        analytics?.onRenderModel(oldModel, payload)
        renderModel(oldModel, newModel, payload)
    }

    internal fun handleTransitionInternal(transition: Transition) {
        analytics?.onTransition(transition)
        handleTransition(transition)
    }

    internal fun onTrimMemory() {
        when (stage) {
            TopInflatedForeground -> getAllChildren().forEach { it.onTrimMemory() }
            TopInflatedBackground -> LifecycleConductor.changeStage(this, TopDeflated)
            BottomInflated -> LifecycleConductor.changeStage(this, BottomDeflated)
        }
    }

    /**
     * findViewById that filters out backstack containers
     */
    @Suppress("UNCHECKED_CAST")
    internal fun <V : View> customFindViewById(@IdRes id: Int, container: View?): V? {
        return when {
            container == null -> null
            container.id == id -> container as V
            container.id in backstacksInternal.keys -> null
            container !is ViewGroup -> null
            else -> {
                (0 until container.childCount)
                        .map { container.getChildAt(it) }
                        .forEach { child ->
                            customFindViewById<V>(id, child)?.let { return it }
                        }
                return null
            }
        }
    }

    internal sealed class LifecycleStage {
        object None : LifecycleStage()
        object TopDeflated : LifecycleStage()
        object TopInflatedForeground : LifecycleStage()
        object TopInflatedBackground : LifecycleStage()
        object BottomInflated : LifecycleStage()
        object BottomDeflated : LifecycleStage()
        object Dead : LifecycleStage()
    }

}
