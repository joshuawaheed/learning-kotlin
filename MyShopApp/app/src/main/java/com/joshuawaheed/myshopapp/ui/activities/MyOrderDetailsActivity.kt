package com.joshuawaheed.myshopapp.ui.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.joshuawaheed.myshopapp.R
import com.joshuawaheed.myshopapp.models.Order
import com.joshuawaheed.myshopapp.ui.adapters.CartItemsListAdapter
import com.joshuawaheed.myshopapp.utils.Constants
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

class MyOrderDetailsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_order_details)
        setupActionBar()

        var myOrderDetails: Order = Order()
        if (intent.hasExtra(Constants.EXTRA_MY_ORDER_DETAILS)) {
            myOrderDetails = intent.getParcelableExtra(Constants.EXTRA_MY_ORDER_DETAILS)!!
        }
        setupUI(myOrderDetails)
    }

    private fun setupActionBar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar_my_order_details_activity)
        setSupportActionBar(toolbar)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun setupUI(orderDetails: Order) {
        val tvOrderDetailsId: TextView = findViewById(R.id.tv_order_details_id)
        tvOrderDetailsId.text = orderDetails.title

        val dateFormat = "dd MMM yyyy HH:mm"
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())

        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = orderDetails.order_datetime

        val orderDateTime = formatter.format(calendar.time)
        val tvOrderDetailsDate: TextView = findViewById(R.id.tv_order_details_date)
        tvOrderDetailsDate.text = orderDateTime

        val diffInMilliSeconds: Long = System.currentTimeMillis() - orderDetails.order_datetime
        val diffInHours: Long = TimeUnit.MILLISECONDS.toHours(diffInMilliSeconds)
        Log.e("Difference in Hours", "$diffInHours")

        val tvOrderStatus: TextView = findViewById(R.id.tv_order_status)

        when {
            diffInHours < 1 -> {
                tvOrderStatus.text = resources.getString(R.string.order_status_pending)
                tvOrderStatus.setTextColor(ContextCompat.getColor(
                    this@MyOrderDetailsActivity,
                    R.color.colorAccent
                ))
            }
            diffInHours < 2 -> {
                tvOrderStatus.text = resources.getString(R.string.order_status_in_process)
                tvOrderStatus.setTextColor(ContextCompat.getColor(
                    this@MyOrderDetailsActivity,
                    R.color.colorOrderStatusInProcess
                ))
            }
            else -> {
                tvOrderStatus.text = resources.getString(R.string.order_status_delivered)
                tvOrderStatus.setTextColor(ContextCompat.getColor(
                    this@MyOrderDetailsActivity,
                    R.color.colorOrderStatusDelivered
                ))
            }
        }

        val rvMyOrderItemsList: RecyclerView = findViewById(R.id.rv_my_order_items_list)
        rvMyOrderItemsList.layoutManager = LinearLayoutManager(this@MyOrderDetailsActivity)
        rvMyOrderItemsList.setHasFixedSize(true)

        val cartListAdapter = CartItemsListAdapter(this@MyOrderDetailsActivity, orderDetails.items, false)
        rvMyOrderItemsList.adapter = cartListAdapter

        val tvMyOrderDetailsAddressType: TextView = findViewById(R.id.tv_my_order_details_address_type)
        tvMyOrderDetailsAddressType.text = orderDetails.address.type

        val tvMyOrderDetailsFullName: TextView = findViewById(R.id.tv_my_order_details_full_name)
        tvMyOrderDetailsFullName.text = orderDetails.address.name

        val tvMyOrderDetailsAddress: TextView = findViewById(R.id.tv_my_order_details_address)
        tvMyOrderDetailsAddress.text = "${orderDetails.address.address}, ${orderDetails.address.zipCode}"

        val tvMyOrderDetailsAdditionalNote: TextView = findViewById(R.id.tv_my_order_details_additional_note)
        tvMyOrderDetailsAdditionalNote.text = orderDetails.address.additionalNote

        val tvMyOrderDetailsOtherDetails: TextView = findViewById(R.id.tv_my_order_details_other_details)
        if (orderDetails.address.otherDetails.isNotEmpty()) {
            tvMyOrderDetailsOtherDetails.visibility = View.VISIBLE
            tvMyOrderDetailsOtherDetails.text = orderDetails.address.otherDetails
        } else {
            tvMyOrderDetailsOtherDetails.visibility = View.GONE
        }

        val tvMyOrderDetailsMobileNumber: TextView = findViewById(R.id.tv_my_order_details_mobile_number)
        tvMyOrderDetailsMobileNumber.text = orderDetails.address.mobileNumber

        val tvOrderDetailsSubTotal: TextView = findViewById(R.id.tv_order_details_sub_total)
        tvOrderDetailsSubTotal.text = orderDetails.sub_total_amount

        val tvOrderDetailsShoppingCart: TextView = findViewById(R.id.tv_order_details_shipping_charge)
        tvOrderDetailsShoppingCart.text = orderDetails.shipping_charge

        val tvOrderDetailsTotalAmount: TextView = findViewById(R.id.tv_order_details_total_amount)
        tvOrderDetailsTotalAmount.text = orderDetails.total_amount
    }
}