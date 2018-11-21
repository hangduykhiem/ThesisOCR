package hangduykhiem.com.thesisocr.domain.interactor

import com.wolt.tacotaco.Interactor
import com.wolt.tacotaco.components.Command
import com.wolt.tacotaco.components.Model
import hangduykhiem.com.thesisocr.helper.NoArg
import hangduykhiem.com.thesisocr.view.controller.OpenCameraCommand
import javax.inject.Inject

class MainInteractor @Inject constructor() : Interactor<NoArg, MainModel>() {

    override fun onAttach(restored: Boolean) {
        super.onAttach(restored)
        updateModel()
    }

    override fun handleCommand(command: Command) {
        when (command) {
            OpenCameraCommand -> openCamera()
        }
    }

    private fun openCamera() {

    }

}

data class MainModel(
    val pictureURL: String? = null
) : Model