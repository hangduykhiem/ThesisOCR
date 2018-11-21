package hangduykhiem.com.thesisocr.view.delegate

import android.app.Activity
import android.content.Intent
import android.provider.MediaStore.ACTION_IMAGE_CAPTURE
import javax.inject.Inject


class CameraDelegate @Inject constructor(
    val activity: Activity
) {

    companion object {
        const val REQUEST_IMAGE_CAPTURE = 1
    }


    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        }
    }


}