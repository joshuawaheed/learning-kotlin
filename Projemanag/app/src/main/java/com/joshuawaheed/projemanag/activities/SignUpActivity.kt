package com.joshuawaheed.projemanag.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.joshuawaheed.projemanag.R

class SignUpActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        val flagFullScreen = WindowManager.LayoutParams.FLAG_FULLSCREEN
        window.setFlags(flagFullScreen, flagFullScreen)
        setupActionBar()
    }

    private fun setupActionBar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar_sign_up_activity)

        setSupportActionBar(toolbar)

        val actionBar = supportActionBar

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val btnSignUp: Button = findViewById(R.id.btn_sign_up)

        btnSignUp.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {
        val etName: EditText = findViewById(R.id.et_name)
        val name: String = etName.text.toString().trim { it <= ' ' }

        val etEmail: EditText = findViewById(R.id.et_email)
        val email: String = etEmail.text.toString().trim { it <= ' ' }

        val etPassword: EditText = findViewById(R.id.et_password)
        val password: String = etPassword.text.toString().trim { it <= ' ' }

        if (validateForm(email, name, password)) {
            showProgressDialog(resources.getString(R.string.please_wait))

            FirebaseAuth
                .getInstance()
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    task ->
                    hideProgressDialog()

                    if (task.isSuccessful) {
                        val firebaseUser: FirebaseUser = task.result!!.user!!
                        val registeredEmail = firebaseUser.email!!

                        Toast.makeText(
                            this@SignUpActivity,
                            "$name you have successfully registered " +
                            "the email address $registeredEmail",
                            Toast.LENGTH_LONG
                        ).show()

                        FirebaseAuth.getInstance().signOut()

                        finish()
                    } else {
                        Toast.makeText(
                            this@SignUpActivity,
                            task.exception!!.message,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }
    }

    private fun validateForm(name: String, email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(name) -> {
                showErrorSnackBar("Please enter a name")
                false
            }
            TextUtils.isEmpty(email) -> {
                showErrorSnackBar("Please enter an email address")
                false
            }
            TextUtils.isEmpty(password) -> {
                showErrorSnackBar("Please enter a password")
                false
            }
            else -> {
                true
            }
        }
    }
}