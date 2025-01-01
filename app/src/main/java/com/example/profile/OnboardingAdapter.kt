package com.example.profile

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.profile.databinding.OnboardingPage1Binding
import com.example.profile.databinding.OnboardingPage2Binding
import com.example.profile.databinding.OnboardingPage3Binding

class OnboardingAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // Daftar layout untuk tiap halaman onboarding
    private val layouts = listOf(
        R.layout.onboarding_page1,
        R.layout.onboarding_page2,
        R.layout.onboarding_page3
    )

    // Inflate layout berdasarkan viewType (halaman)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = when (viewType) {
            0 -> OnboardingPage1Binding.inflate(inflater, parent, false)
            1 -> OnboardingPage2Binding.inflate(inflater, parent, false)
            2 -> OnboardingPage3Binding.inflate(inflater, parent, false)
            else -> throw IllegalArgumentException("Invalid viewType")
        }
        return object : RecyclerView.ViewHolder(binding.root) {}
    }

    // Tidak perlu merujuk ke buttonNext di sini karena tombol "Next" dikelola dari OnboardingActivity
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // Jika ada logika untuk mengatur data atau tampilan tambahan, lakukan di sini
    }

    // Jumlah halaman onboarding
    override fun getItemCount(): Int = layouts.size

    // Mengembalikan posisi untuk ViewType agar bisa menentukan layout yang digunakan
    override fun getItemViewType(position: Int): Int = position
}
