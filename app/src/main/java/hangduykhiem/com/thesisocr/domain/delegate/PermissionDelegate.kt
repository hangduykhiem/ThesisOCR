package hangduykhiem.com.thesisocr.domain.delegate

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

    fun checkAndRequestPermission(permission: String, callback: (Boolean) -> Unit) {
        if (checkPermission(permission)) {
            callback(true)
        } else {
            requestPermission(permission, callback)
        }
    }

    private fun checkPermission(permission: String): Boolean {
        return activity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission(permission: String, callback: (Boolean) -> Unit) {
        activity.registerCallbacks(object : ActivityCallbacks() {
            override fun onRequestPermissionsResult(
                    requestCode: Int,
                    permissions: Array<String>,
                    grantResults: IntArray
            ) {
                if (requestCode == PERMISSION_REQUEST) {
                    if (!grantResults.isEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        callback(true)
                    } else {
                        callback(false)
                    }
                }
            }
        })

        ActivityCompat.requestPermissions(
                activity,
                arrayOf(permission),
                PERMISSION_REQUEST
        )
    }

}