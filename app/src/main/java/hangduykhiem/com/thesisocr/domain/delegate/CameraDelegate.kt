package hangduykhiem.com.thesisocr.domain.delegate

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.MediaStore.ACTION_IMAGE_CAPTURE
import hangduykhiem.com.thesisocr.view.ActivityCallbacks
import hangduykhiem.com.thesisocr.view.BaseActivity
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class CameraDelegate @Inject constructor(
    val activity: BaseActivity
) {

    lateinit var onPhotoTakenAction: (String) -> Unit
    var photoUri: Uri? = null

    companion object {
        const val REQUEST_IMAGE_CAPTURE = 1
        const val SAVED_PHOTO_URI = "CameraDelegateSavedPhotoUri"
    }

    init {
        activity.registerCallbacks(object : ActivityCallbacks() {
            override fun onSaveInstanceState(outState: Bundle) {
                outState.putParcelable(SAVED_PHOTO_URI, photoUri)
            }

            override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
                if (requestCode == REQUEST_IMAGE_CAPTURE) {
                    if (photoUri == null) {
                        photoUri = activity.savedInstanceState?.getParcelable(SAVED_PHOTO_URI)
                    }
                    if (resultCode == Activity.RESULT_OK) {
                        onPhotoTakenAction.invoke(photoUri.toString())
                    }
                }
            }

        })
    }

    @Throws(IOException::class)
    fun takePicture() {
        val imageFile = createTempImageFile();
        val takePictureIntent = Intent(ACTION_IMAGE_CAPTURE)
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageFile.absolutePath)
        if (takePictureIntent.resolveActivity(activity.packageManager) != null) {
            activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }

    @Throws(IOException::class)
    private fun createTempImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "thesisOCR_" + timeStamp + "_"
        val storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName, /* prefix */
            ".jpg", /* suffix */
            storageDir      /* directory */
        )
        return image
    }

}