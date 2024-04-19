package com.joshuawaheed.myshopapp.ui.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.joshuawaheed.myshopapp.R
import com.joshuawaheed.myshopapp.firestore.FirestoreClass
import com.joshuawaheed.myshopapp.models.User
import com.joshuawaheed.myshopapp.utils.Constants
import com.joshuawaheed.myshopapp.utils.GlideLoader

class SettingsActivity : BaseActivity(), View.OnClickListener {
    private lateinit var mUserDetails: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        setupActionBar()
        findViewById<TextView>(R.id.tv_edit).setOnClickListener(this)
        findViewById<Button>(R.id.btn_logout).setOnClickListener(this)
        findViewById<LinearLayout>(R.id.ll_address).setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        getUserDetails()
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.tv_edit -> {
                    val intent = Intent(this@SettingsActivity, UserProfileActivity::class.java)
                    intent.putExtra(Constants.EXTRA_USER_DETAILS, mUserDetails)
                    startActivity(intent)
                }
                R.id.btn_logout -> {
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this@SettingsActivity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                R.id.ll_address -> {
                    val intent = Intent(this@SettingsActivity, AddressListActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    private fun setupActionBar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar_settings_activity)
        setSupportActionBar(toolbar)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun getUserDetails() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getUserDetails(this@SettingsActivity)
    }

    fun userDetailsSuccess(user: User) {
        mUserDetails = user;
        hideProgressDialog()

        val ivUserPhoto: ImageView = findViewById(R.id.iv_user_photo)
        GlideLoader(this@SettingsActivity).loadUserPicture(
            Uri.parse(user.image),
            ivUserPhoto
        )

        val tvName: TextView = findViewById(R.id.tv_name)
        tvName.text = "${user.firstName} ${user.lastName}"

        val tvGender: TextView = findViewById(R.id.tv_gender)
        tvGender.text = user.gender

        val tvEmail: TextView = findViewById(R.id.tv_email)
        tvEmail.text = user.email

        val tvMobileNumber: TextView = findViewById(R.id.tv_mobile_number)
        tvMobileNumber.text = "${user.mobile}"
    }
}