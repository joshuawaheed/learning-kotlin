package com.joshuawaheed.a7minuteworkout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.joshuawaheed.a7minuteworkout.databinding.ActivityBmiBinding
import java.math.BigDecimal
import java.math.RoundingMode

class BmiActivity : AppCompatActivity() {
    companion object {
        private const val METRIC_UNITS_VIEW = "METRIC_UNIT_VIEW"
        private const val METRIC_US_VIEW = "METRIC_US_VIEW"
    }

    private var binding : ActivityBmiBinding? = null
    private var currentVisibleView: String = METRIC_UNITS_VIEW

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBmiBinding.inflate(layoutInflater)

        setContentView(binding?.root)
        setSupportActionBar(binding?.tbNavigationTop)

        if (supportActionBar != null) {
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "CALCULATE BMI"
        }

        binding?.tbNavigationTop?.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding?.btnCalculateBmi?.setOnClickListener {
            calculateUnits()
        }

        makeVisibleMetricUnitsView()

        binding?.rgUnits?.setOnCheckedChangeListener {
            _, checkedId: Int ->
            if (checkedId == R.id.rbMetricUnits) {
                makeVisibleMetricUnitsView()
            } else {
                makeVisibleMetricUsUnitsView()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private fun makeVisibleMetricUnitsView() {
        currentVisibleView = METRIC_UNITS_VIEW
        binding?.llMetricUnitsView?.visibility = View.VISIBLE
        binding?.llUsUnitsView?.visibility = View.GONE
        binding?.etMetricUnitHeight?.text?.clear()
        binding?.etMetricUnitWeight?.text?.clear()
        binding?.llDisplayBmiResult?.visibility = View.INVISIBLE
    }

    private fun makeVisibleMetricUsUnitsView() {
        currentVisibleView = METRIC_US_VIEW
        binding?.llMetricUnitsView?.visibility = View.GONE
        binding?.llUsUnitsView?.visibility = View.VISIBLE
        binding?.etUsUnitWeight?.text?.clear()
        binding?.etUsUnitFeet?.text?.clear()
        binding?.etUsUnitInches?.text?.clear()
        binding?.llDisplayBmiResult?.visibility = View.INVISIBLE
    }

    private fun displayBmiResult(bmi: Float) {
        val bmiLabel: String
        val bmiDescription: String

        if (bmi.compareTo(15f) <= 0) {
            bmiLabel = "Very severely underweight"
            bmiDescription = "Oops! You really need to take better care of yourself! Eat more!"
        } else if (bmi.compareTo(15f) > 0 && bmi.compareTo(16f) <= 0) {
            bmiLabel = "Severely underweight"
            bmiDescription = "Oops! You really need to take better care of yourself! Eat more!"
        } else if (bmi.compareTo(16f) > 0 && bmi.compareTo(18.5f) <= 0) {
            bmiLabel = "Underweight"
            bmiDescription = "Oops! You really need to take better care of yourself! Eat more!"
        } else if (bmi.compareTo(18.5f) > 0 && bmi.compareTo(25f) <= 0) {
            bmiLabel = "Normal"
            bmiDescription = "Congratulations! You are in a good shape!"
        } else if (bmi.compareTo(25f) > 0 && bmi.compareTo(30f) <= 0) {
            bmiLabel = "Overweight"
            bmiDescription = "Oops! You really need to take better care of yourself! Workout more!"
        } else if (bmi.compareTo(30f) > 0 && bmi.compareTo(35f) <= 0) {
            bmiLabel = "Obese class | (Moderately obese)"
            bmiDescription = "Oops! You really need to take better care of yourself! Workout more!"
        } else if (bmi.compareTo(35f) > 0 && bmi.compareTo(40f) <= 0) {
            bmiLabel = "Obese class || (Severely obese)"
            bmiDescription = "OMG! You are in a very dangerous condition! Act now!"
        } else {
            bmiLabel = "Obese class ||| (Very severely obese)"
            bmiDescription = "OMG! You are in a very dangerous condition! Act now!"
        }

        val bmiValue = BigDecimal(bmi.toDouble()).setScale(2, RoundingMode.HALF_EVEN).toString()

        binding?.llDisplayBmiResult?.visibility = View.VISIBLE
        binding?.tvBmiValue?.text = bmiValue
        binding?.tvBmiType?.text = bmiLabel
        binding?.tvBmiDescription?.text = bmiDescription
    }

    private fun validateMetricUnits(): Boolean {
        var isValid = true

        if (binding?.etMetricUnitWeight?.text.toString().isEmpty()) {
            isValid = false
        } else if (binding?.etMetricUnitHeight?.text.toString().isEmpty()) {
            isValid = false
        }

        return isValid
    }

    private fun calculateUnits() {
        if (currentVisibleView == METRIC_UNITS_VIEW) {
            if (validateMetricUnits()) {
                val heightValue: Float = binding?.etMetricUnitHeight?.text.toString().toFloat() / 100
                val weightValue: Float = binding?.etMetricUnitWeight?.text.toString().toFloat()
                val bmi = weightValue / (heightValue * heightValue)
                displayBmiResult(bmi)
            } else {
                Toast.makeText(this, "Please enter valid values.", Toast.LENGTH_LONG).show()
            }
        } else {
            if (validateUsUnits()) {
                val heightValueFeet: String = binding?.etUsUnitFeet?.text.toString()
                val heightValueInches: String = binding?.etUsUnitInches?.text.toString()
                val heightValue = heightValueInches.toFloat() + heightValueFeet.toFloat() * 12
                val weightValue: Float = binding?.etUsUnitWeight?.text.toString().toFloat()
                val bmi = 703 * (weightValue / (heightValue * heightValue))
                displayBmiResult(bmi)
            } else {
                Toast.makeText(this, "Please enter valid values.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun validateUsUnits(): Boolean {
        var isValid = true

        if (binding?.etUsUnitWeight?.text.toString().isEmpty()) {
            isValid = false
        } else if (binding?.etUsUnitFeet?.text.toString().isEmpty()) {
            isValid = false
        } else if (binding?.etUsUnitInches?.text.toString().isEmpty()) {
            isValid = false
        }

        return isValid
    }
}