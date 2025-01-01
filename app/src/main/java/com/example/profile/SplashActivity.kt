package com.example.profile

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        window.statusBarColor = ContextCompat.getColor(this, R.color.pink)

        Handler(Looper.getMainLooper()).postDelayed({
            // Cek apakah onboarding sudah pernah dilihat
            val sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
            val isFirstRun = sharedPreferences.getBoolean("isFirstRun", true)

            if (isFirstRun) {
                // Pindah ke OnboardingActivity
                val intent = Intent(this, OnboardingActivity::class.java)
                startActivity(intent)
            } else {
                // Jika sudah pernah, langsung ke LoginActivity
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
            finish() // Tutup SplashActivity
        }, 2000) // Durasi splash screen selama 2 detik
    }
}
