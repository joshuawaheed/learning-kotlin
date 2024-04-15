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
import com.joshuawaheed.myshopapp.models.Product
import com.joshuawaheed.myshopapp.ui.activities.ProductDetailsActivity
import com.joshuawaheed.myshopapp.ui.fragments.ProductsFragment
import com.joshuawaheed.myshopapp.utils.Constants
import com.joshuawaheed.myshopapp.utils.GlideLoader

class ProductAdapter(
    private val context: Context,
    private val products: ArrayList<Product>,
    private val fragment: ProductsFragment
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater
            .from(context)
            .inflate(R.layout.item_product, parent, false)

        return ProductViewHolder(view)
    }

    override fun getItemCount(): Int {
        return products.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = products[position]

        if (holder is ProductViewHolder) {
            val ivItemImage: ImageView = holder.itemView.findViewById(R.id.iv_item_image)
            GlideLoader(context).loadProductPicture(Uri.parse(model.image), ivItemImage)

            val tvItemName: TextView = holder.itemView.findViewById(R.id.tv_item_name)
            tvItemName.text = model.title

            val tvItemPrice: TextView = holder.itemView.findViewById(R.id.tv_item_price)
            tvItemPrice.text = "$" + model.price

            val ibDeleteProduct: ImageButton = holder.itemView.findViewById(R.id.ib_delete_product)
            ibDeleteProduct.setOnClickListener {
                fragment.deleteProduct(model.product_id)
            }

            holder.itemView.setOnClickListener {
                val intent = Intent(context, ProductDetailsActivity::class.java)
                intent.putExtra(Constants.EXTRA_PRODUCT_ID, model.product_id)
                intent.putExtra(Constants.EXTRA_PRODUCT_OWNER_ID, model.user_id)
                context.startActivity(intent)
            }
        }
    }

    private class ProductViewHolder(view: View): RecyclerView.ViewHolder(view)
}