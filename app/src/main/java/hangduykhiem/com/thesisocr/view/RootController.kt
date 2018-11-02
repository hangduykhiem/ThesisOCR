package hangduykhiem.com.thesisocr.view

import com.wolt.tacotaco.Controller
import com.wolt.tacotaco.Interactor
import com.wolt.tacotaco.components.Args
import hangduykhiem.com.thesisocr.R
import hangduykhiem.com.thesisocr.helper.NoModel

class RootController(
    args: RootArgs
) : Controller<RootArgs, NoModel>(args) {
    override val layoutId = R.layout.controller_root
    override val interactor: Interactor<RootArgs, NoModel>? = null

    override fun inject() {}

}

class RootArgs : Args