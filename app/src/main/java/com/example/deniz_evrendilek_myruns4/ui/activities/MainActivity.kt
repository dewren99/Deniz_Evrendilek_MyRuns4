package com.example.deniz_evrendilek_myruns4.ui.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.example.deniz_evrendilek_myruns4.R
import com.example.deniz_evrendilek_myruns4.constants.ExerciseTypes
import com.example.deniz_evrendilek_myruns4.constants.InputTypes
import com.example.deniz_evrendilek_myruns4.constants.PermissionRequestCodes
import com.example.deniz_evrendilek_myruns4.services.TrackingService
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

        // App is run fresh for the first time
        if (savedInstanceState == null) {
            handleOnNotificationClick(intent)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        // Don't call here otherwise map gets recreated
        // when it's already open
//         handleOnNotificationClick(intent)
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
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                this, android.Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestTrackingServicePermissions()
        }
    }

    private fun requestTrackingServicePermissions() {
        val permissions = mutableListOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(android.Manifest.permission.POST_NOTIFICATIONS)
        }
        ActivityCompat.requestPermissions(
            this, permissions.toTypedArray(), PermissionRequestCodes.PERMISSION_TRACKING_SERVICE
        )
    }

    /**
     * Handles the Intent received created from TrackingService.
     * Extract EXERCISE_TYPE_ID and INPUT_TYPE_ID, pass it to
     * mapFragment on navigation.
     */
    private fun handleOnNotificationClick(intent: Intent?) {
        if (intent?.extras == null) {
            return
        }
        if (intent.action != TrackingService.ON_NOTIFICATION_CLICK_ACTION) {
            return
        }

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        val exerciseTypeId = intent.getIntExtra(
            "EXERCISE_TYPE_ID", ExerciseTypes.EXERCISE_TYPE_UNKNOWN_ID
        )
        val inputTypeId = intent.getIntExtra(
            "INPUT_TYPE_ID", InputTypes.INPUT_TYPE_UNKNOWN_ID
        )
        val bundle = bundleOf(
            "EXERCISE_TYPE_ID" to exerciseTypeId, "INPUT_TYPE_ID" to inputTypeId
        )
        navController.navigate(R.id.action_notification_to_mapFragment, bundle)
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