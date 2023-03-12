package com.joshuawaheed.projemanag.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.joshuawaheed.projemanag.R

class SignInActivity : BaseActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_sign_in)

        auth = Firebase.auth

        val flagFullScreen = WindowManager.LayoutParams.FLAG_FULLSCREEN
        window.setFlags(flagFullScreen, flagFullScreen)

        setupActionBar()

        val btnSignIn: Button = findViewById(R.id.btn_sign_in)

        btnSignIn.setOnClickListener {
            signInRegisteredUser()
        }
    }

    private fun setupActionBar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar_sign_in_activity)

        setSupportActionBar(toolbar)

        val actionBar = supportActionBar

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun signInRegisteredUser() {
        val etEmail: EditText = findViewById(R.id.et_email_sign_in)
        val email: String = etEmail.text.toString().trim { it <= ' ' }

        val etPassword: EditText = findViewById(R.id.et_password_sign_in)
        val password: String = etPassword.text.toString().trim { it <= ' ' }

        if (validateForm(email, password)) {
            showProgressDialog(resources.getString(R.string.please_wait))

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) {
                    task ->
                    hideProgressDialog()

                    if (task.isSuccessful) {
                        Log.d("Sign in", "createUserWithEmail:success")
                        val user = auth.currentUser
                        startActivity(Intent(this@SignInActivity, MainActivity::class.java))
                    } else {
                        Log.w("Sign in", "createUserWithEmail:failure", task.exception)

                        Toast.makeText(
                            baseContext,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    private fun validateForm(email: String, password: String): Boolean {
        return when {
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