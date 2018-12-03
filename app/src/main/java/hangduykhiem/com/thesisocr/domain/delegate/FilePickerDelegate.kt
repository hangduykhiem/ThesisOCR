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
                if (requestCode == REQUEST_IMAGE_FILE) {
                    val uri = data?.data
                    if (uri != null) {
                        onImagePickAction(uri)
                    }
                }
            }
        })
    }

    fun pickImageFromFile() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        activity.startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_IMAGE_FILE)
    }

}