package hangduykhiem.com.thesisocr.domain.interactor

import com.wolt.tacotaco.Interactor
import com.wolt.tacotaco.components.Command
import com.wolt.tacotaco.components.Model
import hangduykhiem.com.thesisocr.domain.delegate.CameraDelegate
import hangduykhiem.com.thesisocr.domain.delegate.FilePickerDelegate
import hangduykhiem.com.thesisocr.helper.NoArg
import hangduykhiem.com.thesisocr.view.controller.OpenCameraCommand
import hangduykhiem.com.thesisocr.view.controller.OpenFileCommand
import hangduykhiem.com.thesisocr.view.controller.ToResultControllerTranstion
import java.io.IOException
import javax.inject.Inject

class MainInteractor @Inject constructor(
    val cameraDelegate: CameraDelegate,
    val filePickerDelegate: FilePickerDelegate
) : Interactor<NoArg, MainModel>() {

    override fun onAttach(restored: Boolean) {
        super.onAttach(restored)
        updateModel(MainModel())
    }

    override fun handleCommand(command: Command) {
        when (command) {
            OpenCameraCommand -> openCamera()
            OpenFileCommand -> openFilePicker()
        }
    }

    private fun openCamera() {
        try {
            cameraDelegate.onPhotoTakenAction = {
                navigate(ToResultControllerTranstion(ResultArgs(uri = it)))
            }
            cameraDelegate.takePicture()
        } catch (e: IOException) {

        }
    }

    private fun openFilePicker() {
        filePickerDelegate.onImagePickAction = {
            navigate(ToResultControllerTranstion(ResultArgs(uri = it)))
        }
        filePickerDelegate.pickImageFromFile()
    }

}

data class MainModel(
    val pictureURL: String? = null
) : Model