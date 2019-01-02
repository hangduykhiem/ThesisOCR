package hangduykhiem.com.thesisocr.view.controller

import android.os.Bundle
import android.widget.TextView
import com.wolt.tacotaco.components.Command
import com.wolt.tacotaco.components.Transition
import hangduykhiem.com.thesisocr.R
import hangduykhiem.com.thesisocr.di.modules.ControllerModule
import hangduykhiem.com.thesisocr.domain.interactor.ScanInteractor
import hangduykhiem.com.thesisocr.domain.interactor.MainModel
import hangduykhiem.com.thesisocr.helper.NoArg
import hangduykhiem.com.thesisocr.view.MainActivity
import javax.inject.Inject

class ScanController : BaseController<NoArg, MainModel>(NoArg) {

    override val layoutId = R.layout.controller_scan
    val tvCameraButton: TextView by bindView(R.id.tvCameraButton)
    val tvFileButton: TextView by bindView(R.id.tvFileButton)

    @Inject
    override lateinit var interactor: ScanInteractor


    override fun inject() {
        (activity as MainActivity).component.plus(ControllerModule(this)).inject(this)
    }

    override fun onPostInflate(savedViewState: Bundle?) {
        super.onPostInflate(savedViewState)
        tvCameraButton.setOnClickListener {
            sendCommand(OpenCameraCommand)
        }
        tvFileButton.setOnClickListener{
            sendCommand(OpenFileCommand)
        }
    }

}

object OpenCameraCommand : Command
object OpenFileCommand : Command
object ToScanControllerTransition : Transition