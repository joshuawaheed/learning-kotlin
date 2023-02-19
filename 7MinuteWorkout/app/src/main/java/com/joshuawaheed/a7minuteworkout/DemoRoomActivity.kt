package com.joshuawaheed.a7minuteworkout

import android.app.AlertDialog
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.joshuawaheed.a7minuteworkout.databinding.ActivityDemoRoomBinding
import com.joshuawaheed.a7minuteworkout.databinding.DialogUpdateBinding
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect

class DemoRoomActivity : AppCompatActivity() {
    private var binding: ActivityDemoRoomBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDemoRoomBinding.inflate(layoutInflater)

        setContentView(binding?.root)
        setSupportActionBar(binding?.tbNavigationTop)

        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "Room Demo"
        }

        binding?.tbNavigationTop?.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val employeeDao = (application as EmployeeApp).db.employeeDao()

        binding?.btnAdd?.setOnClickListener {
            addRecord(employeeDao)
        }

        lifecycleScope.launch {
            employeeDao.fetchAllEmployees().collect {
                val items = ArrayList(it)
                setupListOfDataIntoRecyclerView(items, employeeDao)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    fun addRecord(employeeDao: EmployeeDao) {
        val name = binding?.etName?.text.toString()
        val email = binding?.etEmailId?.text.toString()

        if (name.isNotEmpty() && email.isNotEmpty()) {
            lifecycleScope.launch {
                employeeDao.insert(EmployeeEntity(name = name, email = email))

                Toast.makeText(
                    this@DemoRoomActivity,
                    "Record saved",
                    Toast.LENGTH_LONG
                ).show()

                binding?.etName?.text?.clear()
                binding?.etEmailId?.text?.clear()
            }
        } else {
            Toast.makeText(
                this@DemoRoomActivity,
                "Name or email cannot be blank",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun setupListOfDataIntoRecyclerView(employeesList: ArrayList<EmployeeEntity>, employeeDao: EmployeeDao) {
        if (employeesList.isNotEmpty()) {
            val itemAdapter = ItemAdapter(
                employeesList,
                {
                    updateId ->
                    updateRecordDialog(updateId, employeeDao)
                },
                {
                    deleteId ->
                    deleteRecordAlertDialog(deleteId, employeeDao)
                }
            )

            binding?.rvItemsList?.layoutManager = LinearLayoutManager(this)
            binding?.rvItemsList?.adapter = itemAdapter
            binding?.rvItemsList?.visibility = View.VISIBLE
            binding?.tvNoRecordsAvailable?.visibility = View.GONE
        } else {
            binding?.rvItemsList?.visibility = View.GONE
            binding?.tvNoRecordsAvailable?.visibility = View.VISIBLE
        }
    }

    private fun updateRecordDialog(id: Int, employeeDao: EmployeeDao) {
        val dialog = Dialog(this, R.style.Theme_Dialog)
        dialog.setCancelable(false)

        val binding = DialogUpdateBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)

        lifecycleScope.launch {
            employeeDao.fetchEmployeeById(id).collect {
                if (it != null) {
                    binding.etName.setText(it.name)
                    binding.etEmail.setText(it.email)
                }
            }
        }

        binding.tvUpdate.setOnClickListener {
            val name = binding.etName.text.toString()
            val email = binding.etEmail.text.toString()

            if (name.isNotEmpty() && email.isNotEmpty()) {
                lifecycleScope.launch {
                    employeeDao.update(EmployeeEntity(id, name, email))

                    Toast.makeText(
                        this@DemoRoomActivity,
                        "Record updated",
                        Toast.LENGTH_LONG
                    ).show()

                    dialog.dismiss()
                }
            } else {
                Toast.makeText(
                    this@DemoRoomActivity,
                    "Name or email cannot be blank",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        binding.tvCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun deleteRecordAlertDialog(id: Int, employeeDao: EmployeeDao) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete record")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        builder.setPositiveButton("Yes") { dialogInterface, _ ->
            lifecycleScope.launch {
                employeeDao.delete(EmployeeEntity(id))

                Toast.makeText(
                    this@DemoRoomActivity,
                    "Record deleted successfully",
                    Toast.LENGTH_LONG
                ).show()
            }

            dialogInterface.dismiss()
        }

        builder.setNegativeButton("No") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
}