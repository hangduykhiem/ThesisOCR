package hangduykhiem.com.thesisocr.domain.delegate

import android.Manifest
import android.content.pm.PackageManager
import android.support.v4.app.ActivityCompat
import hangduykhiem.com.thesisocr.view.ActivityCallbacks
import hangduykhiem.com.thesisocr.view.BaseActivity
import javax.inject.Inject

class PermissionDelegate @Inject constructor(
    val activity: BaseActivity
) {

    companion object {
        const val PERMISSION_REQUEST = 1
    }

    fun requestStoragePermission(callback: (Boolean) -> Unit) {
        activity.registerCallbacks(object : ActivityCallbacks() {
            override fun onRequestPermissionsResult(
                requestCode: Int,
                permissions: Array<String>,
                grantResults: IntArray
            ) {
                if (requestCode == PERMISSION_REQUEST) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED
                    ) {
                        callback(true)
                    } else {
                        callback(false)
                    }
                }
            }
        }
        )
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
            PERMISSION_REQUEST
        )
    }

}