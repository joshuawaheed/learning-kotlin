package com.joshuawaheed.projemanag.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.joshuawaheed.projemanag.R
import com.joshuawaheed.projemanag.adapters.BoardItemsAdapter
import com.joshuawaheed.projemanag.firebase.FirestoreClass
import com.joshuawaheed.projemanag.models.Board
import com.joshuawaheed.projemanag.models.User
import com.joshuawaheed.projemanag.utils.Constants

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    companion object {
        const val MY_PROFILE_REQUEST_CODE: Int = 11
        const val CREATE_BOARD_REQUEST_CODE: Int = 12
    }

    private lateinit var mDrawer: DrawerLayout
    private lateinit var mNavigation: NavigationView
    private lateinit var mToolbar: Toolbar
    private lateinit var mUserName: String

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (
            resultCode == Activity.RESULT_OK &&
            requestCode == MY_PROFILE_REQUEST_CODE
        ) {
            FirestoreClass().loadUserData(this)
        } else if (
            resultCode == Activity.RESULT_OK &&
            requestCode == CREATE_BOARD_REQUEST_CODE
        ) {
            FirestoreClass().getBoardsList(this)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START)
        } else {
            doubleBackToExit()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        mDrawer = findViewById(R.id.drawer_layout)
        mNavigation = findViewById(R.id.nav_view)
        mToolbar = findViewById(R.id.toolbar_main_activity)

        setupActionBar()

        mNavigation.setNavigationItemSelectedListener(this)

        FirestoreClass().loadUserData(this, true)

        val fabCreateBoard: FloatingActionButton = findViewById(R.id.fab_create_board)

        fabCreateBoard.setOnClickListener {
            val intent = Intent(this, CreateBoardActivity::class.java)
            intent.putExtra(Constants.NAME, mUserName)
            startActivityForResult(intent, CREATE_BOARD_REQUEST_CODE)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_my_profile -> {
                startActivityForResult(
                    Intent(this, MyProfileActivity::class.java),
                    MY_PROFILE_REQUEST_CODE
                )
            }
            R.id.nav_sign_out -> {
                FirebaseAuth.getInstance().signOut()

                val intent = Intent(this, IntroActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }

        mDrawer.openDrawer(GravityCompat.START)

        return true
    }

    fun populateBoardsListToUI(boardsList: ArrayList<Board>) {
        hideProgressDialog()

        val tvNoBoardsAvailable: TextView = findViewById(R.id.tv_no_boards_available)
        val rvBoardsList: RecyclerView = findViewById(R.id.rv_boards_list)

        if (boardsList.size > 0) {
            tvNoBoardsAvailable.visibility = View.GONE

            rvBoardsList.visibility = View.VISIBLE
            rvBoardsList.layoutManager = LinearLayoutManager(this)
            rvBoardsList.setHasFixedSize(true)

            val adapter = BoardItemsAdapter(this, boardsList)

            rvBoardsList.adapter = adapter

            adapter.setOnClickListener(object: BoardItemsAdapter.OnClickListener {
                override fun onClick(position: Int, model: Board) {
                    val intent = Intent(this@MainActivity, TaskListActivity::class.java)
                    intent.putExtra(Constants.DOCUMENT_ID, model.documentId)
                    startActivity(intent)
                }
            })
        } else {
            tvNoBoardsAvailable.visibility = View.VISIBLE
            rvBoardsList.visibility = View.GONE
        }
    }

    fun updateNavigationUserDetails(user: User, readBoardsList: Boolean) {
        mUserName = user.name

        val navUserImage: ImageView = findViewById(R.id.nav_user_image)

        Glide
            .with(this)
            .load(user.image)
            .centerCrop()
            .placeholder(R.drawable.ic_user_place_holder)
            .into(navUserImage)

        val tvUsername: TextView = findViewById(R.id.tv_username)

        tvUsername.text = user.name

        if (readBoardsList) {
            showProgressDialog(resources.getString(R.string.please_wait))
            FirestoreClass().getBoardsList(this)
        }
    }

    private fun setupActionBar() {
        setSupportActionBar(mToolbar)

        mToolbar.setNavigationIcon(R.drawable.ic_action_navigation_menu)

        mToolbar.setNavigationOnClickListener {
            toggleDrawer()
        }
    }

    private fun toggleDrawer() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START)
        } else {
            mDrawer.openDrawer(GravityCompat.START)
        }
    }
}