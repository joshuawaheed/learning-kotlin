package com.joshuawaheed.myshopapp.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.media.Image
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.joshuawaheed.myshopapp.R
import com.joshuawaheed.myshopapp.firestore.FirestoreClass
import com.joshuawaheed.myshopapp.models.CartItem
import com.joshuawaheed.myshopapp.ui.activities.CartListActivity
import com.joshuawaheed.myshopapp.utils.Constants
import com.joshuawaheed.myshopapp.utils.GlideLoader

open class CartItemsListAdapter(
    private val context: Context,
    private var list: ArrayList<CartItem>,
    private val updateCartItems: Boolean
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return CartItemViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_cart_layout,
                parent,
                false
            )
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is CartItemViewHolder) {
            val ivCartItemImage: ImageView = holder.itemView.findViewById(R.id.iv_cart_item_image)
            GlideLoader(context).loadProductPicture(Uri.parse(model.image), ivCartItemImage)

            val tvCartItemTitle: TextView = holder.itemView.findViewById(R.id.tv_cart_item_title)
            tvCartItemTitle.text = model.title

            val tvCartItemPrice: TextView = holder.itemView.findViewById(R.id.tv_cart_item_price)
            tvCartItemPrice.text = "$${model.price}"

            val tvCartQuantity: TextView = holder.itemView.findViewById(R.id.tv_cart_quantity)
            val ibRemoveCartItem: ImageButton = holder.itemView.findViewById(R.id.ib_remove_cart_item)
            val ibAddCartItem: ImageButton = holder.itemView.findViewById(R.id.ib_add_cart_item)
            val ibDeleteCartItem: ImageButton = holder.itemView.findViewById(R.id.ib_delete_cart_item)

            if (model.cart_quantity == "0") {
                ibRemoveCartItem.visibility = View.GONE
                ibAddCartItem.visibility = View.GONE
                tvCartQuantity.text = context.resources.getString(R.string.lbl_out_of_stock)
                tvCartQuantity.setTextColor(ContextCompat.getColor(context, R.color.colorSnackBarError))

                if (updateCartItems) {
                    ibDeleteCartItem.visibility = View.VISIBLE
                } else {
                    ibDeleteCartItem.visibility = View.GONE
                }
            } else {
                if (updateCartItems) {
                    ibRemoveCartItem.visibility = View.VISIBLE
                    ibAddCartItem.visibility = View.VISIBLE
                    ibDeleteCartItem.visibility = View.VISIBLE
                } else {
                    ibRemoveCartItem.visibility = View.GONE
                    ibAddCartItem.visibility = View.GONE
                    ibDeleteCartItem.visibility = View.GONE
                }

                tvCartQuantity.text = model.cart_quantity
                tvCartQuantity.setTextColor(ContextCompat.getColor(context, R.color.colorSecondaryText))
            }

            ibDeleteCartItem.setOnClickListener {
                when (context) {
                    is CartListActivity -> {
                        context.showProgressDialog(context.resources.getString(R.string.please_wait))
                    }
                }

                FirestoreClass().removeItemFromCart(context, model.id)
            }

            ibRemoveCartItem.setOnClickListener {
                if (model.cart_quantity == "1") {
                    FirestoreClass().removeItemFromCart(context, model.id)
                } else {
                    val cartQuantity: Int = model.cart_quantity.toInt()
                    val itemHashMap = HashMap<String, Any>()
                    itemHashMap[Constants.CART_QUANTITY] = (cartQuantity - 1).toString()

                    if (context is CartListActivity) {
                        context.showProgressDialog(context.resources.getString(R.string.please_wait))
                    }

                    FirestoreClass().updateMyCart(context, model.id, itemHashMap)
                }
            }

            ibAddCartItem.setOnClickListener {
                val cartQuantity: Int = model.cart_quantity.toInt()

                if (cartQuantity < model.stock_quantity.toInt()) {
                    val itemHashMap = HashMap<String, Any>()
                    itemHashMap[Constants.CART_QUANTITY] = (cartQuantity + 1).toString()

                    if (context is CartListActivity) {
                        context.showProgressDialog(context.resources.getString(R.string.please_wait))
                    }

                    FirestoreClass().updateMyCart(context, model.id, itemHashMap)
                } else {
                    if (context is CartListActivity) {
                        context.showErrorSnackBar(
                            context.resources.getString(
                                R.string.msg_for_available_stock,
                                model.stock_quantity
                            ),
                            true
                        )
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private class CartItemViewHolder(view: View) : RecyclerView.ViewHolder(view)
}