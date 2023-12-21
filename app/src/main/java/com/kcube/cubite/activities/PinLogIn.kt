package com.kcube.cubite.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kcube.cubite.R

class PinLogIn : AppCompatActivity() {
    private lateinit var pinBox1: EditText
    private lateinit var pinBox2: EditText
    private lateinit var pinBox3: EditText
    private lateinit var pinBox4: EditText

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin_log_in)
        supportActionBar?.hide()
        pinBox1 = findViewById(R.id.uipinBox1)
        pinBox2 = findViewById(R.id.uipinBox2)
        pinBox3 = findViewById(R.id.uipinBox3)
        pinBox4 = findViewById(R.id.uipinBox4)
        
        pinBox1.addTextChangedListener(MyTextWatcher(pinBox1))
        pinBox2.addTextChangedListener(MyTextWatcher(pinBox2))
        pinBox3.addTextChangedListener(MyTextWatcher(pinBox3))
        pinBox4.addTextChangedListener(MyTextWatcher(pinBox4))
        pinBox1.requestFocus()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
    }

    private inner class MyTextWatcher(private val editText: EditText) : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // This method is called before the text changes
        }
        var enteredPin = " "
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            when (editText.id) {
                R.id.uipinBox1 -> {
                    if(s != null && s.length == 1){
//                        pinBox2.text.clear()
//                        pinBox2.requestFocus()
                        // Add the entered digit to the PIN
                        enteredPin = s.toString()
                        pinBox2.requestFocus()
                    } else {
                        // If the user clears the digit, update the enteredPin
                        enteredPin = ""
                    }
                }
                R.id.uipinBox2 -> {
                    if(s != null && s.length == 1){
//                        pinBox3.text.clear()
//                        pinBox3.requestFocus()
                        enteredPin += s
                        pinBox3.requestFocus()
                    } else {
                        // If the user clears the digit, update the enteredPin
                        enteredPin = enteredPin.dropLast(1)
                    }

                }
                R.id.uipinBox3 -> {
                    if(s != null && s.length == 1){
//                        pinBox4.text.clear()
                        enteredPin += s
                        pinBox4.requestFocus()
                    }else {
                        // If the user clears the digit, update the enteredPin
                        enteredPin = enteredPin.dropLast(1)
                    }
                }
                R.id.uipinBox4 -> {
                        val sharedPref = getSharedPreferences("Kcube_User", Context.MODE_PRIVATE)
                        val confirmPin = sharedPref.getString("confirmPin", "EMPTY")
                        val userType = sharedPref.getInt("user_type", 0)
                        var pin =
                            pinBox1.text.toString() + pinBox2.text.toString() + pinBox3.text.toString() + pinBox4.text.toString()
                        if (pin == confirmPin) {
                            // Handle pin is equals to confirmPin
                            if (userType == 3) {
                                val intent = Intent(applicationContext, PostSnackActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                val intent = Intent(applicationContext, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                        } else {
                            Toast.makeText(baseContext, "INVALID PIN", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        override fun afterTextChanged(s: Editable?) {
            // This method is called after the text changes
        }
    }
}