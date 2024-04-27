package com.joshuawaheed.myshopapp.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.joshuawaheed.myshopapp.R
import com.joshuawaheed.myshopapp.databinding.FragmentOrdersBinding
import com.joshuawaheed.myshopapp.firestore.FirestoreClass
import com.joshuawaheed.myshopapp.models.Order
import com.joshuawaheed.myshopapp.ui.adapters.MyOrdersListAdapter

class OrdersFragment : BaseFragment() {

    private var _binding: FragmentOrdersBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrdersBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        getMyOrdersList()
    }

    private fun getMyOrdersList() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getMyOrdersList(this@OrdersFragment)
    }

    fun populateOrdersListInUI(ordersList: ArrayList<Order>) {
        hideProgressDialog()

        val rvMyOrderItems: RecyclerView = binding.root.findViewById(R.id.rv_my_order_items)
        val tvNoOrdersFound: TextView = binding.root.findViewById(R.id.tv_no_orders_found)

        if (ordersList.size > 0) {
            rvMyOrderItems.visibility = View.VISIBLE
            tvNoOrdersFound.visibility = View.GONE
            rvMyOrderItems.layoutManager = LinearLayoutManager(activity)
            rvMyOrderItems.setHasFixedSize(true)
            val myOrdersAdapter = MyOrdersListAdapter(requireActivity(), ordersList)
            rvMyOrderItems.adapter = myOrdersAdapter
        } else {
            rvMyOrderItems.visibility = View.GONE
            tvNoOrdersFound.visibility = View.VISIBLE
        }
    }
}