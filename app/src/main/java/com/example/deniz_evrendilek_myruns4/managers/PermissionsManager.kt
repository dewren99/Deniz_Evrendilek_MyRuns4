package com.example.deniz_evrendilek_myruns4.managers

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment

class PermissionsManager(private val fragment: Fragment) {

    private fun hasPermission(permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(
            fragment.requireActivity(), permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermission(permission: String, requestCode: Int) {
        @Suppress("DEPRECATION")
        fragment.requestPermissions(arrayOf(permission), requestCode)
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        @Suppress("UNUSED_PARAMETER") ignoredPermissions: Array<out String>,
        grantResults: IntArray,
        onPermissionGranted: (requestCode: Int) -> Unit,
        onPermissionDenied: (requestCode: Int) -> Unit
    ) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            onPermissionGranted(requestCode)
        } else {
            onPermissionDenied(requestCode)
        }
    }

    fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        actionMap: Map<Int, (data: Intent?) -> Unit>,
    ) {
        if (resultCode != Activity.RESULT_OK) {
            return
        }

        actionMap[requestCode]?.invoke(data)
    }

    fun hasCameraPermission(): Boolean {
        return hasPermission(android.Manifest.permission.CAMERA)
    }

    fun hasReadStoragePermission(): Boolean {
        return hasPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    fun hasWriteStoragePermission(): Boolean {
        return hasPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }
}
