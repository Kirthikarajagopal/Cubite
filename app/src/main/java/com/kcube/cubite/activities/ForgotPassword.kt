package com.kcube.cubite.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.kcube.cubite.R

class ForgotPassword : AppCompatActivity() {
    private lateinit var uiEmployeeEdittext: EditText
    private lateinit var uiOldPassword: EditText
    private lateinit var uiNewPassword: EditText
    private lateinit var uiConfirmPassword: EditText
    private lateinit var uiResetPassword: Button
    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("employees_list")
    private var isPasswordValidated = false;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        supportActionBar?.hide()
        uiEmployeeEdittext = findViewById(R.id.uiForgotEmployeeId)
        uiOldPassword = findViewById(R.id.uiForgotOldPassword)
        uiNewPassword = findViewById(R.id.uiForgotNewPassword)
        uiConfirmPassword = findViewById(R.id.uiForgotConfirmPassword)
        uiResetPassword = findViewById(R.id.uiupdatePasswordBtn)

        uiResetPassword.setOnClickListener {
                resetPasswordValidation()
        }
    }

    private fun resetPasswordValidation() {
        // Handle Password Validation
        if(!isPasswordValidated){
            if (uiEmployeeEdittext.text.isEmpty()) {
                Toast.makeText(applicationContext, "Enter Employee Id", Toast.LENGTH_SHORT).show()
            } else if (uiOldPassword.text.isEmpty()) {
                Toast.makeText(applicationContext, "Enter Valid Old Password", Toast.LENGTH_SHORT)
                    .show()
            } else {
                usersCollection.document(uiEmployeeEdittext.text.toString()).get()
                    .addOnSuccessListener { results ->
                        if (results.exists()) {
                            val empDetails = results.data
                            val dbEmpId: Long = (empDetails?.get("emp_id") ?: 0) as Long
                            val dbEmpName = empDetails?.get("emp_name") ?: ""
                            val dbEmpPassword = empDetails?.get("emp_password") ?: ""
                            val userType: Long = (empDetails?.get("user_type") ?: 0) as Long
                            if (dbEmpPassword != uiOldPassword.text.toString()) {
                                Toast.makeText(
                                    applicationContext,
                                    "Invalid Password",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                isPasswordValidated = true
                                uiEmployeeEdittext.visibility = View.GONE
                                uiOldPassword.visibility = View.GONE
                                uiNewPassword.visibility = View.VISIBLE
                                uiConfirmPassword.visibility = View.VISIBLE
                            }
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Invalid Employee Id",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }.addOnFailureListener { exeception ->

                    }
            }
        }else{
            if(uiNewPassword.text.toString() != uiConfirmPassword.text.toString()){
                // Handle uiNewPassword not equals to uiConfirmPassword
                Toast.makeText(applicationContext, "Password does not match", Toast.LENGTH_SHORT).show()
            }else{
                usersCollection.document(uiEmployeeEdittext.text.toString())
                    .update("emp_password", uiConfirmPassword.text.toString())
                    .addOnSuccessListener {
                        // Handle change emp_password from firestore
                        val intent = Intent(this, LoginActivity::class.java)
                        startActivity(intent)
                        finish()
                        Toast.makeText(baseContext, "Password Updated", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(baseContext, "Password Update Failed", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
}