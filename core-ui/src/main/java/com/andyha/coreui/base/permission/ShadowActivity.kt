package com.andyha.coreui.base.permission

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.andyha.coredata.storage.preference.AppSharedPreference
import com.andyha.coredata.storage.preference.didShowRationale
import com.andyha.coredata.storage.preference.setShowRationale
import com.andyha.coreextension.isAndroidROrHigher
import com.andyha.coreextension.toJson
import com.andyha.coreui.base.ui.widget.dialog.OptionsDialogFragment
import com.andyha.coreutils.R
import com.andyha.coreutils.permission.RxPermission
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class ShadowActivity : AppCompatActivity() {
    lateinit var permissions: Array<String>
    var reasonTitle: String? = null
    var reasonDesc: String? = null
    private var isShown = false

    @Inject
    lateinit var prefs: AppSharedPreference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            handleIntent(intent)
        }
    }

    override fun onNewIntent(intent: Intent) {
        handleIntent(intent)
        super.onNewIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        permissions = intent.getStringArrayExtra(ARG_PERMISSIONS) ?: emptyArray()

        reasonTitle = (intent.getStringArrayExtra(ARG_REASON) as? Array<String>)?.get(0)
        reasonDesc = (intent.getStringArrayExtra(ARG_REASON) as? Array<String>)?.get(1)

        if (isRequested(permissions)) {
            setPermissionResult(true)
            return
        }

        if (reasonTitle != null && reasonDesc != null)
            showReasonDialog()
        else
            requestPermissions(permissions, PERMISSIONS_REQUEST_CODE)
    }

    override fun onResume() {
        Timber.d("onResume")
        super.onResume()
        // Process for case open setting android and change permission
        if (isShown) {
            isShown = false
            if (!hasPermissions(*permissions)) {
                requestPermissions(permissions, PERMISSIONS_REQUEST_CODE)
            } else {
                finish()
            }
        }
    }

    override fun finish() {
        super.finish()
        // Reset the animation to avoid flickering.
        overridePendingTransition(0, 0)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            val rationales = rationales(permissions)
            Timber.d("Rationales: ${rationales.toJson()}")
            for (i in grantResults.indices) {
                val grantResult = grantResults[i]
                val rationale = rationales[i]
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    if (isAndroidROrHigher()) {
                        // in android 11 and above, permission will not be asked again if
                        // shouldShowRequestPermissionRationale() has already returned true once
                        if (!rationale && prefs.didShowRationale(permissions[i])) {
                            finish()
                            onDenyForever?.invoke()
                            return
                        } else {
                            prefs.setShowRationale(permissions[i], rationale)
                        }
                    } else {
                        //permission not allowed to be asked again in Android 10 and below
                        if (!rationale) {
                            finish()
                            onDenyForever?.invoke()
                            return
                        }
                    }
                    setPermissionResult(false)
                    return
                }
            }
            setPermissionResult(true)
        }
    }


    private fun rationales(permissions: Array<String>): BooleanArray {
        val rationales = BooleanArray(permissions.size)
        for (i in permissions.indices) {
            shouldShowRequestPermissionRationale(permissions[i]).also {
                rationales[i] = it
            }

        }
        return rationales
    }

    private fun showReasonDialog() {
        OptionsDialogFragment.showDialog(
            supportFragmentManager,
            cancelable = false,
            title = reasonTitle,
            message = reasonDesc,
            option1ResId = R.string.common_btn_do_not_allow,
            option2ResId = R.string.common_btn_ok,
            onOption1Clicked = { setPermissionResult(false) },
            onOption2Clicked = { requestPermissions(permissions, PERMISSIONS_REQUEST_CODE) }
        )
    }

    private fun setPermissionResult(result: Boolean) {
        finish()
        RxPermission.getInstance(application).onPermissionResult(result)
    }

    private fun setShown(shown: Boolean) {
        isShown = shown
    }

    private fun hasPermissions(vararg permissions: String): Boolean {
        for (permission in permissions) if (PackageManager.PERMISSION_GRANTED != checkSelfPermission(
                permission
            )
        ) return false
        return true
    }

    private fun isRequested(permissions: Array<String>): Boolean {
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    applicationContext,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            )
                return false
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        RxPermission.clear()
        onDenyForever = null
    }

    companion object {
        private const val ARG_PERMISSIONS = "permissions"
        private const val ARG_REASON = "reason"
        private const val PERMISSIONS_REQUEST_CODE = 42

        private var onDenyForever: (() -> Unit)? = null

        fun start(
            context: Context,
            permissions: Array<String>,
            reasonTitle: String?,
            reasonDesc: String?,
            onDenyForever: (() -> Unit)? = null
        ) {

            val intent = Intent(context, ShadowActivity::class.java)
                .putExtra(ARG_PERMISSIONS, permissions)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            if (reasonTitle != null && reasonDesc != null) {
                intent.putExtra(ARG_REASON, arrayOf(reasonTitle, reasonDesc))
            }

            Companion.onDenyForever = onDenyForever

            context.startActivity(intent)
        }
    }
}