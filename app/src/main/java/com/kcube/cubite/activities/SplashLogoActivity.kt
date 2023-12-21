package com.kcube.cubite.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.kcube.cubite.R


@Suppress("DEPRECATION")
class SplashLogoActivity : AppCompatActivity() {
    companion object {
        private const val LOCATION_SETTINGS_REQUEST_CODE = 1001
    }

    var loginStatus = false
    var userType = 0

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_logo)

        supportActionBar?.hide()
        val sharedPref = getSharedPreferences("Kcube_User", Context.MODE_PRIVATE)
        loginStatus = sharedPref.getBoolean("loginStatus", false)
        userType = sharedPref.getInt("user_type", 0)
        Handler().postDelayed({
            // Finding user location near by company location
            checkAndRedirectToLocationSettings(this)
        }, 1000)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun checkAndRedirectToLocationSettings(context: Context) {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (!isLocationEnabled) {
            // Handle Location Is Enable
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivityForResult(intent, LOCATION_SETTINGS_REQUEST_CODE)
        } else {
            // Handle Location Is Not Enable
           handleUsersFeatures()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == LOCATION_SETTINGS_REQUEST_CODE) {
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val isLocationEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            // Finding user location Enable or not
            if (isLocationEnabled) {
                // handle Location is Enable
                handleUsersFeatures()
            } else {
                // Handle Location is Not Enable
                Toast.makeText(
                    applicationContext,
                    "Please enable location services",
                    Toast.LENGTH_SHORT
                ).show()
                checkAndRedirectToLocationSettings(this)
            }
        }
    }

    private fun handleUsersFeatures() {
        // check User Id already exist from
        if (loginStatus) {
            // Handel already exist
            val intent = Intent(applicationContext, PinLogIn::class.java)
            startActivity(intent)
            finish()
        } else {
            // handel not exist
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Optional: Finish the current activity navigate to Login Activity .
        }
    }
}