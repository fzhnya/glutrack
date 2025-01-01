package com.example.profile

// ProfileActivity.kt
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat


class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile)
        window.statusBarColor = ContextCompat.getColor(this, R.color.pink)

        // Temukan RelativeLayout berdasarkan ID
        val aboutSection = findViewById<View>(R.id.about)
        val qrcodeSection = findViewById<View>(R.id.qrcode)
        val editprofileSection = findViewById<View>(R.id.editprofile)
        val logoutSection = findViewById<View>(R.id.logout_icon)

        // Atur OnClickListener
        aboutSection.setOnClickListener {
            // Intent untuk membuka AboutActivity
            val intent = Intent(this, AboutActivity::class.java)
            startActivity(intent)
        }

        qrcodeSection.setOnClickListener {
            // Intent untuk membuka AboutActivity
            val intent = Intent(this, QrcodeActivity::class.java)
            startActivity(intent)
        }

        logoutSection.setOnClickListener {
            // Membuat Intent untuk membuka TrackerActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // Atur OnClickListener
        editprofileSection.setOnClickListener {
            // Intent untuk membuka AboutActivity
            val intent = Intent(this, EditProfileActivity::class.java)
            startActivity(intent)
        }

    }
}
