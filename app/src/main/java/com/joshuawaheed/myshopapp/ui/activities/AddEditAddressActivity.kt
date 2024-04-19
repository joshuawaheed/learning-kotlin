package com.joshuawaheed.myshopapp.ui.activities

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.textfield.TextInputLayout
import com.joshuawaheed.myshopapp.R
import com.joshuawaheed.myshopapp.firestore.FirestoreClass
import com.joshuawaheed.myshopapp.models.Address
import com.joshuawaheed.myshopapp.utils.Constants

class AddEditAddressActivity : BaseActivity() {
    private var mAddressDetails: Address? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_address)
        setupActionBar()

        if (intent.hasExtra(Constants.EXTRA_ADDRESS_DETAILS)) {
            mAddressDetails = intent.getParcelableExtra(Constants.EXTRA_ADDRESS_DETAILS)!!
        }

        if (mAddressDetails != null) {
            if (mAddressDetails!!.id.isNotEmpty()) {
                findViewById<TextView>(R.id.tv_title).text = resources.getString(R.string.title_edit_address)
                findViewById<Button>(R.id.btn_submit_address).text = resources.getString(R.string.btn_lbl_update)
                findViewById<TextView>(R.id.et_full_name).text = mAddressDetails?.name
                findViewById<TextView>(R.id.et_phone_number).text = mAddressDetails?.mobileNumber
                findViewById<TextView>(R.id.et_address).text = mAddressDetails?.address
                findViewById<TextView>(R.id.et_zip_code).text = mAddressDetails?.zipCode
                findViewById<TextView>(R.id.et_additional_note).text = mAddressDetails?.additionalNote

                when (mAddressDetails?.type) {
                    Constants.HOME -> {
                        findViewById<RadioButton>(R.id.rb_home).isChecked = true
                    }
                    Constants.OFFICE -> {
                        findViewById<RadioButton>(R.id.rb_office).isChecked = true
                    }
                    else -> {
                        findViewById<RadioButton>(R.id.rb_other).isChecked = true
                        findViewById<TextInputLayout>(R.id.til_other_details).visibility = View.VISIBLE
                        findViewById<EditText>(R.id.et_other_details).setText(mAddressDetails?.otherDetails)
                    }
                }
            }
        }

        findViewById<Button>(R.id.btn_submit_address).setOnClickListener {
            saveAddressToFirestore()
        }

        findViewById<RadioGroup>(R.id.rg_type).setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rb_other) {
                findViewById<TextInputLayout>(R.id.til_other_details).visibility = View.VISIBLE
            } else {
                findViewById<TextInputLayout>(R.id.til_other_details).visibility = View.GONE
            }
        }
    }

    private fun setupActionBar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar_add_edit_address_activity)
        setSupportActionBar(toolbar)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
        }

        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun validateData(): Boolean {
        return when {
            TextUtils.isEmpty(findViewById<EditText>(R.id.et_full_name).text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_please_enter_full_name),
                    true
                )
                false
            }
            TextUtils.isEmpty(findViewById<EditText>(R.id.et_phone_number).text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_please_enter_phone_number),
                    true
                )
                false
            }
            TextUtils.isEmpty(findViewById<EditText>(R.id.et_address).text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_address), true)
                false
            }
            TextUtils.isEmpty(findViewById<EditText>(R.id.et_zip_code).text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_zip_code), true)
                false
            }
            findViewById<RadioButton>(R.id.rb_other).isChecked &&
                    TextUtils.isEmpty(findViewById<EditText>(R.id.et_zip_code).text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_zip_code), true)
                false
            }
            else -> {
                true
            }
        }
    }

    private fun saveAddressToFirestore() {
        val fullName: String = findViewById<EditText>(R.id.et_full_name).text.toString().trim { it <= ' ' }
        val phoneNumber: String = findViewById<EditText>(R.id.et_phone_number).text.toString().trim { it <= ' ' }
        val address: String = findViewById<EditText>(R.id.et_address).text.toString().trim { it <= ' ' }
        val zipCode: String = findViewById<EditText>(R.id.et_zip_code).text.toString().trim { it <= ' ' }
        val additionalNote: String = findViewById<EditText>(R.id.et_additional_note).text.toString().trim { it <= ' ' }
        val otherDetails: String = findViewById<EditText>(R.id.et_other_details).text.toString().trim { it <= ' ' }

        if (validateData()) {
            showProgressDialog(resources.getString(R.string.please_wait))

            val addressType: String = when {
                findViewById<RadioButton>(R.id.rb_home).isChecked -> {
                    Constants.HOME
                }
                findViewById<RadioButton>(R.id.rb_office).isChecked -> {
                    Constants.OFFICE
                }
                else -> {
                    Constants.OTHER
                }
            }

            val addressModel = Address(
                FirestoreClass().getCurrentUserID(),
                fullName,
                phoneNumber,
                address,
                zipCode,
                additionalNote,
                addressType,
                otherDetails
            )

            if (mAddressDetails != null && mAddressDetails!!.id.isNotEmpty()) {
                FirestoreClass().updateAddressDetails(
                    this@AddEditAddressActivity,
                    addressModel,
                    mAddressDetails!!.id
                )
            } else {
                FirestoreClass().addAddress(this@AddEditAddressActivity, addressModel)
            }
        }
    }

    fun addUpdateAddressSuccess() {
        hideProgressDialog()

        val notifySuccessMessage: String = if (mAddressDetails != null && mAddressDetails!!.id.isNotEmpty()) {
            resources.getString(R.string.msg_your_address_updated_successfully)
        } else {
            resources.getString(R.string.err_your_address_added_successfully)
        }

        Toast.makeText(
            this@AddEditAddressActivity,
            notifySuccessMessage,
            Toast.LENGTH_SHORT
        ).show()

        setResult(RESULT_OK)
        finish()
    }
}