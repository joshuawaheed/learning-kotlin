package com.joshuawaheed.myshopapp.ui.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.joshuawaheed.myshopapp.R
import com.joshuawaheed.myshopapp.firestore.FirestoreClass
import com.joshuawaheed.myshopapp.models.Address
import com.joshuawaheed.myshopapp.ui.adapters.AddressListAdapter
import com.joshuawaheed.myshopapp.utils.Constants
import com.joshuawaheed.myshopapp.utils.SwipeToDeleteCallback
import com.joshuawaheed.myshopapp.utils.SwipeToEditCallback

class AddressListActivity : BaseActivity() {
    private var mSelectAddress: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_list)

        if (intent.hasExtra(Constants.EXTRA_SELECT_ADDRESS)) {
            mSelectAddress = intent.getBooleanExtra(Constants.EXTRA_SELECT_ADDRESS, false)
        }

        setupActionBar()

        if (mSelectAddress) {
            findViewById<TextView>(R.id.tv_title).text = resources.getString(R.string.title_select_address)
        }

        findViewById<TextView>(R.id.tv_add_address).setOnClickListener {
            val intent = Intent(this@AddressListActivity, AddEditAddressActivity::class.java)
            startActivityForResult(intent, Constants.ADD_ADDRESS_REQUEST_CODE)
        }

        getAddressList()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.ADD_ADDRESS_REQUEST_CODE) {
                getAddressList()
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.e("Request Cancelled", "To add the address.")
        }
    }

    private fun setupActionBar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar_address_list_activity)
        setSupportActionBar(toolbar)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun getAddressList() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getAddressesList(this@AddressListActivity)
    }

    fun successAddressListFromFirestore(addressList: ArrayList<Address>) {
        hideProgressDialog()

        if (addressList.size > 0) {
            val rvAddressList: RecyclerView = findViewById(R.id.rv_address_list)
            rvAddressList.visibility = View.VISIBLE

            val tvNoAddressFound: TextView = findViewById(R.id.tv_no_address_found)
            tvNoAddressFound.visibility = View.GONE

            rvAddressList.layoutManager = LinearLayoutManager(this@AddressListActivity)
            rvAddressList.setHasFixedSize(true)

            val addressAdapter = AddressListAdapter(this@AddressListActivity, addressList, mSelectAddress)
            rvAddressList.adapter = addressAdapter

            if (!mSelectAddress) {
                val editSwipeHandler = object : SwipeToEditCallback(this) {
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        val adapter = rvAddressList.adapter as AddressListAdapter
                        adapter.notifyEditItem(this@AddressListActivity, viewHolder.adapterPosition)
                    }
                }

                val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
                editItemTouchHelper.attachToRecyclerView(rvAddressList)

                val deleteSwipeHandler = object : SwipeToDeleteCallback(this) {
                    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                        showProgressDialog(resources.getString(R.string.please_wait))

                        FirestoreClass().deleteAddress(
                            this@AddressListActivity,
                            addressList[viewHolder.adapterPosition].id
                        )
                    }
                }

                val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
                deleteItemTouchHelper.attachToRecyclerView(rvAddressList)
            }
        } else {
            val rvAddressList: RecyclerView = findViewById(R.id.rv_address_list)
            rvAddressList.visibility = View.GONE

            val tvNoAddressFound: TextView = findViewById(R.id.tv_no_address_found)
            tvNoAddressFound.visibility = View.VISIBLE
        }
    }

    fun deleteAddressSuccess() {
        hideProgressDialog()

        Toast.makeText(
            this@AddressListActivity,
            resources.getString(R.string.err_your_address_deleted_successfully),
            Toast.LENGTH_SHORT
        ).show()

        getAddressList()
    }
}