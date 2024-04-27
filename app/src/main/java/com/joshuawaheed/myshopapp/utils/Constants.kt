package com.joshuawaheed.myshopapp.utils

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap

object Constants {

    // Firebase Constants
    const val USERS: String = "users"
    const val PRODUCTS: String = "products"
    const val ADDRESSES: String = "addresses"
    const val ORDERS: String = "orders"
    const val SOLD_PRODUCTS: String = "sold_products"

    // Preferences
    const val MYSHOPAPP_PREFERENCES: String = "MyShopPalPrefs"
    const val LOGGED_IN_USERNAME: String = "logged_in_username"

    // Parcelable
    const val EXTRA_USER_DETAILS: String = "extra_user_details"
    const val EXTRA_PRODUCT_DETAILS: String = "extra_product_details"
    const val EXTRA_PRODUCT_ID: String = "extra_product_id"
    const val EXTRA_PRODUCT_OWNER_ID: String = "extra_product_owner_id"
    const val EXTRA_ADDRESS_DETAILS: String = "AddressDetails"
    const val EXTRA_SELECT_ADDRESS: String = "extra_select_address"
    const val EXTRA_SELECTED_ADDRESS: String = "extra_selected_address"
    const val EXTRA_MY_ORDER_DETAILS: String = "extra_MY_ORDER_DETAILS"
    const val EXTRA_SOLD_PRODUCT_DETAILS: String = "extra_sold_product_details"

    // Permissions
    const val READ_STORAGE_PERMISSION_CODE = 2

    // Request codes
    const val PICK_IMAGE_REQUEST_CODE = 1
    const val ADD_ADDRESS_REQUEST_CODE: Int = 121

    // User information
    const val MALE: String = "male"
    const val FEMALE: String = "female"
    const val FIRST_NAME: String = "firstName"
    const val LAST_NAME: String = "lastName"
    const val MOBILE: String = "mobile"
    const val GENDER: String = "gender"
    const val IMAGE: String = "image"
    const val COMPLETE_PROFILE: String = "profileCompleted"
    const val USER_PROFILE_IMAGE: String = "User_Profile_Image"

    // Product information
    const val PRODUCT_IMAGE_URL_PREFIX: String = "Product_Image"
    const val PRODUCT_USER_ID: String = "user_id"
    const val PRODUCT_ID: String = "product_id"
    const val STOCK_QUANTITY: String = "stock_quantity"

    // Cart
    const val DEFAULT_CART_QUANTITY: String = "1"
    const val CART_ITEMS: String = "cart_items"
    const val CART_QUANTITY: String = "cart_quantity"

    // Address
    const val HOME: String = "Home"
    const val OFFICE: String = "Office"
    const val OTHER: String = "Other"

    fun showImageChooser(activity: Activity) {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        activity.startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST_CODE)
    }

    fun getFileExtension(activity: Activity, uri: Uri?): String? {
        return MimeTypeMap
            .getSingleton()
            .getExtensionFromMimeType(activity.contentResolver.getType(uri!!))
    }

}