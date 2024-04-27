package com.joshuawaheed.myshopapp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.joshuawaheed.myshopapp.R
import com.joshuawaheed.myshopapp.firestore.FirestoreClass
import com.joshuawaheed.myshopapp.models.CartItem
import com.joshuawaheed.myshopapp.models.Product
import com.joshuawaheed.myshopapp.ui.adapters.CartItemsListAdapter
import com.joshuawaheed.myshopapp.utils.Constants

class CartListActivity : BaseActivity() {
    private lateinit var mProductsList: ArrayList<Product>
    private lateinit var mCartListItems: ArrayList<CartItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cart_list)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        setupActionBar()

        findViewById<Button>(R.id.btn_checkout).setOnClickListener {
            val intent = Intent(this@CartListActivity, AddressListActivity::class.java)
            intent.putExtra(Constants.EXTRA_SELECT_ADDRESS, true)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        getProductList()
    }

    fun successCartItemsList(cartList: ArrayList<CartItem>) {
        hideProgressDialog()

        for (product in mProductsList) {
            for (cartItem in cartList) {
                if (product.product_id == cartItem.product_id) {
                    cartItem.stock_quantity = product.stock_quantity

                    if (product.stock_quantity.toInt() == 0){
                        cartItem.cart_quantity = product.stock_quantity
                    }
                }
            }
        }

        mCartListItems = cartList

        val llCheckout: LinearLayout = findViewById(R.id.ll_checkout)
        val tvNoCartItemFound: TextView = findViewById(R.id.tv_no_cart_item_found)
        val rvCartItemsList: RecyclerView = findViewById(R.id.rv_cart_items_list)

        if (mCartListItems.size > 0) {
            llCheckout.visibility = View.VISIBLE
            tvNoCartItemFound.visibility = View.GONE

            rvCartItemsList.visibility = View.VISIBLE
            rvCartItemsList.layoutManager = LinearLayoutManager(this@CartListActivity)
            rvCartItemsList.setHasFixedSize(true)
            val cartListAdapter = CartItemsListAdapter(this@CartListActivity, mCartListItems, true)
            rvCartItemsList.adapter = cartListAdapter

            var subTotal: Double = 0.0

            for (item in mCartListItems) {
                val availableQuantity = item.stock_quantity.toInt()

                if (availableQuantity > 0) {
                    val price = item.price.toDouble()
                    val quantity = item.cart_quantity.toInt()
                    subTotal += (price * quantity)
                }
            }

            val tvSubTotal: TextView = findViewById(R.id.tv_sub_total)
            tvSubTotal.text = "$$subTotal"

            val tvShippingCharge: TextView = findViewById(R.id.tv_shipping_charge)
            tvShippingCharge.text = "$10.0"

            if (subTotal > 0) {
                llCheckout.visibility = View.VISIBLE
                val total = subTotal + 10
                val tvTotalAmount: TextView = findViewById(R.id.tv_total_amount)
                tvTotalAmount.text = "$$total"
            } else {
                llCheckout.visibility = View.GONE
            }

        } else {
            rvCartItemsList.visibility = View.GONE
            llCheckout.visibility = View.GONE
            tvNoCartItemFound.visibility = View.VISIBLE
        }
    }

    fun successProductsListFromFireStore(productsList: ArrayList<Product>) {
        hideProgressDialog()
        mProductsList = productsList
        getCartItemsList()
    }

    fun itemRemovedSuccess() {
        hideProgressDialog()

        Toast.makeText(
            this@CartListActivity,
            resources.getString(R.string.msg_item_removed_successfully),
            Toast.LENGTH_SHORT
        ).show()

        getCartItemsList()
    }

    fun itemUpdateSuccess() {
        hideProgressDialog()
        getCartItemsList()
    }

    private fun getCartItemsList() {
        FirestoreClass().getCartList(this@CartListActivity)
    }

    private fun getProductList() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getAllProductsList(this@CartListActivity)
    }

    private fun setupActionBar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar_cart_list_activity)
        setSupportActionBar(toolbar)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar.setNavigationOnClickListener { onBackPressed() }
    }
}