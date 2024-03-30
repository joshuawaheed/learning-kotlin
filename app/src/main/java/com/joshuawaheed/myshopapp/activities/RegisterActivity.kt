package com.joshuawaheed.myshopapp.activities

import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.joshuawaheed.myshopapp.R
import com.joshuawaheed.myshopapp.firestore.FirestoreClass
import com.joshuawaheed.myshopapp.models.User
import com.joshuawaheed.myshopapp.utils.MSPButton
import com.joshuawaheed.myshopapp.utils.MSPTextViewBold

class RegisterActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        setupActionBar()

        findViewById<MSPButton>(R.id.btn_register).setOnClickListener {
            registerUser()
        }

        findViewById<MSPTextViewBold>(R.id.tv_login).setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupActionBar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar_register_activity)
        setSupportActionBar(toolbar)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        }

        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun validateRegisterDetails(): Boolean {
        val etFirstName: EditText = findViewById(R.id.et_first_name)
        val etLastName: EditText = findViewById(R.id.et_last_name)
        val etEmail: EditText = findViewById(R.id.et_email)
        val etPassword: EditText = findViewById(R.id.et_password)
        val etConfirmPassword: EditText = findViewById(R.id.et_confirm_password)
        val cbTermsAndConditions: AppCompatCheckBox = findViewById(R.id.cb_terms_and_condition)

        return when {
            TextUtils.isEmpty(etFirstName.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_first_name), true)
                false
            }

            TextUtils.isEmpty(etLastName.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_last_name), true)
                false
            }

            TextUtils.isEmpty(etEmail.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }

            TextUtils.isEmpty(etPassword.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }

            TextUtils.isEmpty(etConfirmPassword.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_confirm_password), true)
                false
            }

            etPassword.text.toString().trim { it <= ' ' } != etConfirmPassword.text.toString()
                .trim { it <= ' ' } -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_password_and_confirm_password_mismatch), true)
                false
            }
            !cbTermsAndConditions.isChecked -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_agree_terms_and_condition), true)
                false
            }
            else -> {
                true
            }
        }
    }

    private fun registerUser() {
        if (validateRegisterDetails()) {
            showProgressDialog(resources.getString(R.string.please_wait))

            val etEmail: EditText = findViewById(R.id.et_email)
            val email: String = etEmail.text.toString().trim { it <= ' ' }
            val etPassword: EditText = findViewById(R.id.et_password)
            val password: String = etPassword.text.toString().trim { it <= ' ' }

            FirebaseAuth
                .getInstance()
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val etFirstName: EditText = findViewById(R.id.et_first_name)
                        val etLastName: EditText = findViewById(R.id.et_last_name)
                        val firebaseUser: FirebaseUser = task.result!!.user!!

                        val user = User(
                            firebaseUser.uid,
                            etFirstName.text.toString().trim { it <= ' ' },
                            etLastName.text.toString().trim { it <= ' ' },
                            etEmail.text.toString().trim { it <= ' ' }
                        )

                        FirestoreClass().registerUser(this@RegisterActivity, user)
                    } else {
                        hideProgressDialog()
                        showErrorSnackBar(task.exception!!.message.toString(), true)
                    }
                }
        }
    }

    fun userRegistrationSuccess() {
        hideProgressDialog()

        Toast.makeText(
            this@RegisterActivity,
            resources.getString(R.string.register_success),
            Toast.LENGTH_SHORT
        ).show()

        FirebaseAuth.getInstance().signOut()
        finish()
    }
}