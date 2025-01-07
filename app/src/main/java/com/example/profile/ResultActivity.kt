package com.example.profile

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.profile.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        try {
            // Ambil objek Biodata dari Intent
            val biodata = intent.getSerializableExtra("biodata") as? Biodata

            // Cek apakah biodata tidak null
            if (biodata != null) {
                // Log semua nilai dari biodata untuk debugging
                Log.d("ResultActivity", "Received biodata: $biodata")

                // Tampilkan prediksi gula jika tersedia
                val prediksiGula = biodata.prediksi_gula
                Log.d("ResultActivity", "Received prediksiGula: $prediksiGula")

                binding.tvPrediksiGula.text = String.format(
                    "Batas konsumsi gula yang direkomendasikan:\n%.1f gram per hari",
                    prediksiGula
                )
            } else {
                Log.e("ResultActivity", "Biodata is null")
                binding.tvPrediksiGula.text = "Terjadi kesalahan dalam memproses biodata"
                Toast.makeText(this, "Terjadi kesalahan: Biodata tidak ditemukan", Toast.LENGTH_LONG).show()
            }

        } catch (e: Exception) {
            Log.e("ResultActivity", "Error processing biodata: ${e.message}", e)
            binding.tvPrediksiGula.text = "Terjadi kesalahan dalam memproses biodata"
            Toast.makeText(this, "Terjadi kesalahan: ${e.message}", Toast.LENGTH_LONG).show()
        }

        binding.btnKembali.setOnClickListener { finish() }
    }
}
