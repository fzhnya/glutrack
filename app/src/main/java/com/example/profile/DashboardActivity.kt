package com.example.profile

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat


class DashboardActivity : AppCompatActivity() {
    private lateinit var searchInput: EditText
    private lateinit var payButton: Button
    private lateinit var topUpButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = ContextCompat.getColor(this, R.color.pink)
        setContentView(R.layout.activity_dashboard)


    }
}
