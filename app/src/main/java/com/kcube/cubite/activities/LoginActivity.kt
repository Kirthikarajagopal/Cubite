package com.kcube.cubite.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.kcube.cubite.R
import com.kcube.cubite.modals.User

@Suppress("DEPRECATION", "UNUSED_EXPRESSION")
class LoginActivity : AppCompatActivity() {
    lateinit var Employee_id: EditText
    lateinit var password: EditText
    lateinit var button: Button
    lateinit var forgotPassword :TextView
    lateinit var showPasswordCheckBox: CheckBox
    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("employees_list")
    private lateinit var progressDialog: AlertDialog;

    @SuppressLint("SuspiciousIndentation", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val dialogView = LayoutInflater.from(applicationContext).inflate(R.layout.progress_dialog_view, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        builder.setCancelable(false) // Make the dialog non-cancelable
        progressDialog = builder.create()

        val firestoreHelper = User()
        Employee_id = findViewById(R.id.Employee_Id)
        password = findViewById(R.id.password)
        button = findViewById(R.id.button)
        showPasswordCheckBox = findViewById(R.id.showPasswordCheckBox)
        forgotPassword = findViewById(R.id.forgotPassword)

        forgotPassword.setOnClickListener {
            val intent = Intent(applicationContext, ForgotPassword::class.java)
            startActivity(intent)
        }

        showPasswordCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Handle Show password
                password.transformationMethod = HideReturnsTransformationMethod.getInstance()
            }
            else {
                // Handle Hide password
                password.transformationMethod = PasswordTransformationMethod.getInstance()
            }
            password.setSelection(password.text.length)
        }

        button.setOnClickListener {
            checkUserIdExistOrNot()
        }
    }

    private fun checkUserIdExistOrNot() {
        val empID = Employee_id.text.toString()
        val empPassword = password.text.toString()
        if (empID.isEmpty() && empPassword.isEmpty()) {
            // Handle emp Id and Password Is Empty
            showSnackbar("Please enter both Employee ID and Password.")
        } else if (empID.isEmpty()) {
            // Handle emp Id Empty
            showSnackbar("Please enter Employee ID.")
        } else if (empPassword.isEmpty()) {
            // Handle emp Password Is Empty
            showSnackbar("Please enter Password.")
        } else {
            progressDialog.show()
            usersCollection.document(empID).get().addOnSuccessListener { results ->
                if (results.exists()) {
                    // Handel emp ID exist
                    progressDialog.dismiss()
                    val empDetails = results.data
                    val dbEmpId: Long = (empDetails?.get("emp_id") ?: 0) as Long
                    val dbEmpName = empDetails?.get("emp_name") ?: ""
                    val dbEmpPassword = empDetails?.get("emp_password") ?: ""
                    val userType: Long = (empDetails?.get("user_type") ?: 0) as Long

                    if (dbEmpId != empID.toLong() && dbEmpPassword != empPassword) {
                        // Handle Id and Password not presant in collection
                        showSnackbar("Invalid User Credentials")
                    } else if (dbEmpId != empID.toLong()) {
                        // Handle Id  not presant in collection
                        showSnackbar("Invalid Employee ID")
                    } else if (dbEmpPassword != empPassword) {
                        // Handle passwword not presant in collection
                        showSnackbar("Invalid Password")
                    } else {
                        // Handle Store emp_id ,emp_name, user_type in shared preference  & navigate to PinNumberActivity
                        val sharedPref =
                            getSharedPreferences("Kcube_User", Context.MODE_PRIVATE)
                        val editor: SharedPreferences.Editor = sharedPref.edit()
                        editor.putInt("emp_id", dbEmpId.toInt())
                        editor.putString("emp_name", dbEmpName.toString())
                        editor.putInt("user_type", userType.toInt())
                        editor.putBoolean("loginStatus", true)
                        editor.apply()
                        val intent = Intent(applicationContext, PinNumberActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    // Handle Emp Id Not exist
                    progressDialog.dismiss()
                    showSnackbar("Invalid User Credentials")
                }
            }.addOnFailureListener { exeception ->
                // Handle exeception
                progressDialog.dismiss()
            }
        }
    }

    private fun showSnackbar(message: String) {
        val rootView: View = findViewById(android.R.id.content)
        val snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_LONG)
        snackbar.show()
    }
    }