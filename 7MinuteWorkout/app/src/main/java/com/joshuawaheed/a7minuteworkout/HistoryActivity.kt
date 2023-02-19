package com.joshuawaheed.a7minuteworkout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.joshuawaheed.a7minuteworkout.databinding.ActivityHistoryBinding
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect

class HistoryActivity : AppCompatActivity() {
    private var binding: ActivityHistoryBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHistoryBinding.inflate(layoutInflater)

        setContentView(binding?.root)
        setSupportActionBar(binding?.tbNavigationTop)

        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "HISTORY"
        }

        binding?.tbNavigationTop?.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val historyDao = (application as EmployeeApp).db.historyDao()
        getAllCompletedDates(historyDao)
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private fun getAllCompletedDates(historyDao: HistoryDao) {
        lifecycleScope.launch {
            historyDao.fetchAllDates().collect { allCompletedDatesList ->
                if (allCompletedDatesList.isNotEmpty()) {
                    val arrayList = ArrayList(allCompletedDatesList)
                    val historyAdapter = HistoryAdapter(arrayList)
                    binding?.rvHistory?.layoutManager = LinearLayoutManager(this@HistoryActivity)
                    binding?.rvHistory?.adapter = historyAdapter
                    binding?.rvHistory?.visibility = View.VISIBLE
                    binding?.tvNoRecordsAvailable?.visibility = View.GONE
                    binding?.tvTitle?.visibility = View.VISIBLE
                } else {
                    binding?.rvHistory?.visibility = View.GONE
                    binding?.tvNoRecordsAvailable?.visibility = View.VISIBLE
                    binding?.tvTitle?.visibility = View.GONE
                }
            }
        }
    }
}