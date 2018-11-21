package hangduykhiem.com.thesisocr.view.controller

import com.wolt.tacotaco.Controller
import com.wolt.tacotaco.components.Args
import com.wolt.tacotaco.components.Model
import com.wolt.tacotaco.components.TransitionAnimation

/**
 * BaseController to generalize the backstack handling model
 */
abstract class BaseController<A : Args, M : Model>(args: A) : Controller<A, M>(args) {

    fun pushChild(
        controller: BaseController<*, *>,
        backstackId: Int,
        animation: TransitionAnimation? = null
    ) {
        val oldBackstack = getBackstack(backstackId)
        if (oldBackstack.lastOrNull()?.tag != controller.tag) {
            setBackstack(backstackId, oldBackstack + controller, animation)
        }
    }

    fun popChild(
        backstackId: Int,
        tag: String,
        animation: TransitionAnimation? = null
    ) {
        val oldBackstack = getBackstack(backstackId)
        if (oldBackstack.lastOrNull()?.tag == tag) {
            setBackstack(backstackId, oldBackstack.dropLast(1), animation)
        }
    }


}