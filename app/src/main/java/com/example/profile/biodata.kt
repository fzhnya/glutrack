package com.example.profile

data class Biodata(
    val id: Int,
    val namaLengkap: String?,
    val usia: Int,
    val beratBadan: Int,
    val tinggiBadan: Int?,
    val jenisKelamin: String?,
    val statusDiabetes: String?,
    val token: String
) {
    // Fungsi untuk menghitung BMI
    fun calculateBMI(): Double? {
        // Pastikan tinggiBadan tidak null dan lebih besar dari 0
        return if (tinggiBadan != null && tinggiBadan > 0) {
            val heightInMeters = tinggiBadan / 100.0
            beratBadan / (heightInMeters * heightInMeters)
        } else {
            null // Return null jika tinggi badan tidak valid
        }
    }
}
