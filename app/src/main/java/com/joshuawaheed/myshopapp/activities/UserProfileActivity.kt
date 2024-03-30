package com.joshuawaheed.myshopapp.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.joshuawaheed.myshopapp.R
import com.joshuawaheed.myshopapp.firestore.FirestoreClass
import com.joshuawaheed.myshopapp.models.User
import com.joshuawaheed.myshopapp.utils.Constants
import com.joshuawaheed.myshopapp.utils.GlideLoader
import java.io.IOException

class UserProfileActivity : BaseActivity(), View.OnClickListener {
    private lateinit var mUserDetails: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_profile)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if(intent.hasExtra(Constants.EXTRA_USER_DETAILS)) {
            mUserDetails = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!
        }

        val etFirstName: EditText = findViewById(R.id.et_first_name)
        etFirstName.isEnabled = false
        etFirstName.setText(mUserDetails.firstName)

        val etLastName: EditText = findViewById(R.id.et_last_name)
        etLastName.isEnabled = false
        etLastName.setText(mUserDetails.lastName)

        val etEmail: EditText = findViewById(R.id.et_email)
        etEmail.isEnabled = false
        etEmail.setText(mUserDetails.email)

        val ivUserPhoto: ImageView = findViewById(R.id.iv_user_photo)
        ivUserPhoto.setOnClickListener(this@UserProfileActivity)

        val btnSubmit: Button = findViewById(R.id.btn_submit)
        btnSubmit.setOnClickListener(this@UserProfileActivity)
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.iv_user_photo -> {
                    val checkReadPermission = ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )

                    if (
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE ||
                        checkReadPermission == PackageManager.PERMISSION_GRANTED
                    ) {
                        Constants.showImageChooser(this@UserProfileActivity)
                    } else {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            Constants.READ_STORAGE_PERMISSION_CODE
                        )
                    }
                }
                R.id.btn_submit ->{
                    if (validateUserProfileDetails()) {
                        val etMobileNumber: EditText = findViewById(R.id.et_mobile_number)
                        val rbMale: RadioButton = findViewById(R.id.rb_male)

                        val userHashMap = HashMap<String, Any>()
                        val mobileNumber = etMobileNumber.text.toString().trim { it <= ' ' }

                        val gender = if (rbMale.isChecked) {
                            Constants.MALE
                        } else {
                            Constants.FEMALE
                        }

                        if (mobileNumber.isNotEmpty()) {
                            userHashMap[Constants.MOBILE] = mobileNumber.toLong()
                        }

                        userHashMap[Constants.GENDER] = gender
                        showProgressDialog(resources.getString(R.string.please_wait))
                        FirestoreClass().updateUserProfileData(this@UserProfileActivity, userHashMap)
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this@UserProfileActivity)
            } else {
                Toast.makeText(
                    this,
                    resources.getString(R.string.read_storage_permission_denied),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
                if (data != null) {
                    try {
                        val selectedImageFileUri = data.data!!
                        val ivUserPhoto: ImageView = findViewById(R.id.iv_user_photo)

                        GlideLoader(this@UserProfileActivity).loadUserPicture(
                            selectedImageFileUri,
                            ivUserPhoto
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()

                        Toast.makeText(
                            this@UserProfileActivity,
                            resources.getString(R.string.image_selection_failed),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.e("Request Cancelled", "Image selection cancelled")
        }
    }

    private fun validateUserProfileDetails(): Boolean {
        val etMobileNumber: EditText = findViewById(R.id.et_mobile_number)

        return when {
            TextUtils.isEmpty(etMobileNumber.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_mobile_number),
                    true
                )
                false
            }
            else -> {
                true
            }
        }
    }

    fun userProfileUpdateSuccess() {
        hideProgressDialog()

        Toast.makeText(
            this@UserProfileActivity,
            resources.getString(R.string.msg_profile_update_success),
            Toast.LENGTH_SHORT
        ).show()
        
        startActivity(Intent(this@UserProfileActivity, MainActivity::class.java))
        finish()
    }
}