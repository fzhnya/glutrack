package com.example.profile

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class QrcodeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qrcode) // Mengarahkan ke layout about.xml
        window.statusBarColor = ContextCompat.getColor(this, R.color.pink)

        val backButtonqrcode = findViewById<Button>(R.id.btn_close)
        backButtonqrcode.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}