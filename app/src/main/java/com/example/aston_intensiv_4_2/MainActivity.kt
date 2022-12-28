package com.example.aston_intensiv_4_2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.aston_intensiv_4_2.clock_widget.clock_widget.ClockWidget
import com.example.aston_intensiv_4_2.clock_widget.extentions.find

class MainActivity : AppCompatActivity() {
private val clockWidget by find<ClockWidget>(R.id.clock_widget)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}