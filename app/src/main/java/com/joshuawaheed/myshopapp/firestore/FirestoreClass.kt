package com.joshuawaheed.myshopapp.firestore

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Log
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.joshuawaheed.myshopapp.models.Address
import com.joshuawaheed.myshopapp.models.CartItem
import com.joshuawaheed.myshopapp.models.Product
import com.joshuawaheed.myshopapp.ui.activities.LoginActivity
import com.joshuawaheed.myshopapp.ui.activities.RegisterActivity
import com.joshuawaheed.myshopapp.ui.activities.UserProfileActivity
import com.joshuawaheed.myshopapp.models.User
import com.joshuawaheed.myshopapp.ui.activities.AddEditAddressActivity
import com.joshuawaheed.myshopapp.ui.activities.AddProductActivity
import com.joshuawaheed.myshopapp.ui.activities.AddressListActivity
import com.joshuawaheed.myshopapp.ui.activities.CartListActivity
import com.joshuawaheed.myshopapp.ui.activities.ProductDetailsActivity
import com.joshuawaheed.myshopapp.ui.activities.SettingsActivity
import com.joshuawaheed.myshopapp.ui.fragments.DashboardFragment
import com.joshuawaheed.myshopapp.ui.fragments.ProductsFragment
import com.joshuawaheed.myshopapp.utils.Constants

class FirestoreClass {
    private val mFirestore = FirebaseFirestore.getInstance()

    fun registerUser(activity: RegisterActivity, userInfo: User) {
        mFirestore
            .collection(Constants.USERS)
            .document(userInfo.id)
            .set(userInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegistrationSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while registering the user.", e)
            }
    }

    fun getCurrentUserID(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserID = ""

        if (currentUser != null) {
            currentUserID = currentUser.uid
        }

        return currentUserID
    }

    fun getUserDetails(activity: Activity) {
        mFirestore
            .collection(Constants.USERS)
            .document(getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.i(activity.javaClass.simpleName, document.toString())

                val user = document.toObject(User::class.java)!!

                val sharedPreferences = activity.getSharedPreferences(
                    Constants.MYSHOPAPP_PREFERENCES,
                    Context.MODE_PRIVATE
                )

                val editor: SharedPreferences.Editor = sharedPreferences.edit()
                editor.putString(Constants.LOGGED_IN_USERNAME, "${user.firstName} ${user.lastName}")
                editor.apply()

                when (activity) {
                    is LoginActivity -> {
                        activity.userLoggedInSuccess(user)
                    }
                    is SettingsActivity ->{
                        activity.userDetailsSuccess(user)
                    }
                }
            }
            .addOnFailureListener { e ->
                when (activity) {
                    is LoginActivity -> {
                        activity.hideProgressDialog()
                    }
                    is SettingsActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(activity.javaClass.simpleName, "Error while getting user details.", e)
            }
    }

    fun updateUserProfileData(activity: Activity, userHashMap: HashMap<String, Any>) {
        mFirestore
            .collection(Constants.USERS)
            .document(getCurrentUserID())
            .update(userHashMap)
            .addOnSuccessListener {
                when (activity) {
                    is UserProfileActivity -> {
                        activity.userProfileUpdateSuccess()
                    }
                }
            }
            .addOnFailureListener { e ->
                when (activity) {
                    is UserProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(activity.javaClass.simpleName, "Error while updating the user details.", e)
            }
    }

    fun uploadProductDetails(activity: AddProductActivity, productInfo: Product) {
        mFirestore
            .collection(Constants.PRODUCTS)
            .document()
            .set(productInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.productUploadSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(activity.javaClass.simpleName, "Error while uploading the product details.", e)
            }
    }

    fun uploadImageToCloudStorage(activity: Activity, imageFileURI: Uri?, imageType: String) {
        val sRef: StorageReference = FirebaseStorage.getInstance().reference.child(
            imageType +
            System.currentTimeMillis() + "." +
            Constants.getFileExtension(activity, imageFileURI)
        )

        sRef.putFile(imageFileURI!!)
            .addOnSuccessListener { taskSnapshot ->
                Log.e(
                    "Firebase Image URL",
                    taskSnapshot.metadata!!.reference!!.downloadUrl.toString()
                )

                taskSnapshot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { uri ->
                        Log.e("Downloadable Image URL", uri.toString())

                        when (activity) {
                            is UserProfileActivity -> {
                                activity.imageUploadSuccess(uri.toString())
                            }
                            is AddProductActivity -> {
                                activity.imageUploadSuccess(uri.toString())
                            }
                        }
                    }
            }
            .addOnFailureListener { exception ->
                when (activity) {
                    is UserProfileActivity -> {
                        activity.hideProgressDialog()
                    }
                    is AddProductActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    exception.message,
                    exception
                )
            }
    }

    fun getProductsList(fragment: Fragment) {
        mFirestore
            .collection(Constants.PRODUCTS)
            .whereEqualTo(Constants.PRODUCT_USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.e("Products list: ", document.documents.toString())

                val productsList: ArrayList<Product> = ArrayList()

                for (i in document.documents) {
                    val product = i.toObject(Product::class.java)
                    product!!.product_id = i.id
                    productsList.add(product)
                }

                when (fragment) {
                    is ProductsFragment -> {
                        fragment.successProductsListFromFirestore(productsList)
                    }
                }
            }
            .addOnFailureListener { exception ->
                when (fragment) {
                    is ProductsFragment -> {
                        fragment.hideProgressDialog()
                    }
                }

                Log.e(
                    fragment.javaClass.simpleName,
                    exception.message,
                    exception
                )
            }
    }

    fun getDashboardItemsList(fragment: DashboardFragment) {
        mFirestore
            .collection(Constants.PRODUCTS)
            .get()
            .addOnSuccessListener { document ->
                Log.i(fragment.javaClass.simpleName, document.documents.toString())

                val productsList: ArrayList<Product> = ArrayList()

                for (i in document.documents) {
                    val product = i.toObject(Product::class.java)!!
                    product.product_id = i.id
                    productsList.add(product)
                }

                fragment.successDashboardItemsList(productsList)
            }
            .addOnFailureListener { e ->
                fragment.hideProgressDialog()
                Log.e(fragment.javaClass.simpleName, "Error while getting dashboard items list.", e)
            }
    }

    fun getProductDetails(activity: ProductDetailsActivity, productId: String) {
        mFirestore
            .collection(Constants.PRODUCTS)
            .document(productId)
            .get()
            .addOnSuccessListener { document ->
                Log.i(document.javaClass.simpleName, document.toString())
                val product = document.toObject(Product::class.java)

                if (product != null) {
                    activity.productDetailsSuccess(product)
                }
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting the product.",
                    e
                )
            }
    }

    fun deleteProduct(fragment: ProductsFragment, productId: String) {
        mFirestore
            .collection(Constants.PRODUCTS)
            .document(productId)
            .delete()
            .addOnSuccessListener {
                fragment.productDeleteSuccess()
            }
            .addOnFailureListener { e ->
                fragment.hideProgressDialog()

                Log.e(
                    fragment.requireActivity().javaClass.simpleName,
                    "Error while deleting the product",
                    e
                )
            }
    }

    fun addCartItems(activity: ProductDetailsActivity, cartItem: CartItem) {
        mFirestore
            .collection(Constants.CART_ITEMS)
            .document()
            .set(cartItem, SetOptions.merge())
            .addOnSuccessListener {
                activity.addToCartSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while creating the document for cart item.",
                    e
                )
            }
    }

    fun checkIfItemExistInCart(activity: ProductDetailsActivity, productId: String) {
        mFirestore
            .collection(Constants.CART_ITEMS)
            .whereEqualTo(Constants.PRODUCT_USER_ID, getCurrentUserID())
            .whereEqualTo(Constants.PRODUCT_ID, productId)
            .get()
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.documents.toString())

                if (document.documents.size > 0) {
                    activity.productExistsInCart()
                } else {
                    activity.hideProgressDialog()
                }
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while checking the existing cart list.",
                    e
                )
            }
    }

    fun getCartList(activity: Activity) {
        mFirestore
            .collection(Constants.CART_ITEMS)
            .whereEqualTo(Constants.PRODUCT_USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.documents.toString())

                val list: ArrayList<CartItem> = ArrayList()

                for (i in document.documents) {
                    val cartItem = i.toObject(CartItem::class.java)!!
                    cartItem.id = i.id
                    list.add(cartItem)
                }

                when (activity) {
                    is CartListActivity -> {
                        activity.successCartItemsList(list)
                    }
                }
            }
            .addOnFailureListener { e ->
                when (activity) {
                    is CartListActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting the cart list items.",
                    e
                )
            }
    }

    fun getAllProductsList(activity: CartListActivity) {
        mFirestore
            .collection(Constants.PRODUCTS)
            .get()
            .addOnSuccessListener { document ->
                Log.e("Products List", document.documents.toString())

                val productsList: ArrayList<Product> = ArrayList()

                for (i in document.documents) {
                    val product = i.toObject(Product::class.java)
                    product!!.product_id = i.id
                    productsList.add(product)
                }

                activity.successProductsListFromFireStore(productsList)
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()

                Log.e(
                    "Get Product List",
                    "Error while getting all product list.",
                    e
                )
            }
    }

    fun removeItemFromCart(context: Context, cart_id: String) {
        mFirestore
            .collection(Constants.CART_ITEMS)
            .document(cart_id)
            .delete()
            .addOnSuccessListener {
                when (context) {
                    is CartListActivity -> {
                        context.itemRemovedSuccess()
                    }
                }
            }
            .addOnFailureListener { e ->
                when (context) {
                    is CartListActivity -> {
                        context.hideProgressDialog()
                    }
                }
                Log.e(
                    context.javaClass.simpleName,
                    "Error while removing the item from the cart list.",
                    e
                )
            }
    }

    fun updateMyCart(context: Context, cart_id: String, itemHashMap: HashMap<String, Any>) {
        mFirestore
            .collection(Constants.CART_ITEMS)
            .document(cart_id)
            .update(itemHashMap)
            .addOnSuccessListener {
                when (context) {
                    is CartListActivity -> {
                        context.itemUpdateSuccess()
                    }
                }
            }
            .addOnFailureListener { e ->
                when (context) {
                    is CartListActivity -> {
                        context.hideProgressDialog()
                    }
                }

                Log.e(
                    context.javaClass.simpleName,
                    "Error while updating the cart item.",
                    e
                )
            }
    }

    fun addAddress(activity: AddEditAddressActivity, addressInfo: Address) {
        mFirestore
            .collection(Constants.ADDRESSES)
            .document()
            .set(addressInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.addUpdateAddressSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while adding the address.",
                    e
                )
            }
    }

    fun getAddressesList(activity: AddressListActivity) {
        mFirestore
            .collection(Constants.ADDRESSES)
            .whereEqualTo(Constants.PRODUCT_USER_ID, getCurrentUserID())
            .get()
            .addOnSuccessListener { document ->
                Log.e(activity.javaClass.simpleName, document.documents.toString())

                val addressList: ArrayList<Address> = ArrayList()

                for (i in document.documents) {
                    val address = i.toObject(Address::class.java)!!
                    address.id = i.id
                    addressList.add(address)
                }

                activity.successAddressListFromFirestore(addressList)
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while getting the address list.",
                    e
                )
            }
    }

    fun updateAddressDetails(activity: AddEditAddressActivity, addressInfo: Address, addressId: String) {
        mFirestore
            .collection(Constants.ADDRESSES)
            .document(addressId)
            .set(addressInfo, SetOptions.merge())
            .addOnSuccessListener {
                activity.addUpdateAddressSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()

                Log.e(
                    activity.javaClass.simpleName,
                    "Error while updating the address.",
                    e
                )
            }
    }

    fun deleteAddress(activity: AddressListActivity, addressId: String) {
        mFirestore
            .collection(Constants.ADDRESSES)
            .document(addressId)
            .delete()
            .addOnSuccessListener {
                activity.deleteAddressSuccess()
            }
            .addOnFailureListener { e ->
                activity.hideProgressDialog()
                Log.e(
                    activity.javaClass.simpleName,
                    "Error while deleting the address.",
                    e
                )
            }
    }
}