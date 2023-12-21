package com.kcube.cubite.activities


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.firestore.FirebaseFirestore
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import com.kcube.cubite.R
import java.util.Calendar

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("employees_list")
    private lateinit var sharedPreferences: SharedPreferences
    private var userType : Int = 0
    private lateinit var sessionTextView : TextView
    private lateinit var userName :TextView
    private lateinit var logoutImage : ImageView
    private lateinit var manageButton : ConstraintLayout
    private lateinit var snackButton : ConstraintLayout
    private lateinit var publishSnacksButton : ConstraintLayout
    private lateinit var postSnacksButton : ConstraintLayout
    private lateinit var constrainSnack : ConstraintLayout
    private lateinit var constrainManageSnacks : ConstraintLayout
    @SuppressLint("SuspiciousIndentation", "SetTextI18n", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        // Initialize sharedPreferences
        sharedPreferences = getSharedPreferences("Kcube_User", Context.MODE_PRIVATE)

        logoutImage = findViewById(R.id.uilogoutImage)
        manageButton = findViewById(R.id.manageButton)
        snackButton = findViewById(R.id.snackButton)
        publishSnacksButton = findViewById(R.id.publishSnacksButton)
        postSnacksButton = findViewById(R.id.postSnacksButton)
        constrainSnack = findViewById(R.id.uiConstrainManageSnackCoordinator)
        constrainManageSnacks = findViewById(R.id.uiConstrainManageSnack)
        userName = findViewById(R.id.userName)
        sessionTextView = findViewById(R.id.session)

        // Handle greetings based on the session
        sessionTextView.text = "Good ${getTimeOfDay()}"

        // If you want to logout currrent account click logoutImage icon
        logoutImage.setOnClickListener {
            logOutfunction()
        }

        userName.visibility = View.GONE
        manageButton.visibility = View.GONE
        snackButton.visibility = View.GONE
        publishSnacksButton.visibility = View.GONE
        postSnacksButton.visibility = View.GONE

        userType = sharedPreferences.getInt("user_type", 0)
        val empName = sharedPreferences.getString("emp_name", "")
        if (empName != null) {
            userName.text = "$empName "
        }

        if (userType != null) {
            if (userType == 1) {
                // Handel if user Type is 1
                userName.visibility = View.VISIBLE
                manageButton.visibility = View.VISIBLE
                snackButton.visibility = View.VISIBLE
                publishSnacksButton.visibility = View.VISIBLE
                postSnacksButton.visibility = View.VISIBLE

            } else if (userType == 2 || userType ==4) {
                // Handel if user Type is 2 or 4
                userName.visibility = View.VISIBLE
                constrainSnack.visibility = View.GONE
                constrainManageSnacks.setBackgroundColor(Color.parseColor("#ffe6c7"))
                snackButton.visibility = View.VISIBLE
                publishSnacksButton.visibility = View.VISIBLE
                postSnacksButton.visibility = View.VISIBLE
            }
        }

        manageButton.setOnClickListener {
            // Navigate to SnackCoordinate Activity .
            val intent = Intent(this, SnackCoordinate::class.java)
            startActivity(intent)

        }
        snackButton.setOnClickListener {
            // Navigate to ManageSnacks Activity .
            val intent = Intent(this, ManageSnacks::class.java)
            startActivity(intent)
        }

        publishSnacksButton.setOnClickListener {
            // Navigate to PublishSnacks Activity
            val intent = Intent(this, PublishSnacks::class.java)
            startActivity(intent)
        }
        postSnacksButton.setOnClickListener {
            if (userType ==4){
                // Navigate to SnacksPurchaser Activity
                val intent = Intent(this, SnacksPurchaser::class.java)
                startActivity(intent)
            }else{
                // Navigate to PostSnackActivity
                val intent = Intent(this, PostSnackActivity::class.java)
                startActivity(intent)
            }
        }
    }


    private fun logOutfunction() {
        // Create an AlertDialog.Builder
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Logout")
        alertDialogBuilder.setMessage("Are you sure you want to log out?")
        alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
            // Handle the dialog If "Yes" is clicked
            val editor: SharedPreferences.Editor = sharedPreferences.edit()
            editor.clear()
            editor.apply()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        alertDialogBuilder.setNegativeButton("No") { dialog, _ ->
            // Dismiss the dialog if "No" is clicked
            dialog.dismiss()
        }

        // Create and show the AlertDialog
        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    fun getTimeOfDay(): String {
        val calendar = Calendar.getInstance()
        val currentHour = calendar.get(Calendar.HOUR_OF_DAY)

        return when {
            currentHour in 0..11 -> "Morning"
            currentHour in 12..16 -> "Afternoon"
            else -> "Evening"
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_logout -> {
                // Create an AlertDialog.Builder
                val alertDialogBuilder = AlertDialog.Builder(this)
                alertDialogBuilder.setTitle("Logout")
                alertDialogBuilder.setMessage("Are you sure you want to log out?")
                alertDialogBuilder.setPositiveButton("Yes") { _, _ ->
                    // Handle the dialog If "Yes" is clicked
                    val editor: SharedPreferences.Editor = sharedPreferences.edit()
                    editor.clear()
                    editor.apply()

                    // Navigate back to the login screen
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish() // Finish the MainActivity so the user cannot navigate back without logging in
                }
                alertDialogBuilder.setNegativeButton("No") { dialog, _ ->
                    // Dismiss the dialog if "No" is clicked
                    dialog.dismiss()
                }

                // Create and show the AlertDialog
                val alertDialog = alertDialogBuilder.create()
                alertDialog.show()

                return true

            }
            // Handle other menu items if needed
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun runtimeLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        }
    }
}