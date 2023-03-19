package com.joshuawaheed.projemanag.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.joshuawaheed.projemanag.R

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var mDrawer: DrawerLayout
    private lateinit var mNavigation: NavigationView
    private lateinit var mToolbar: Toolbar

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
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_my_profile -> {
                Toast.makeText(
                    this@MainActivity,
                    R.string.nav_my_profile,
                    Toast.LENGTH_SHORT
                ).show()
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