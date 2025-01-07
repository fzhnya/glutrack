package com.example.profile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.profile.databinding.ActivityOnboardingBinding

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var viewPager: ViewPager2
    private lateinit var onboardingAdapter: OnboardingAdapter
    private lateinit var layouts: List<Int> // List halaman onboarding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // **Cek apakah onboarding sudah selesai**
        val sharedPreferences = getSharedPreferences("OnboardingPrefs", Context.MODE_PRIVATE)
        val isFirstRun = sharedPreferences.getBoolean("isFirstRun", true)

        if (!isFirstRun) {
            // Jika sudah pernah onboarding, langsung ke LoginActivity
            val intent = Intent(this, OCRActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        // Jika ini pertama kali, tampilkan onboarding
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.pink)

        // Inisialisasi ViewPager dan Adapter
        viewPager = binding.viewPager
        layouts = listOf(
            R.layout.onboarding_page1,
            R.layout.onboarding_page2,
            R.layout.onboarding_page3
        )
        onboardingAdapter = OnboardingAdapter()
        viewPager.adapter = onboardingAdapter

        // Tombol "Next" untuk berpindah halaman
        binding.btnNext.setOnClickListener {
            val currentPage = viewPager.currentItem
            if (currentPage < layouts.size - 1) {
                // Pindah ke halaman onboarding berikutnya
                viewPager.currentItem = currentPage + 1
            } else {
                // Simpan status bahwa onboarding sudah selesai
                val editor = sharedPreferences.edit()
                editor.putBoolean("isFirstRun", false)
                editor.apply()

                // Pindah ke LoginActivity setelah onboarding selesai
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}
