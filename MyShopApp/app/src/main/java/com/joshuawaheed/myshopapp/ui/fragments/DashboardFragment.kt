package com.joshuawaheed.myshopapp.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.joshuawaheed.myshopapp.R
import com.joshuawaheed.myshopapp.ui.adapters.DashboardAdapter
import com.joshuawaheed.myshopapp.ui.adapters.ProductAdapter
import com.joshuawaheed.myshopapp.databinding.FragmentDashboardBinding
import com.joshuawaheed.myshopapp.firestore.FirestoreClass
import com.joshuawaheed.myshopapp.models.Product
import com.joshuawaheed.myshopapp.ui.activities.CartListActivity
import com.joshuawaheed.myshopapp.ui.activities.ProductDetailsActivity
import com.joshuawaheed.myshopapp.ui.activities.SettingsActivity
import com.joshuawaheed.myshopapp.utils.Constants

class DashboardFragment : BaseFragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when (id) {
            R.id.action_settings -> {
                startActivity(Intent(activity, SettingsActivity::class.java))
                return true
            }
            R.id.action_cart -> {
                startActivity(Intent(activity, CartListActivity::class.java))
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.dashboard_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        getDashboardItemsList()
    }

    fun successDashboardItemsList(dashboardItemsList: ArrayList<Product>) {
        hideProgressDialog()

        val rvDashboardItems: RecyclerView = binding.root.findViewById(R.id.rv_dashboard_items)
        val tvNoDashbaordItemsFound: TextView = binding.root.findViewById(R.id.tv_no_dashboard_items_found)

        if (dashboardItemsList.size > 0) {
            rvDashboardItems.visibility = View.VISIBLE
            rvDashboardItems.layoutManager = GridLayoutManager(activity, 2)
            rvDashboardItems.setHasFixedSize(true)
            val productAdapter = DashboardAdapter(requireActivity(), dashboardItemsList)
            rvDashboardItems.adapter = productAdapter
            tvNoDashbaordItemsFound.visibility = View.GONE

            productAdapter.setOnClickListener(object: DashboardAdapter.OnClickListener {
                override fun onClick(position: Int, product: Product) {
                    val intent = Intent(context, ProductDetailsActivity::class.java)
                    intent.putExtra(Constants.EXTRA_PRODUCT_ID, product.product_id)
                    intent.putExtra(Constants.EXTRA_PRODUCT_OWNER_ID, product.user_id)
                    startActivity(intent)
                }
            })
        } else {
            rvDashboardItems.visibility = View.GONE
            tvNoDashbaordItemsFound.visibility = View.VISIBLE
        }
    }

    private fun getDashboardItemsList() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getDashboardItemsList(this@DashboardFragment)
    }
}