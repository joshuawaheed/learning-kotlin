package com.joshuawaheed.myshopapp.ui.activities

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.joshuawaheed.myshopapp.R
import com.joshuawaheed.myshopapp.models.SoldProduct
import com.joshuawaheed.myshopapp.utils.Constants
import com.joshuawaheed.myshopapp.utils.GlideLoader
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SoldProductDetailsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sold_product_details)

        var productDetails: SoldProduct = SoldProduct()
        if (intent.hasExtra(Constants.EXTRA_SOLD_PRODUCT_DETAILS)) {
            productDetails = intent.getParcelableExtra<SoldProduct>(Constants.EXTRA_SOLD_PRODUCT_DETAILS)!!
        }

        setupActionBar()
        setupUI(productDetails)
    }

    private fun setupActionBar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar_sold_product_details_activity)
        setSupportActionBar(toolbar)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun setupUI(productDetails: SoldProduct) {
        val tvSoldProductDetailsId: TextView = findViewById(R.id.tv_order_details_id)
        tvSoldProductDetailsId.text = productDetails.order_id

        val dateFormat = "dd MMM yyyy HH:mm"
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())

        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = productDetails.order_date

        val tvSoldProductDetailsDate: TextView = findViewById(R.id.tv_order_details_date)
        tvSoldProductDetailsDate.text = formatter.format(calendar.time)

        val ivProductItemImage: ImageView = findViewById(R.id.iv_product_item_image)
        GlideLoader(this@SoldProductDetailsActivity).loadProductPicture(
            Uri.parse(productDetails.image),
            ivProductItemImage
        )

        val tvProductItemName: TextView = findViewById(R.id.tv_product_item_name)
        tvProductItemName.text = productDetails.title

        val tvProductItemPrice: TextView = findViewById(R.id.tv_product_item_price)
        tvProductItemPrice.text ="$${productDetails.price}"

        val tvSoldProductQuantity: TextView = findViewById(R.id.tv_sold_product_quantity)
        tvSoldProductQuantity.text = productDetails.sold_quantity

        val tvSoldDetailsAddressType: TextView = findViewById(R.id.tv_sold_details_address_type)
        tvSoldDetailsAddressType.text = productDetails.address.type

        val tvSoldDetailsFullName: TextView = findViewById(R.id.tv_sold_details_full_name)
        tvSoldDetailsFullName.text = productDetails.address.name

        val tvSoldDetailsAddress: TextView = findViewById(R.id.tv_sold_details_address)
        tvSoldDetailsAddress.text = "${productDetails.address.address}, ${productDetails.address.zipCode}"

        val tvSoldDetailsAdditionalNotes: TextView = findViewById(R.id.tv_sold_details_additional_note)
        tvSoldDetailsAdditionalNotes.text = productDetails.address.additionalNote

        val tvSoldDetailsOtherDetails: TextView = findViewById(R.id.tv_sold_details_other_details)
        if (productDetails.address.otherDetails.isNotEmpty()) {
            tvSoldDetailsOtherDetails.visibility = View.VISIBLE
            tvSoldDetailsOtherDetails.text = productDetails.address.otherDetails
        } else {
            tvSoldDetailsOtherDetails.visibility = View.GONE
        }

        val tvSoldDetailsMobileNumber: TextView = findViewById(R.id.tv_sold_details_mobile_number)
        tvSoldDetailsMobileNumber.text = productDetails.address.mobileNumber

        val tvSoldProductSubTotal: TextView = findViewById(R.id.tv_sold_product_sub_total)
        tvSoldProductSubTotal.text = productDetails.sub_total_amount

        val tvSoldProductShippingCharge: TextView = findViewById(R.id.tv_sold_product_shipping_charge)
        tvSoldProductShippingCharge.text = productDetails.shipping_charge

        val tvSoldProductTotalAmount: TextView = findViewById(R.id.tv_sold_product_total_amount)
        tvSoldProductTotalAmount.text = productDetails.total_amount
    }
}