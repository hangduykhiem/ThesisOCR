package com.wolt.tacotaco

import android.os.Bundle
import com.wolt.tacotaco.components.*

abstract class Interactor<A : Args, M : Model> {

    val args: A
        get() = controller.args
    protected val model: M
        get() = controller.model ?: throw IllegalStateException()
    protected val controllerTreeNode: ControllerTreeNode
        get() = controller
    protected val lifecycleProvider: LifecycleProvider
        get() = controller.lifecycleProvider

    internal lateinit var controller: Controller<A, M>

    open fun onAttach(restored: Boolean) {}

    open fun onForeground() {}

    open fun onBackground() {}

    open fun onSaveInstanceState(outState: Bundle) {}

    open fun onDetach() {}

    open fun handleCommand(command: Command) {}

    fun updateModel(newModel: M = model, changePayload: ChangePayload? = null) {
        controller.updateModel(newModel, changePayload)
    }

    fun navigate(transition: Transition) {
        controller.handleTransitionInternal(transition)
    }

}
