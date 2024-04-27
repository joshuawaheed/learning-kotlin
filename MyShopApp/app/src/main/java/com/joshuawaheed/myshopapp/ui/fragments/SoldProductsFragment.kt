package com.joshuawaheed.myshopapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.joshuawaheed.myshopapp.R
import com.joshuawaheed.myshopapp.firestore.FirestoreClass
import com.joshuawaheed.myshopapp.models.SoldProduct
import com.joshuawaheed.myshopapp.ui.adapters.SoldProductsListAdapter

class SoldProductsFragment : BaseFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sold_products, container, false)
    }

    override fun onResume() {
        super.onResume()
        getSoldProductsList()
    }

    private fun getSoldProductsList() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getSoldProductsList(this@SoldProductsFragment)
    }

    fun successSoldProductsList(soldProductsList: ArrayList<SoldProduct>) {
        hideProgressDialog()

        val rvSoldProductItems: RecyclerView = requireActivity().findViewById(R.id.rv_sold_product_items)
        val tvNoSoldProductsFound: TextView = requireActivity().findViewById(R.id.tv_no_sold_products_found)

        if (soldProductsList.size > 0) {
            rvSoldProductItems.visibility = View.VISIBLE
            tvNoSoldProductsFound.visibility = View.GONE
            rvSoldProductItems.layoutManager = LinearLayoutManager(activity)
            rvSoldProductItems.setHasFixedSize(true)
            val soldProductsListAdapter = SoldProductsListAdapter(requireActivity(), soldProductsList)
            rvSoldProductItems.adapter = soldProductsListAdapter
        } else {
            rvSoldProductItems.visibility = View.GONE
            tvNoSoldProductsFound.visibility = View.VISIBLE
        }
    }
}