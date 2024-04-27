package com.joshuawaheed.myshopapp.ui.activities

import android.Manifest
import android.app.Activity
import android.content.Context
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
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.joshuawaheed.myshopapp.R
import com.joshuawaheed.myshopapp.firestore.FirestoreClass
import com.joshuawaheed.myshopapp.models.Product
import com.joshuawaheed.myshopapp.utils.Constants
import com.joshuawaheed.myshopapp.utils.GlideLoader
import java.io.IOException

class AddProductActivity : BaseActivity(), View.OnClickListener {
    private lateinit var mProductDetails: Product
    private var mSelectedImageFileUri: Uri? = null
    private var mProductImageURL: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_product)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        if (intent.hasExtra(Constants.EXTRA_PRODUCT_DETAILS)) {
            mProductDetails = intent.getParcelableExtra(Constants.EXTRA_PRODUCT_DETAILS)!!
        } else {
            mProductDetails = Product()
        }

        setupActionBar()

        val ivAddProductAddPhoto: ImageView = findViewById(R.id.iv_add_product_add_photo)
        ivAddProductAddPhoto.setOnClickListener(this)

        val btnSubmit: Button = findViewById(R.id.btn_submit)
        btnSubmit.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.iv_add_product_add_photo -> {
                    val checkReadPermission = ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )

                    if (
                        Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE ||
                        checkReadPermission == PackageManager.PERMISSION_GRANTED
                    ) {
                        Constants.showImageChooser(this@AddProductActivity)
                    } else {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            Constants.READ_STORAGE_PERMISSION_CODE
                        )
                    }
                }
                R.id.btn_submit -> {
                    if (validateProductDetails()) {
                        showProgressDialog(resources.getString(R.string.please_wait))

                        FirestoreClass().uploadImageToCloudStorage(
                            this@AddProductActivity,
                            mSelectedImageFileUri,
                            Constants.PRODUCT_IMAGE_URL_PREFIX
                        )
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
                Constants.showImageChooser(this@AddProductActivity)
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
                    val ivAddProductAddPhoto: ImageView = findViewById(R.id.iv_add_product_add_photo)
                    ivAddProductAddPhoto.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_vector_edit))

                    try {
                        val ivAddProductPhoto: ImageView = findViewById(R.id.iv_add_product_photo)
                        mSelectedImageFileUri = data.data!!

                        GlideLoader(this@AddProductActivity).loadUserPicture(
                            mSelectedImageFileUri!!,
                            ivAddProductPhoto
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()

                        Toast.makeText(
                            this@AddProductActivity,
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

    fun imageUploadSuccess(imageURL: String) {
        mProductImageURL = imageURL
        uploadProductDetails()
    }

    fun uploadProductDetails() {
        val username = this
            .getSharedPreferences(Constants.MYSHOPAPP_PREFERENCES, Context.MODE_PRIVATE)
            .getString(Constants.LOGGED_IN_USERNAME, "")!!

        val etProductTitle: EditText = findViewById(R.id.et_product_title)
        val productTitle = etProductTitle.text.toString().trim { it <= ' ' }

        val etProductPrice: EditText = findViewById(R.id.et_product_price)
        val productPrice = etProductPrice.text.toString().trim { it <= ' ' }

        val etProductDescription: EditText = findViewById(R.id.et_product_description)
        val productDescription = etProductDescription.text.toString().trim { it <= ' ' }

        val etProductQuantity: EditText = findViewById(R.id.et_product_quantity)
        val productQuantity = etProductQuantity.text.toString().trim { it <= ' ' }

        val product = Product(
            FirestoreClass().getCurrentUserID(),
            username,
            productTitle,
            productPrice,
            productDescription,
            productQuantity,
            mProductImageURL
        )

        FirestoreClass().uploadProductDetails(this@AddProductActivity, product)
    }

    fun productUploadSuccess() {
        hideProgressDialog()

        Toast.makeText(
            this@AddProductActivity,
            resources.getString(R.string.product_upload_success),
            Toast.LENGTH_SHORT
        ).show()

        finish()
    }

    private fun setupActionBar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar_add_product_activity)
        setSupportActionBar(toolbar)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun validateProductDetails(): Boolean {
        val etProductTitle: EditText = findViewById(R.id.et_product_title)
        val etProductPrice: EditText = findViewById(R.id.et_product_price)
        val etProductDescription: EditText = findViewById(R.id.et_product_description)
        val etProductQuantity: EditText = findViewById(R.id.et_product_quantity)

        return when {
            mSelectedImageFileUri == null -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_select_product_image), true)
                false
            }
            TextUtils.isEmpty(etProductTitle.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_title), true)
                false
            }
            TextUtils.isEmpty(etProductPrice.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_price), true)
                false
            }
            TextUtils.isEmpty(etProductDescription.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_description), true)
                false
            }
            TextUtils.isEmpty(etProductQuantity.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_product_quantity), true)
                false
            }
            else -> {
                true
            }
        }
    }
}