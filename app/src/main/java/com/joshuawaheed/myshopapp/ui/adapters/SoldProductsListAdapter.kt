package com.joshuawaheed.myshopapp.ui.adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.joshuawaheed.myshopapp.R
import com.joshuawaheed.myshopapp.models.SoldProduct
import com.joshuawaheed.myshopapp.ui.activities.SoldProductDetailsActivity
import com.joshuawaheed.myshopapp.utils.Constants
import com.joshuawaheed.myshopapp.utils.GlideLoader

open class SoldProductsListAdapter(
    private val context: Context,
    private var list: ArrayList<SoldProduct>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_product,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {
            val ivItemImage: ImageView = holder.itemView.findViewById(R.id.iv_item_image)
            GlideLoader(context).loadProductPicture(Uri.parse(model.image), ivItemImage)

            val tvItemName: TextView = holder.itemView.findViewById(R.id.tv_item_name)
            tvItemName.text = model.title

            val tvItemPrice: TextView = holder.itemView.findViewById(R.id.tv_item_price)
            tvItemPrice.text = "$${model.price}"

            val ibDeleteProduct: ImageButton = holder.itemView.findViewById(R.id.ib_delete_product)
            ibDeleteProduct.visibility = View.GONE

            holder.itemView.setOnClickListener {
                val intent = Intent(context, SoldProductDetailsActivity::class.java)
                intent.putExtra(Constants.EXTRA_SOLD_PRODUCT_DETAILS, model)
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}