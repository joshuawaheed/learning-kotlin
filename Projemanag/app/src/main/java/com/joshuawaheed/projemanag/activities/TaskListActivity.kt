package com.joshuawaheed.projemanag.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.joshuawaheed.projemanag.R
import com.joshuawaheed.projemanag.firebase.FirestoreClass
import com.joshuawaheed.projemanag.models.Board
import com.joshuawaheed.projemanag.utils.Constants

class TaskListActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_list)

        var boardDocumentId = ""

        if (intent.hasExtra(Constants.DOCUMENT_ID)) {
            boardDocumentId = intent.getStringExtra(Constants.DOCUMENT_ID).toString()
        }

        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getBoardDetails(this, boardDocumentId)
    }

    fun boardDetails(board: Board) {
        hideProgressDialog()
        setupActionBar(board.name)
    }

    private fun setupActionBar(title: String) {
        val toolbar: Toolbar = findViewById(R.id.toolbar_tesk_list_activity)

        setSupportActionBar(toolbar)

        val actionBar = supportActionBar

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = title
        }

        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}