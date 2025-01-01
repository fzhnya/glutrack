package com.example.profile


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView


class ArticleActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = ContextCompat.getColor(this, R.color.pink)


        // Mengambil data dari Intent
        val articleNumber = intent.getIntExtra("ARTICLE_NUMBER", 1)

        // Memilih layout berdasarkan nomor artikel
        when (articleNumber) {
            1 -> setContentView(R.layout.activity_artikel1)
            2 -> setContentView(R.layout.activity_artikel2)
            3 -> setContentView(R.layout.activity_artikel3)
            4 -> setContentView(R.layout.activity_artikel4)
            else -> setContentView(R.layout.activity_artikel1) // Default ke artikel 1
        }
    }
}
