package com.wolt.tacotaco.components

import com.wolt.tacotaco.Controller

abstract class ControllerAnalytics<A : Args, M : Model> {

    internal lateinit var controller: Controller<A, M>
    val args: A
        get() = controller.args
    protected val model: M
        get() = controller.model ?: throw IllegalStateException()

    open fun onAttach(restored: Boolean) {}

    open fun onForeground() {}

    open fun onBackground() {}

    open fun onDetach() {}

    open fun onCommand(command: Command) {}

    open fun onUpdateModel(oldModel: M?) {}

    open fun onRenderModel(oldModel: M?, payload: ChangePayload?) {}

    open fun onTransition(transition: Transition) {}

}
