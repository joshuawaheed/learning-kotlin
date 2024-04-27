package com.joshuawaheed.myshopapp.ui.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.joshuawaheed.myshopapp.R
import com.joshuawaheed.myshopapp.firestore.FirestoreClass
import com.joshuawaheed.myshopapp.models.CartItem
import com.joshuawaheed.myshopapp.models.Product
import com.joshuawaheed.myshopapp.utils.Constants
import com.joshuawaheed.myshopapp.utils.GlideLoader

class ProductDetailsActivity : BaseActivity(), View.OnClickListener {
    private var mProductId: String = ""
    private lateinit var mProductDetails: Product
    private var mProductOwnerId: String = ""

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
        }

        if (intent.hasExtra(Constants.EXTRA_PRODUCT_OWNER_ID)) {
            mProductOwnerId = intent.getStringExtra(Constants.EXTRA_PRODUCT_OWNER_ID)!!
        }

        val btnAddToCart: Button = findViewById(R.id.btn_add_to_cart)
        val btnGoToCart: Button = findViewById(R.id.btn_go_to_cart)

        if (FirestoreClass().getCurrentUserID() == mProductOwnerId) {
            btnAddToCart.visibility = View.GONE
            btnGoToCart.visibility = View.GONE
        } else {
            btnAddToCart.visibility = View.VISIBLE
        }

        setupActionBar()
        getProductDetails()
        btnAddToCart.setOnClickListener(this)
        btnGoToCart.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.btn_add_to_cart -> {
                    addToCart()
                }
                R.id.btn_go_to_cart->{
                    startActivity(Intent(this@ProductDetailsActivity, CartListActivity::class.java))
                }
            }
        }
    }

    fun getProductDetails() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getProductDetails(this@ProductDetailsActivity, mProductId)
    }

    fun productDetailsSuccess(product: Product) {
        mProductDetails = product

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

        if (product.stock_quantity.toInt() == 0) {
            hideProgressDialog()

            val btnAddToCart: Button = findViewById(R.id.btn_add_to_cart)
            btnAddToCart.visibility = View.GONE

            tvProductQuantity.text = resources.getString(R.string.lbl_out_of_stock)
            tvProductQuantity.setTextColor(
                ContextCompat.getColor(this@ProductDetailsActivity, R.color.colorSnackBarError)
            )
        } else {
            if (FirestoreClass().getCurrentUserID() == product.user_id) {
                hideProgressDialog()
            } else {
                FirestoreClass().checkIfItemExistInCart(this@ProductDetailsActivity, mProductId)
            }
        }
    }

    fun productExistsInCart() {
        hideProgressDialog()

        val btnAddToCart: Button = findViewById(R.id.btn_add_to_cart)
        btnAddToCart.visibility = View.GONE

        val btnGoToCart: Button = findViewById(R.id.btn_go_to_cart)
        btnGoToCart.visibility = View.VISIBLE
    }

    private fun addToCart() {
        val cartItem = CartItem(
            FirestoreClass().getCurrentUserID(),
            mProductOwnerId,
            mProductId,
            mProductDetails.title,
            mProductDetails.price,
            mProductDetails.image,
            Constants.DEFAULT_CART_QUANTITY
        )

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addCartItems(this@ProductDetailsActivity, cartItem)
    }

    fun addToCartSuccess() {
        hideProgressDialog()

        Toast.makeText(
            this@ProductDetailsActivity,
            resources.getString(R.string.success_message_item_added_to_cart),
            Toast.LENGTH_SHORT
        ).show()

        val btnAddToCart: Button = findViewById(R.id.btn_add_to_cart)
        btnAddToCart.visibility = View.GONE

        val btnGoToCart: Button = findViewById(R.id.btn_go_to_cart)
        btnGoToCart.visibility = View.VISIBLE
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