package com.hao.cubc.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity // 注意：從 ComponentActivity 改成 AppCompatActivity
import androidx.activity.enableEdgeToEdge
import com.hao.cubc.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContentView(R.layout.activity_main)
    }
}