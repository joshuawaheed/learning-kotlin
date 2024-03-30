package com.joshuawaheed.myshopapp.utils

import android.app.Activity
import android.content.Intent
import android.provider.MediaStore

object Constants {

    // Firebase Constants
    const val USERS: String = "users"

    // Preferences
    const val MYSHOPAPP_PREFERENCES: String = "MyShopPalPrefs"
    const val LOGGED_IN_USERNAME: String = "logged_in_username"

    // Parcelable
    const val EXTRA_USER_DETAILS: String = "extra_user_details"

    // Permissions
    const val READ_STORAGE_PERMISSION_CODE = 2

    // Request codes
    const val PICK_IMAGE_REQUEST_CODE = 1

    // User information
    const val MALE: String = "male"
    const val FEMALE: String = "female"
    const val MOBILE: String = "mobile"
    const val GENDER: String = "gender"

    fun showImageChooser(activity: Activity) {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

}