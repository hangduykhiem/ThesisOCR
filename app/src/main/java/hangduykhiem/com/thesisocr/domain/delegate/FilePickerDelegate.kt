package hangduykhiem.com.thesisocr.domain.delegate

import android.content.Intent
import android.net.Uri
import hangduykhiem.com.thesisocr.view.ActivityCallbacks
import hangduykhiem.com.thesisocr.view.BaseActivity
import javax.inject.Inject

class FilePickerDelegate @Inject constructor(
    val activity: BaseActivity
) {

    lateinit var onImagePickAction: (Uri) -> Unit


    companion object {
        const val REQUEST_IMAGE_FILE = 2
    }

    init {
        activity.registerCallbacks(object : ActivityCallbacks() {
            override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
                if (requestCode == CameraDelegate.REQUEST_IMAGE_CAPTURE) {
                    val uri = data?.data
                    if (uri != null) {
                        onImagePickAction(uri)
                    }
                }
            }
        })
    }

    fun pickImageFromFile() {

    }

}