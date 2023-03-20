package com.joshuawaheed.projemanag.firebase

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.joshuawaheed.projemanag.activities.MainActivity
import com.joshuawaheed.projemanag.activities.MyProfileActivity
import com.joshuawaheed.projemanag.activities.SignInActivity
import com.joshuawaheed.projemanag.activities.SignUpActivity
import com.joshuawaheed.projemanag.models.User
import com.joshuawaheed.projemanag.utils.Constants

class FirestoreClass {
    private val mFirestore = FirebaseFirestore.getInstance()

    fun getCurrentUserId(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser
        var currentUserId = ""

        if (currentUser != null) {
            currentUserId = currentUser.uid
        }

        return currentUserId
    }

    fun loadUserData(activity: Activity) {
        mFirestore
            .collection(Constants.USERS)
            .document(getCurrentUserId())
            .get()
            .addOnSuccessListener {
                document ->
                val loggedInUser = document.toObject(User::class.java)!!

                when (activity) {
                    is SignInActivity -> {
                        activity.signInSuccess(loggedInUser)
                    }
                    is MainActivity -> {
                        activity.updateNavigationUserDetails(loggedInUser)
                    }
                    is MyProfileActivity -> {
                        activity.setUserDataInUI(loggedInUser)
                    }
                }
            }
            .addOnFailureListener {
                e ->
                when (activity) {
                    is SignInActivity -> {
                        activity.hideProgressDialog()
                    }
                    is MainActivity -> {
                        activity.hideProgressDialog()
                    }
                }

                Log.e(activity.javaClass.simpleName, "Error reading document", e)
            }
    }

    fun registerUser(activity: SignUpActivity, user: User) {
        mFirestore
            .collection(Constants.USERS)
            .document(getCurrentUserId())
            .set(user, SetOptions.merge())
            .addOnSuccessListener {
                activity.userRegisteredSuccess()
            }
            .addOnFailureListener {
                    e ->
                Log.e(activity.javaClass.simpleName, "Error writing document", e)
            }
    }

    fun updateUserProfileData(activity: MyProfileActivity, userHashMap: HashMap<String, Any>) {
        mFirestore
            .collection(Constants.USERS)
            .document(getCurrentUserId())
            .update(userHashMap)
            .addOnSuccessListener {
                Log.i(activity.javaClass.simpleName, "Profile data updated successfully.")

                Toast.makeText(
                    activity,
                    "Profile update successfully",
                    Toast.LENGTH_LONG
                ).show()

                activity.profileUpdateSuccess()
            }
            .addOnFailureListener {
                activity.hideProgressDialog()

                Toast.makeText(
                    activity,
                    "Error while updating the profile data.",
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}