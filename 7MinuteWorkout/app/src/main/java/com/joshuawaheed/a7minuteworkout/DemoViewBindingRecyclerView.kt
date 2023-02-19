package com.joshuawaheed.a7minuteworkout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.joshuawaheed.a7minuteworkout.databinding.ActivityDemoViewBindingRecyclerViewBinding

class DemoViewBindingRecyclerView : AppCompatActivity() {
    private var binding: ActivityDemoViewBindingRecyclerViewBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setBindings()

        binding?.rvTask?.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )

        val adapter = MainAdapter(TaskList.taskList)
        binding?.rvTask?.adapter = adapter
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private fun setBindings() {
        binding = ActivityDemoViewBindingRecyclerViewBinding.inflate(layoutInflater)

        setContentView(binding?.root)
        setSupportActionBar(binding?.tbExercise)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding?.tbExercise?.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }
    }
}