package com.example.profile

import java.io.Serializable

data class Biodata(
    val nama: String,
    val usia: Int,
    val jenis_kelamin: String,
    val berat_badan: Double,
    val tinggi_badan: Double,
    val riwayat_keturunan: Int,
    val riwayat_diabetes: Int,
    val prediksi_gula: Double
) : Serializable


