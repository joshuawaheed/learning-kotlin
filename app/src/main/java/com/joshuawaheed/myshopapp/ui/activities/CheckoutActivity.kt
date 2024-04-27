package com.joshuawaheed.myshopapp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.joshuawaheed.myshopapp.R
import com.joshuawaheed.myshopapp.firestore.FirestoreClass
import com.joshuawaheed.myshopapp.models.Address
import com.joshuawaheed.myshopapp.models.CartItem
import com.joshuawaheed.myshopapp.models.Order
import com.joshuawaheed.myshopapp.models.Product
import com.joshuawaheed.myshopapp.ui.adapters.CartItemsListAdapter
import com.joshuawaheed.myshopapp.utils.Constants

class CheckoutActivity : BaseActivity() {
    private var mAddressDetails: Address? = null
    private lateinit var mProductsList: ArrayList<Product>
    private lateinit var mCartItemsList: ArrayList<CartItem>
    private var mSubTotal: Double = 0.0
    private var mTotalAmount: Double = 0.0
    private lateinit var mOrderDetails: Order

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)
        setupActionBar()

        if (intent.hasExtra(Constants.EXTRA_SELECTED_ADDRESS)) {
            mAddressDetails = intent.getParcelableExtra(Constants.EXTRA_SELECTED_ADDRESS)!!
        }

        if (mAddressDetails != null) {
            val tvCheckoutAddressType: TextView = findViewById(R.id.tv_checkout_address_type)
            tvCheckoutAddressType.text = mAddressDetails?.type

            val tvCheckoutFullName: TextView = findViewById(R.id.tv_checkout_full_name)
            tvCheckoutFullName.text = mAddressDetails?.name

            val tvCheckoutAddress: TextView = findViewById(R.id.tv_checkout_address)
            tvCheckoutAddress.text = "${mAddressDetails!!.address}, ${mAddressDetails!!.zipCode}"

            val tvCheckoutAdditionalNote: TextView = findViewById(R.id.tv_checkout_additional_note)
            tvCheckoutAdditionalNote.text = mAddressDetails?.additionalNote

            if (mAddressDetails?.otherDetails!!.isNotEmpty()) {
                val tvCheckoutOtherDetails: TextView = findViewById(R.id.tv_checkout_other_details)
                tvCheckoutOtherDetails.text = mAddressDetails?.otherDetails
            }

            val tvCheckoutMobileNumber: TextView = findViewById(R.id.tv_checkout_mobile_number)
            tvCheckoutMobileNumber.text = mAddressDetails?.mobileNumber
        }

        getProductList()

        val btnPlaceOrder: Button = findViewById(R.id.btn_place_order)
        btnPlaceOrder.setOnClickListener {
            placeAnOrder()
        }
    }

    private fun setupActionBar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar_checkout_activity)
        setSupportActionBar(toolbar)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun getProductList() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getAllProductsList(this@CheckoutActivity)
    }

    fun successProductsListFromFireStore(productsList: ArrayList<Product>) {
        mProductsList = productsList
        getCartItemsList()
    }

    private fun getCartItemsList() {
        FirestoreClass().getCartList(this@CheckoutActivity)
    }

    fun successCartItemsList(cartList: ArrayList<CartItem>) {
        hideProgressDialog()

        for (product in mProductsList) {
            for (cart in cartList) {
                if (product.product_id == cart.product_id) {
                    cart.stock_quantity = product.stock_quantity
                }
            }
        }

        mCartItemsList = cartList

        val rvCartListItems: RecyclerView = findViewById(R.id.rv_cart_list_items)
        rvCartListItems.layoutManager = LinearLayoutManager(this@CheckoutActivity)
        rvCartListItems.setHasFixedSize(true)

        val cartListAdapter = CartItemsListAdapter(this@CheckoutActivity, mCartItemsList, false)
        rvCartListItems.adapter = cartListAdapter

        for (item in mCartItemsList) {
            val availableQuantity = item.stock_quantity.toInt()

            if (availableQuantity > 0) {
                val price = item.price.toDouble()
                val quantity = item.cart_quantity.toInt()
                mSubTotal += (price * quantity)
            }
        }

        val tvCheckoutSubTotal: TextView = findViewById(R.id.tv_checkout_sub_total)
        tvCheckoutSubTotal.text = "$$mSubTotal"

        val tvCheckoutShippingCharge: TextView = findViewById(R.id.tv_checkout_shipping_charge)
        tvCheckoutShippingCharge.text = "$10.0"

        val llCheckoutPlaceOrder: LinearLayout = findViewById(R.id.ll_checkout_place_order)

        if (mSubTotal > 0) {
            llCheckoutPlaceOrder.visibility = View.VISIBLE

            mTotalAmount = mSubTotal + 10.0
            val tvCheckoutTotalAmount: TextView = findViewById(R.id.tv_checkout_total_amount)
            tvCheckoutTotalAmount.text = "$$mTotalAmount"
        } else {
            llCheckoutPlaceOrder.visibility = View.GONE
        }
    }

    private fun placeAnOrder() {
        showProgressDialog(resources.getString(R.string.please_wait))

        if (mAddressDetails != null) {
            mOrderDetails = Order(
                FirestoreClass().getCurrentUserID(),
                mCartItemsList,
                mAddressDetails!!,
                "My order ${System.currentTimeMillis()}",
                mCartItemsList[0].image,
                mSubTotal.toString(),
                "10.0",
                mTotalAmount.toString(),
                System.currentTimeMillis()
            )

            FirestoreClass().placeOrder(this@CheckoutActivity, mOrderDetails)
        } else {
            hideProgressDialog()

            Toast.makeText(
                this@CheckoutActivity,
                "We could not get the address details to deliver this order to.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun orderPlacedSuccess() {
        FirestoreClass().updateAllDetails(this@CheckoutActivity, mCartItemsList, mOrderDetails)
    }

    fun allDetailsUpdatedSuccessfully() {
        hideProgressDialog()

        Toast.makeText(
            this@CheckoutActivity,
            "Your order placed successfully.",
            Toast.LENGTH_SHORT
        ).show()

        val intent = Intent(this@CheckoutActivity, DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}