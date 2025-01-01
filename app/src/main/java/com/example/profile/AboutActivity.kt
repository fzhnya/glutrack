package com.example.profile

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.about) // Mengarahkan ke layout about.xml
        window.statusBarColor = ContextCompat.getColor(this, R.color.pink)

        // Inisialisasi backButtonabout di dalam onCreate
        val backButtonabout = findViewById<ImageView>(R.id.back_button)
        backButtonabout.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Opsional, untuk menutup aktivitas saat ini
        }
    }
}
