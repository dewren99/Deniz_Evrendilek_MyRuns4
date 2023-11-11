package com.example.deniz_evrendilek_myruns4.ui.activities

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.deniz_evrendilek_myruns4.R
import com.example.deniz_evrendilek_myruns4.constants.ExerciseTypes
import com.example.deniz_evrendilek_myruns4.constants.InputTypes
import com.example.deniz_evrendilek_myruns4.constants.PermissionRequestCodes
import com.example.deniz_evrendilek_myruns4.ui.viewmodel.StartFragmentViewModel
import com.example.deniz_evrendilek_myruns4.utils.DateTimeUtils

class MainActivity : AppCompatActivity() {
    private lateinit var startFragmentViewModel: StartFragmentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initGlobal()

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbar.title = resources.getString(R.string.myruns)
        setSupportActionBar(toolbar)
        checkTrackingServicePermissions()
    }

    private fun initGlobal() {
        InputTypes.init(this)
        ExerciseTypes.init(this)
        DateTimeUtils.init(this)
        startFragmentViewModel = ViewModelProvider(this)[StartFragmentViewModel::class.java]
    }

    private fun checkTrackingServicePermissions() {
        if (ActivityCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, android.Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestTrackingServicePermissions()
        }
    }

    private fun requestTrackingServicePermissions() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.POST_NOTIFICATIONS
            ), PermissionRequestCodes.PERMISSION_TRACKING_SERVICE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PermissionRequestCodes.PERMISSION_TRACKING_SERVICE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    checkTrackingServicePermissions()
                }
                return
            }
        }
    }
}