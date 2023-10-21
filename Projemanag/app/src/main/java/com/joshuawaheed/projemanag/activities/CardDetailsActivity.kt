package com.joshuawaheed.projemanag.activities

import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.joshuawaheed.projemanag.R
import com.joshuawaheed.projemanag.adapters.CardMemberListItemsAdapter
import com.joshuawaheed.projemanag.dialogs.LabelColorListDialog
import com.joshuawaheed.projemanag.dialogs.MembersListDialog
import com.joshuawaheed.projemanag.firebase.FirestoreClass
import com.joshuawaheed.projemanag.models.Board
import com.joshuawaheed.projemanag.models.Card
import com.joshuawaheed.projemanag.models.SelectedMembers
import com.joshuawaheed.projemanag.models.Task
import com.joshuawaheed.projemanag.models.User
import com.joshuawaheed.projemanag.utils.Constants

class CardDetailsActivity : BaseActivity() {
    private lateinit var mBoardDetails: Board
    private var mTaskListPosition: Int = -1
    private var mCardPosition: Int = -1
    private var mSelectedColor = ""
    private lateinit var mMembersDetailList: ArrayList<User>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_details)
        getIntentData()
        setupActionBar()

        val etCardDetails: EditText = findViewById(R.id.et_name_card_details)
        etCardDetails.setText(mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].name)
        etCardDetails.setSelection(etCardDetails.text.toString().length)

        mSelectedColor = mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].labelColor
        if (mSelectedColor.isNotEmpty()) {
            setColor()
        }

        val btnUpdateCardDetails: Button = findViewById(R.id.btn_update_card_details)
        btnUpdateCardDetails.setOnClickListener {
            if (etCardDetails.text.toString().isNotEmpty()) {
                updateCardDetails()
            } else {
                Toast.makeText(
                    this@CardDetailsActivity,
                    "Enter card name.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        val tvSelectLabelColor: TextView = findViewById(R.id.tv_select_label_color)
        tvSelectLabelColor.setOnClickListener {
            labelColorsListDialog()
        }

        val tvSelectMembers: TextView = findViewById(R.id.tv_select_members)
        tvSelectMembers.setOnClickListener {
            membersListDialog()
        }

        setupSelectedMembersList()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_delete_card, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_delete_card -> {
                alertDialogForDeleteCard(mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].name)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    fun addUpdateTaskListSuccess() {
        hideProgressDialog()
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun alertDialogForDeleteCard(cardName: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(resources.getString(R.string.alert))
        builder.setMessage(resources.getString(R.string.confirmation_message_to_delete_card, cardName))

        builder.setPositiveButton(resources.getString(R.string.yes)) {
            dialogInterface, _ ->
            dialogInterface.dismiss()
            deleteCard()
        }

        builder.setNegativeButton(resources.getString(R.string.no)) {
            dialogInterface, _ ->
            dialogInterface.dismiss()
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun colorsList(): ArrayList<String> {
        val colorsList: ArrayList<String> = ArrayList()
        colorsList.add("#43C8FF")
        colorsList.add("#0C90F1")
        colorsList.add("#F72400")
        colorsList.add("#7A8089")
        colorsList.add("#D57C1D")
        colorsList.add("#770000")
        colorsList.add("#0022F8")
        return colorsList
    }

    private fun deleteCard() {
        val cardsList: ArrayList<Card> = mBoardDetails.taskList[mTaskListPosition].cards
        cardsList.removeAt(mCardPosition)

        val taskList: ArrayList<Task> = mBoardDetails.taskList
        taskList.removeAt(taskList.size - 1)
        taskList[mTaskListPosition].cards = cardsList

        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this@CardDetailsActivity, mBoardDetails)
    }

    private fun getIntentData() {
        if (intent.hasExtra(Constants.BOARD_DETAIL)) {
            mBoardDetails = intent.getParcelableExtra(Constants.BOARD_DETAIL)!!
        }

        if (intent.hasExtra(Constants.TASK_LIST_ITEM_POSITION)) {
            mTaskListPosition = intent.getIntExtra(Constants.TASK_LIST_ITEM_POSITION, mTaskListPosition)
        }

        if (intent.hasExtra(Constants.CARD_LIST_ITEM_POSITION)) {
            mCardPosition = intent.getIntExtra(Constants.CARD_LIST_ITEM_POSITION, mCardPosition)
        }

        if (intent.hasExtra(Constants.BOARD_MEMBERS_LIST)) {
            mMembersDetailList = intent.getParcelableArrayListExtra(Constants.BOARD_MEMBERS_LIST)!!
        }
    }

    private fun labelColorsListDialog() {
        val colorsList: ArrayList<String> = colorsList()

        val listDialog = object: LabelColorListDialog(
            this,
            colorsList,
            resources.getString(R.string.str_select_label_color),
            mSelectedColor
        ) {
            override fun onItemSelected(color: String) {
                mSelectedColor = color
                setColor()
            }
        }

        listDialog.show()
    }

    private fun membersListDialog() {
        var cardAssignedMembersList = mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo

        if (cardAssignedMembersList.size > 0) {
            for (i in mMembersDetailList.indices) {
                for (j in cardAssignedMembersList) {
                    if (mMembersDetailList[i].id == j) {
                        mMembersDetailList[i].selected = true
                    }
                }
            }
        } else {
            for (i in mMembersDetailList.indices) {
                mMembersDetailList[i].selected = false
            }
        }

        val listDialog = object: MembersListDialog(
            this,
            mMembersDetailList,
            resources.getString(R.string.str_select_member)
        ) {
            override fun onItemSelected(user: User, action: String) {
                if (action == Constants.SELECT) {
                    if (!mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo.contains(user.id)) {
                        mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo.add(user.id)
                    }
                } else {
                    mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo.remove(user.id)

                    for (i in mMembersDetailList.indices) {
                        if (mMembersDetailList[i].id == user.id) {
                            mMembersDetailList[i].selected = false
                        }
                    }
                }

                setupSelectedMembersList()
            }
        }

        listDialog.show()
    }

    private fun setColor() {
        val tvSelectLabelColor: TextView = findViewById(R.id.tv_select_label_color)
        tvSelectLabelColor.text = ""
        tvSelectLabelColor.setBackgroundColor(Color.parseColor(mSelectedColor))
    }

    private fun setupActionBar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar_card_details_activity)

        setSupportActionBar(toolbar)

        val actionBar = supportActionBar

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_white_color_back_24dp)
            actionBar.title = mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].name
        }

        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupSelectedMembersList() {
        val cardAssignedMemberList = mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo
        val selectedMembersList = ArrayList<SelectedMembers>()

        for (i in mMembersDetailList.indices) {
            for (j in cardAssignedMemberList) {
                if (mMembersDetailList[i].id == j) {
                    val selectedMember = SelectedMembers(
                        mMembersDetailList[i].id,
                        mMembersDetailList[i].image
                    )

                    selectedMembersList.add(selectedMember)
                }
            }
        }

        val tvSelectedMembers: TextView = findViewById(R.id.tv_select_members)
        val rvSelectedMembersList: RecyclerView = findViewById(R.id.rv_selected_members_list)

        if (selectedMembersList.size > 0) {
            selectedMembersList.add(SelectedMembers("", ""))

            tvSelectedMembers.visibility = View.GONE

            rvSelectedMembersList.visibility = View.VISIBLE
            rvSelectedMembersList.layoutManager = GridLayoutManager(this, 6)

            val adapter = CardMemberListItemsAdapter(this, selectedMembersList, true)
            rvSelectedMembersList.adapter = adapter

            adapter.setOnClickListener(object: CardMemberListItemsAdapter.OnClickListener {
                override fun onClick() {
                    membersListDialog()
                }
            })
        } else {
            tvSelectedMembers.visibility = View.VISIBLE
            rvSelectedMembersList.visibility = View.GONE
        }
    }

    private fun updateCardDetails() {
        var etCardName: EditText = findViewById(R.id.et_name_card_details)

        val card = Card(
            etCardName.text.toString(),
            mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].createdBy,
            mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition].assignedTo,
            mSelectedColor
        )

        val taskList: ArrayList<Task> = mBoardDetails.taskList
        taskList.removeAt(taskList.size - 1)
        mBoardDetails.taskList[mTaskListPosition].cards[mCardPosition] = card
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addUpdateTaskList(this@CardDetailsActivity, mBoardDetails)
    }
}