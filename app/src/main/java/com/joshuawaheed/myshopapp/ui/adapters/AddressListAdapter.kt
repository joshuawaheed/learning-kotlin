package com.joshuawaheed.myshopapp.ui.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.joshuawaheed.myshopapp.R
import com.joshuawaheed.myshopapp.models.Address
import com.joshuawaheed.myshopapp.ui.activities.AddEditAddressActivity
import com.joshuawaheed.myshopapp.utils.Constants

open class AddressListAdapter(
    private val context: Context,
    private var list: ArrayList<Address>,
    private val selectAddress: Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_address_layout,
                parent,
                false
            )
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {
            val tvAddressFullName: TextView = holder.itemView.findViewById(R.id.tv_address_full_name)
            tvAddressFullName.text = model.name

            val tvAddressType: TextView = holder.itemView.findViewById(R.id.tv_address_type)
            tvAddressType.text = model.type

            val tvAddressDetails: TextView = holder.itemView.findViewById(R.id.tv_address_details)
            tvAddressDetails.text = "${model.address}, ${model.zipCode}"

            val tvAddressMobileNumber: TextView = holder.itemView.findViewById(R.id.tv_address_mobile_number)
            tvAddressMobileNumber.text = model.mobileNumber

            if (selectAddress) {
                holder.itemView.setOnClickListener {
                    Toast.makeText(
                        context,
                        "Selected address: ${model.address} ${model.zipCode}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun notifyEditItem(activity: Activity, position: Int) {
        val intent = Intent(context, AddEditAddressActivity::class.java)
        intent.putExtra(Constants.EXTRA_ADDRESS_DETAILS, list[position])
        activity.startActivityForResult(intent, Constants.ADD_ADDRESS_REQUEST_CODE)
        notifyItemChanged(position)
    }

    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}