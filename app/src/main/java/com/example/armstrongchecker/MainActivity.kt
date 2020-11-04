package com.example.armstrongchecker

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.math.pow

class MainActivity : AppCompatActivity() {
    var lowestNumber: Long = 1L
    var highestNumber: Long = 1L
    var inputNumberText: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkButton.setOnClickListener {
            if (number_input.text.isEmpty()) {
                Toast.makeText(this, "Please enter a number", Toast.LENGTH_SHORT).show()
            }else {
                progressBar.visibility = View.VISIBLE
                MainScope().launch {
                    getResultForUserInput()
                }
            }

        }
    }
     fun checkArmstrongNumber(number: Long): Boolean {
        var tempNumber = number
        var remainder: Long
        var digits = 0
        var result = 0L
        while (tempNumber != 0L) {
            tempNumber /= 10
            digits++
        }

        tempNumber = number
        while (tempNumber != 0L) {
            remainder = tempNumber % 10
            result += remainder.toDouble().pow(digits.toDouble()).toInt()
            tempNumber /= 10
        }
        return result == number
    }

    suspend fun getResultForUserInput() {
        var isArmstrong: Boolean = false
        var start: Long = 0L
        var beforeUsedMemory: Long = 0L
        result_text.visibility = View.VISIBLE
        result_label.visibility = View.VISIBLE
        time_text.visibility = View.VISIBLE
        memory_text.visibility = View.VISIBLE
        try {
            val inputNumber = number_input.text.toString().toLong()
            inputNumberText = inputNumber.toString()
            withContext(Dispatchers.Default) {
                start = System.nanoTime()
                beforeUsedMemory =
                    Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
                isArmstrong = checkArmstrongNumber(inputNumber)
                if (!isArmstrong) {
                    getClosestNumber(inputNumber)
                }
            }
        } catch (exception: NumberFormatException) {
            Toast.makeText(this, "please Try Again", Toast.LENGTH_SHORT).show()
        }
        val end = System.nanoTime()
        val afterUsedMemory =
            Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()

        val executionTime: Double = ((end - start).toDouble() / 1_000_000_000)

        val memoryUsage = (afterUsedMemory - beforeUsedMemory)
        updateUI(isArmstrong, executionTime, memoryUsage)

    }

    fun updateUI(isArmstrong: Boolean, executionTime: Double, memoryUsage: Long) {
        progressBar.visibility = View.GONE
        if (!isArmstrong) {
            closest_lower.visibility = View.VISIBLE
            closest_higher.visibility = View.VISIBLE
        } else {
            closest_lower.visibility = View.GONE
            closest_higher.visibility = View.GONE
        }
        if (isArmstrong) {
            val resultText = inputNumberText.toString().plus(" ")
                .plus(resources.getText(R.string.result_true))
            result_text.text = resultText.toString()
        } else {
            val resultText = inputNumberText.toString().plus(" ")
                .plus(resources.getText(R.string.result_false))
            result_text.text = resultText.toString()
        }

        closest_lower.text = resources.getText(R.string.low_text).toString().plus(" ")
            .plus(lowestNumber.toString())
        closest_higher.text = resources.getText(R.string.high_text).toString().plus(" ")
            .plus(highestNumber.toString())
        time_text.text = resources.getText(R.string.time_text).toString().plus(" ")
            .plus(executionTime.roundTo(2).toString().plus("sec"))
        memory_text.text =
            resources.getText(R.string.memory_text).toString().plus(" ").plus(memoryUsage).plus(" ")
                .plus("bytes")
    }


     fun getClosestNumber(number: Long) {
        var tempNumber = number
        var flag = false
        while (!flag) {
            flag = checkArmstrongNumber(--tempNumber)
            lowestNumber = tempNumber
        }
        flag = false
        while (!flag) {
            flag = checkArmstrongNumber(++tempNumber)
            highestNumber = tempNumber
        }
    }

    fun Number.roundTo(
        numFractionDigits: Int
    ) = "%.${numFractionDigits}f".format(this, Locale.ENGLISH).toDouble()

}
