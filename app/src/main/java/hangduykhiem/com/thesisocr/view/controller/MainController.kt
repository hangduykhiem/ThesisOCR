package hangduykhiem.com.thesisocr.view.controller

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import com.wolt.tacotaco.components.Command
import com.wolt.tacotaco.components.Transition
import hangduykhiem.com.thesisocr.R
import hangduykhiem.com.thesisocr.di.modules.ControllerModule
import hangduykhiem.com.thesisocr.domain.interactor.MainInteractor
import hangduykhiem.com.thesisocr.domain.interactor.MainModel
import hangduykhiem.com.thesisocr.helper.NoArg
import hangduykhiem.com.thesisocr.view.MainActivity
import javax.inject.Inject

class MainController : BaseController<NoArg, MainModel>(NoArg) {

    override val layoutId = R.layout.controller_main
    val button: Button by bindView(R.id.button)

    @Inject
    override lateinit var interactor: MainInteractor


    override fun inject() {
        (activity as MainActivity).component.plus(ControllerModule(this)).inject(this)
    }

    override fun onPostInflate(savedViewState: Bundle?) {
        super.onPostInflate(savedViewState)
        button.setOnClickListener {
        }
    }


}

object OpenCameraCommand : Command

object ToMainControllerTransition : Transition