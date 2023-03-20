package com.joshuawaheed.projemanag.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.joshuawaheed.projemanag.R
import com.joshuawaheed.projemanag.firebase.FirestoreClass
import com.joshuawaheed.projemanag.models.User
import com.joshuawaheed.projemanag.utils.Constants
import java.io.IOException

class MyProfileActivity : BaseActivity() {
    companion object {
        private const val READ_STORAGE_PERMISSION_CODE = 1
        private const val PICK_IMAGE_REQUEST_CODE = 2
    }

    private lateinit var mIvUserImage: ImageView
    private lateinit var mUserDetails: User

    private var mSelectedImageFileUri: Uri? = null
    private var mProfileImageUrl: String = ""

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (
            resultCode == Activity.RESULT_OK &&
            requestCode == PICK_IMAGE_REQUEST_CODE &&
            data!!.data != null
        ) {
            mSelectedImageFileUri = data.data

            try {
                Glide
                    .with(this)
                    .load(mSelectedImageFileUri)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user_place_holder)
                    .into(mIvUserImage)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_my_profile)

        mIvUserImage = findViewById(R.id.iv_my_profile_user_image)

        setupActionBar()

        FirestoreClass().loadUserData(this)

        mIvUserImage.setOnClickListener {
            val checkReadExternalStoragePermission = ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            )

            if (checkReadExternalStoragePermission == PackageManager.PERMISSION_GRANTED) {
                showImageChooser()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                    READ_STORAGE_PERMISSION_CODE
                )
            }
        }

        val btnUpdate: Button = findViewById(R.id.btn_my_profile_update)

        btnUpdate.setOnClickListener {
            if (mSelectedImageFileUri != null) {
                uploadUserImage()
            } else {
                showProgressDialog(resources.getString(R.string.please_wait))
                updateUserProfileData()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showImageChooser()
            } else {
                Toast.makeText(
                    this,
                    "Oops, you just denied the permission for storage. " +
                    "You can also allow it from settings.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    fun profileUpdateSuccess() {
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }

    fun setUserDataInUI(user: User) {
        mUserDetails = user

        try {
            Glide
                .with(this)
                .load(mUserDetails.image)
                .centerCrop()
                .placeholder(R.drawable.ic_user_place_holder)
                .into(mIvUserImage)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val etName: EditText = findViewById(R.id.et_name_my_profile)

        etName.setText(mUserDetails.name)

        val etEmail: EditText = findViewById(R.id.et_email_my_profile)

        etEmail.setText(mUserDetails.email)

        val etMobile: EditText = findViewById(R.id.et_mobile_my_profile)

        if (mUserDetails.mobile != 0L) {
            etMobile.setText(mUserDetails.mobile.toString())
        }
    }

    private fun getFileExtension(uri: Uri?): String? {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri!!))
    }

    private fun setupActionBar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar_my_profile_activity)

        setSupportActionBar(toolbar)

        val actionBar = supportActionBar

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = resources.getString(R.string.my_profile_title)
        }

        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun showImageChooser() {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )

        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    private fun updateUserProfileData() {
        val userHashMap = HashMap<String, Any>()

        if (mProfileImageUrl.isNotEmpty() && mProfileImageUrl != mUserDetails.image) {
            userHashMap[Constants.IMAGE] = mProfileImageUrl
        }

        val etName: EditText = findViewById(R.id.et_name_my_profile)

        if (etName.text.toString() != mUserDetails.name) {
            userHashMap[Constants.NAME] = etName.text.toString()
        }

        val etMobile: EditText = findViewById(R.id.et_mobile_my_profile)

        if (etMobile.text.toString() != mUserDetails.mobile.toString()) {
            userHashMap[Constants.MOBILE] = etMobile.text.toString().toLong()
        }

        FirestoreClass().updateUserProfileData(this, userHashMap)
    }

    private fun uploadUserImage() {
        showProgressDialog(resources.getString(R.string.please_wait))

        if (mSelectedImageFileUri != null) {
            val sRef: StorageReference = FirebaseStorage
                .getInstance()
                .reference
                .child(
                    "USER_IMAGE" +
                    System.currentTimeMillis() +
                    "." +
                    getFileExtension(mSelectedImageFileUri)
                )

            sRef.putFile(mSelectedImageFileUri!!).addOnSuccessListener {
                taskSnapshot ->
                val downloadUrl = taskSnapshot.metadata!!.reference!!.downloadUrl

                Log.i("Firebase Image URL", downloadUrl.toString())

                downloadUrl.addOnSuccessListener {
                    uri ->
                    Log.i("Downloadable Image URL", uri.toString())
                    mProfileImageUrl = uri.toString()
                    updateUserProfileData()
                }
            }.addOnFailureListener {
                exception ->
                Toast.makeText(
                    this@MyProfileActivity,
                    exception.message,
                    Toast.LENGTH_LONG
                ).show()

                hideProgressDialog()
            }
        }
    }
}