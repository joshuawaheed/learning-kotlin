package com.joshuawaheed.projemanag.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.joshuawaheed.projemanag.R
import com.joshuawaheed.projemanag.activities.TaskListActivity
import com.joshuawaheed.projemanag.models.Task
import java.util.Collections

open class TaskListitemsAdapter(
    private val context: Context,
    private var list: ArrayList<Task>
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var mPositionDraggedFrom = -1
    private var mPositionDraggedTo = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context).inflate(
            R.layout.item_task,
            parent,
            false
        )

        val layoutParams = LinearLayout.LayoutParams(
            (parent.width * 0.7).toInt(),
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        layoutParams.setMargins((15.toDp()).toPx(), 0, (40.toDp()).toPx(), 0)

        view.layoutParams = layoutParams

        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyViewHolder) {
            val taskList: TextView = holder.itemView.findViewById(R.id.tv_add_task_list)
            val taskItem: LinearLayout = holder.itemView.findViewById(R.id.ll_task_item)

            if (position == list.size - 1) {
                taskList.visibility = View.VISIBLE
                taskItem.visibility = View.GONE
            } else {
                taskList.visibility = View.GONE
                taskItem.visibility = View.VISIBLE
            }

            val taskListTitle: TextView = holder.itemView.findViewById(R.id.tv_task_list_title)
            taskListTitle.text = model.title

            val taskListName: CardView = holder.itemView.findViewById(R.id.cv_add_task_list_name)

            taskList.setOnClickListener {
                taskList.visibility = View.GONE
                taskListName.visibility = View.VISIBLE
            }

            val closeListNameButton: ImageButton = holder.itemView.findViewById(R.id.ib_close_list_name)

            closeListNameButton.setOnClickListener {
                taskList.visibility = View.VISIBLE
                taskListName.visibility = View.GONE
            }

            val doneListNameButton: ImageButton = holder.itemView.findViewById(R.id.ib_done_list_name)

            doneListNameButton.setOnClickListener {
                val addTextListName: EditText = holder.itemView.findViewById(R.id.et_add_task_list_name)
                val addTextListNameText = addTextListName.text.toString()

                if (addTextListNameText.isNotEmpty()) {
                    if (context is TaskListActivity) {
                        context.createTaskList(addTextListNameText)
                    }
                } else {
                    Toast.makeText(context, "Please Enter List Name", Toast.LENGTH_SHORT).show()
                }
            }

            val editListButton: ImageButton = holder.itemView.findViewById(R.id.ib_edit_list_name)

            editListButton.setOnClickListener {
                val editListName: EditText = holder.itemView.findViewById(R.id.et_edit_task_list_name)

                editListName.setText(model.title)

                val titleView: LinearLayout = holder.itemView.findViewById(R.id.ll_title_view)

                titleView.visibility = View.GONE

                val editTaskList: CardView = holder.itemView.findViewById(R.id.cv_edit_task_list_name)

                editTaskList.visibility = View.VISIBLE
            }

            val closeEditListButton: ImageButton = holder.itemView.findViewById(R.id.ib_close_editable_view)

            closeEditListButton.setOnClickListener {
                val titleView: LinearLayout = holder.itemView.findViewById(R.id.ll_title_view)

                titleView.visibility = View.VISIBLE

                val editTaskList: CardView = holder.itemView.findViewById(R.id.cv_edit_task_list_name)

                editTaskList.visibility = View.GONE
            }

            val doneEditListButton: ImageButton = holder.itemView.findViewById(R.id.ib_done_edit_list_name)

            doneEditListButton.setOnClickListener {
                val editListName: EditText = holder.itemView.findViewById(R.id.et_edit_task_list_name)
                val editListNameText = editListName.text.toString()

                if (editListNameText.isNotEmpty()) {
                    if (context is TaskListActivity) {
                        context.updateTaskList(position, editListNameText, model)
                    }
                } else {
                    Toast.makeText(context, "Please Enter List Name", Toast.LENGTH_SHORT).show()
                }
            }

            val deleteListButton: ImageButton = holder.itemView.findViewById(R.id.ib_delete_list)

            deleteListButton.setOnClickListener {
                val builder = AlertDialog.Builder(context)
                builder.setTitle("Alert")
                builder.setMessage("Are you sure you want to delete ${model.title}.")
                // builder.setIcon(R.drawable.ic_dialog_alert)

                builder.setPositiveButton("Yes") {
                    dialogInterface, _ ->
                    dialogInterface.dismiss()

                    if (context is TaskListActivity) {
                        context.deleteTaskList(position)
                    }
                }

                builder.setNegativeButton("No") {
                    dialogInterface, _ ->
                    dialogInterface.dismiss()
                }

                val alertDialog: AlertDialog = builder.create()
                alertDialog.setCancelable(false)
                alertDialog.show()
            }

            val addCardTextView: TextView = holder.itemView.findViewById(R.id.tv_add_card)
            val addCardCardView: CardView = holder.itemView.findViewById(R.id.cv_add_card)

            addCardTextView.setOnClickListener {
                addCardTextView.visibility = View.GONE
                addCardCardView.visibility = View.VISIBLE
            }

            val closeAddCardButton: ImageButton = holder.itemView.findViewById(R.id.ib_close_card_name)

            closeAddCardButton.setOnClickListener {
                addCardTextView.visibility = View.VISIBLE
                addCardCardView.visibility = View.GONE
            }

            val doneAddCardButton: ImageButton = holder.itemView.findViewById(R.id.ib_done_card_name)

            doneAddCardButton.setOnClickListener {
                val addCardName: EditText = holder.itemView.findViewById(R.id.et_card_name)
                val addCardNameText = addCardName.text.toString()

                if (addCardNameText.isNotEmpty()) {
                    if (context is TaskListActivity) {
                        context.addCardToTaskList(position, addCardNameText)
                    }
                } else {
                    Toast.makeText(context, "Please Enter a Card Name", Toast.LENGTH_SHORT).show()
                }
            }

            val cardList: RecyclerView = holder.itemView.findViewById(R.id.rv_card_list)

            cardList.layoutManager = LinearLayoutManager(context)
            cardList.setHasFixedSize(true)

            val cardListAdapter = CardListItemsAdapter(context, model.cards)

            cardList.adapter = cardListAdapter
            
            cardListAdapter.setOnClickListener(object: CardListItemsAdapter.OnClickListener {
                override fun onClick(cardPosition: Int) {
                    if (context is TaskListActivity) {
                        context.cardDetails(position, cardPosition)
                    }
                }
            })

            val dividerItemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
            cardList.addItemDecoration(dividerItemDecoration)

            val helper = ItemTouchHelper(object: ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    dragged: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    val draggedPosition = dragged.adapterPosition
                    val targetPosition = target.adapterPosition

                    if (mPositionDraggedFrom == -1) {
                        mPositionDraggedFrom = draggedPosition
                    }

                    mPositionDraggedTo = targetPosition
                    Collections.swap(list[position].cards, draggedPosition, targetPosition)
                    cardListAdapter.notifyItemMoved(draggedPosition, targetPosition)
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                }

                override fun clearView(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ) {
                    super.clearView(recyclerView, viewHolder)

                    if (mPositionDraggedFrom != -1 && mPositionDraggedTo != -1 && mPositionDraggedFrom != mPositionDraggedTo) {
                        if (context is TaskListActivity) {
                            context.updateCardsInTaskList(position, list[position].cards)
                        }
                    }

                    mPositionDraggedFrom = -1
                    mPositionDraggedTo = -1
                }
            })

            helper.attachToRecyclerView(cardList)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    private fun Int.toDp(): Int = (this / Resources.getSystem().displayMetrics.density).toInt()

    private fun Int.toPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()

    class MyViewHolder(view: View): RecyclerView.ViewHolder(view)
}