package com.example.profile

import android.content.Intent
import android.app.Application
import com.google.firebase.FirebaseApp
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.cardview.widget.CardView // Pastikan mengimpor CardView
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        window.statusBarColor = ContextCompat.getColor(this, R.color.pink)

        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.nav_home

        // Listener untuk CardView atau elemen lain untuk membuka Artikel
        // CardView untuk Artikel 1
        val articleCardView1: CardView = findViewById(R.id.artikel_1)
        articleCardView1.setOnClickListener {
            openArticle(1)
        }

        // CardView untuk Artikel 2
        val articleCardView2: CardView = findViewById(R.id.artikel_2)
        articleCardView2.setOnClickListener {
            openArticle(2)
        }

        // CardView untuk Artikel 3
        val articleCardView3: CardView = findViewById(R.id.artikel_3)
        articleCardView3.setOnClickListener {
            openArticle(3)
        }

        // CardView untuk Artikel 4
        val articleCardView4: CardView = findViewById(R.id.artikel_4)
        articleCardView4.setOnClickListener {
            openArticle(4)
        }
        val trackerCardView1: CardView = findViewById(R.id.tracker1)
        trackerCardView1.setOnClickListener {
            // Membuat Intent untuk membuka TrackerActivity
            val intent = Intent(this, TrackerActivity::class.java)
            startActivity(intent)
        }

        val trackerCardView2: CardView = findViewById(R.id.tracker2)
        trackerCardView2.setOnClickListener {
            // Membuat Intent untuk membuka TrackerActivity
            val intent = Intent(this, TrackerActivity::class.java)
            startActivity(intent)
        }

        val add_btn: Button = findViewById(R.id.btn_add)
        add_btn.setOnClickListener {
            // Membuat Intent untuk membuka TrackerActivity
            val intent = Intent(this, TrackerActivity::class.java)
            startActivity(intent)
        }

        val default_btn: Button = findViewById(R.id.btn_default)
        default_btn.setOnClickListener {
            // Membuat Intent untuk membuka TrackerActivity
            val intent = Intent(this, TrackerActivity::class.java)
            startActivity(intent)
        }


        // Set listener untuk BottomNavigationView
        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_ocr -> {
                    // Intent to open OCRActivity
                    startActivity(Intent(this, OCRActivity::class.java))
                    true
                }
                R.id.nav_tracker -> {
                    // Intent to open TrackerActivity
                    startActivity(Intent(this, TrackerActivity::class.java))
                    true
                }
                R.id.nav_notifikasi -> {
                    // Intent to open NotifikasiActivity
                    startActivity(Intent(this, NotifikasiActivity::class.java))
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
    private fun openArticle(articleNumber: Int) {
        val intent = Intent(this, ArticleActivity::class.java)
        intent.putExtra("ARTICLE_NUMBER", articleNumber)
        startActivity(intent)
    }
}
