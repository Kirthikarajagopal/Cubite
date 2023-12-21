package com.kcube.cubite.activities

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kcube.cubite.R


class PinNumberActivity : AppCompatActivity() {
    private lateinit var pinBox1: EditText
    private lateinit var pinBox2: EditText
    private lateinit var pinBox3: EditText
    private lateinit var pinBox4: EditText
    private lateinit var conformPinBox1: EditText
    private lateinit var conformPinBox2: EditText
    private lateinit var conformPinBox3: EditText
    private lateinit var conformPinBox4: EditText
    private lateinit var pinSubmitButton:Button

    @SuppressLint("MissingInflatedId", "CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin_number)

        supportActionBar?.hide()
        pinBox1 = findViewById(R.id.pinBox1)
        pinBox2 = findViewById(R.id.pinBox2)
        pinBox3 = findViewById(R.id.pinBox3)
        pinBox4 = findViewById(R.id.pinBox4)

        conformPinBox1 = findViewById(R.id.conformPinBox1)
        conformPinBox2 = findViewById(R.id.conformPinBox2)
        conformPinBox3 = findViewById(R.id.conformPinBox3)
        conformPinBox4 = findViewById(R.id.conformPinBox4)

        pinBox1.addTextChangedListener(MyTextWatcher(pinBox1))
        pinBox2.addTextChangedListener(MyTextWatcher(pinBox2))
        pinBox3.addTextChangedListener(MyTextWatcher(pinBox3))
        pinBox4.addTextChangedListener(MyTextWatcher(pinBox4))
        conformPinBox1.addTextChangedListener(MyTextWatcher(conformPinBox1))
        conformPinBox2.addTextChangedListener(MyTextWatcher(conformPinBox2))
        conformPinBox3.addTextChangedListener(MyTextWatcher(conformPinBox3))
        conformPinBox4.addTextChangedListener(MyTextWatcher(conformPinBox4))
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        pinSubmitButton = findViewById(R.id.pinSubmitButton)

        pinSubmitButton.setOnClickListener {
           checkPinNumber()
        }
    }

    private fun checkPinNumber() {
        // Here Handle check pin number equals to confirmPin
        if (pinBox1.text.toString().isNotEmpty() && pinBox2.text.toString()
                .isNotEmpty() && pinBox3.text.toString().isNotEmpty() && pinBox4.text.toString()
                .isNotEmpty() && conformPinBox1.text.toString()
                .isNotEmpty() && conformPinBox2.text.toString()
                .isNotEmpty() && conformPinBox3.text.toString()
                .isNotEmpty() && conformPinBox4.text.toString().isNotEmpty()
        ) {
            // Here Handle check pin number and confirmPin not empty
            var pin =
                pinBox1.text.toString() + pinBox2.text.toString() + pinBox3.text.toString() + pinBox4.text.toString();
            var confirmPin =
                conformPinBox1.text.toString() + conformPinBox2.text.toString() + conformPinBox3.text.toString() + conformPinBox4.text.toString();
            if (pin.equals(confirmPin)) {
                // Here Handle check pin number equals to confirmPin
                val sharedPref = getSharedPreferences("Kcube_User", Context.MODE_PRIVATE)
                val editor: SharedPreferences.Editor = sharedPref.edit()
                editor.putString("confirmPin", confirmPin)
                editor.putBoolean("loginStatus", true)
                editor.apply()
                val intent = Intent(applicationContext, PinLogIn::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(
                    baseContext,
                    "Pin & Confirm Pin Does Not Match",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private inner class MyTextWatcher(private val editText: EditText) : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // This method is called before the text changes
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // Handle EditText changes
            when (editText.id) {
                R.id.pinBox1 -> {
                    if (s != null && s.length == 1) {
                        pinBox2.text.clear()
                        pinBox2.requestFocus()
                    }
                }

                R.id.pinBox2 -> {
                    if (s != null && s.length == 1) {
                        pinBox3.text.clear()
                        pinBox3.requestFocus()
                    }
                }

                R.id.pinBox3 -> {
                    if (s != null && s.length == 1) {
                        pinBox4.text.clear()
                        pinBox4.requestFocus()
                    }
                }

                R.id.pinBox4 -> {
                    if (s != null && s.length == 1) {
                        conformPinBox1.text.clear()
                        conformPinBox1.requestFocus()
                    }
                }

                R.id.conformPinBox1 -> {
                    if (s != null && s.length == 1) {
                        conformPinBox2.text.clear()
                        conformPinBox2.requestFocus()
                    }
                }

                R.id.conformPinBox2 -> {
                    if (s != null && s.length == 1) {
                        conformPinBox3.text.clear()
                        conformPinBox3.requestFocus()
                    }

                }

                R.id.conformPinBox3 -> {
                    if (s != null && s.length == 1) {
                        conformPinBox4.text.clear()
                        conformPinBox4.requestFocus()
                    }
                }
            }
        }
        override fun afterTextChanged(p0: Editable?) {
        }
    }
}