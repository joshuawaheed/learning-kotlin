package com.joshuawaheed.myshopapp.ui.activities

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.joshuawaheed.myshopapp.R
import com.joshuawaheed.myshopapp.firestore.FirestoreClass
import com.joshuawaheed.myshopapp.models.Product
import com.joshuawaheed.myshopapp.utils.Constants
import com.joshuawaheed.myshopapp.utils.GlideLoader

class ProductDetailsActivity : BaseActivity() {
    private var mProductId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_product_details)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        if (intent.hasExtra(Constants.EXTRA_PRODUCT_ID)) {
            mProductId = intent.getStringExtra(Constants.EXTRA_PRODUCT_ID)!!
            Log.i("Product ID", mProductId)
        }

        setupActionBar()
        getProductDetails()
    }

    fun getProductDetails() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getProductDetails(this@ProductDetailsActivity, mProductId)
    }

    fun productDetailsSuccess(product: Product) {
        hideProgressDialog()

        val ivProductImage: ImageView = findViewById(R.id.iv_product_details_photo)
        GlideLoader(this@ProductDetailsActivity).loadProductPicture(
            Uri.parse(product.image),
            ivProductImage
        )

        val tvProductTitle: TextView = findViewById(R.id.tv_product_details_product_title)
        tvProductTitle.text = product.title

        val tvProductPrice: TextView = findViewById(R.id.tv_product_details_price)
        tvProductPrice.text = "$" + product.price

        val tvProductDescription: TextView = findViewById(R.id.tv_product_details_description)
        tvProductDescription.text = product.description

        val tvProductQuantity: TextView = findViewById(R.id.tv_product_details_stock_quantity)
        tvProductQuantity.text = product.stock_quantity
    }

    private fun setupActionBar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar_product_details_activity)
        setSupportActionBar(toolbar)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar.setNavigationOnClickListener { onBackPressed() }
    }
}