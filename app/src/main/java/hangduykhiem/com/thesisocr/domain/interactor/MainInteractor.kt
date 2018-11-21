package hangduykhiem.com.thesisocr.domain.interactor

import com.wolt.tacotaco.Interactor
import com.wolt.tacotaco.components.Command
import com.wolt.tacotaco.components.Model
import hangduykhiem.com.thesisocr.helper.NoArg
import hangduykhiem.com.thesisocr.view.controller.OpenCameraCommand
import hangduykhiem.com.thesisocr.view.delegate.CameraDelegate
import java.io.IOException
import javax.inject.Inject

class MainInteractor @Inject constructor(
    val cameraDelegate: CameraDelegate
) : Interactor<NoArg, MainModel>() {

    override fun onAttach(restored: Boolean) {
        super.onAttach(restored)
        updateModel(MainModel())
    }

    override fun handleCommand(command: Command) {
        when (command) {
            OpenCameraCommand -> openCamera()
        }
    }

    private fun openCamera() {
        try {
            cameraDelegate.takePicture()
        } catch (e: IOException) {

        }
    }

}

data class MainModel(
    val pictureURL: String? = null
) : Model