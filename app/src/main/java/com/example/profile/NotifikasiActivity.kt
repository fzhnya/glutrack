package com.example.profile

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class NotifikasiActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifikasi)
        window.statusBarColor = ContextCompat.getColor(this, R.color.red)

        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.nav_notifikasi

        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {

                R.id.nav_tracker -> {
                    // Intent to open TrackerActivity
                    startActivity(Intent(this, TrackerActivity::class.java))
                    true
                }
                R.id.nav_home -> {
                    // Intent to open TrackerActivity
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.nav_ocr -> {
                    // Intent to open OCRActivity
                    startActivity(Intent(this, OCRActivity::class.java))
                    true
                }
                R.id.nav_profile -> {
                    // Intent to open ProfileActivity
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                // Tambahkan lebih banyak kasus jika diperlukan
                else -> false
            }
        }
    }
}
