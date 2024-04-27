package com.joshuawaheed.myshopapp.ui.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.joshuawaheed.myshopapp.R
import com.joshuawaheed.myshopapp.models.Product
import com.joshuawaheed.myshopapp.utils.GlideLoader

class DashboardAdapter(
    private val context: Context,
    private val products: ArrayList<Product>
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var onClickListener: OnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater
            .from(context)
            .inflate(R.layout.item_dashboard, parent, false)

        return DashboardViewHolder(view)
    }

    override fun getItemCount(): Int {
        return products.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = products[position]

        if (holder is DashboardViewHolder) {
            val ivItemImage: ImageView = holder.itemView.findViewById(R.id.iv_dashboard_item_image)
            GlideLoader(context).loadProductPicture(Uri.parse(model.image), ivItemImage)

            val tvItemName: TextView = holder.itemView.findViewById(R.id.tv_dashboard_item_name)
            tvItemName.text = model.title

            val tvItemPrice: TextView = holder.itemView.findViewById(R.id.tv_dashboard_item_price)
            tvItemPrice.text = "$" + model.price

            holder.itemView.setOnClickListener {
                if (onClickListener != null) {
                    onClickListener!!.onClick(position, model)
                }
            }
        }
    }

    fun setOnClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    private class DashboardViewHolder(view: View): RecyclerView.ViewHolder(view)

    interface OnClickListener {
        fun onClick(position: Int, product: Product)
    }
}