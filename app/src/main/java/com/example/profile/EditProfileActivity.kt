package com.example.profile

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class EditProfileActivity : AppCompatActivity() {

    private lateinit var namaLengkap: EditText
    private lateinit var usia: EditText
    private lateinit var beratBadan: EditText
    private lateinit var tinggiBadan: EditText
    private lateinit var jenisKelamin: RadioGroup
    private lateinit var statusDiabetes: RadioGroup
    private lateinit var saveButton: Button
    private lateinit var generateTokenButton: Button
    private lateinit var backButton: ImageView
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_profile)
        window.statusBarColor = ContextCompat.getColor(this, R.color.red)

        // Initialize views
        namaLengkap = findViewById(R.id.namaLengkap)
        usia = findViewById(R.id.usia)
        beratBadan = findViewById(R.id.beratBadan)
        tinggiBadan = findViewById(R.id.tinggiBadan)
        jenisKelamin = findViewById(R.id.jenisKel)
        statusDiabetes = findViewById(R.id.statusDbt)
        saveButton = findViewById(R.id.saveButton)
        generateTokenButton = findViewById(R.id.btnGenerateToken)
        backButton = findViewById(R.id.backButton)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        // Handle save button click
        saveButton.setOnClickListener {
            if (!validateInput()) return@setOnClickListener

            // Initialize values
            val fullName = namaLengkap.text.toString()
            val usiaValue = usia.text.toString().toInt()  // Convert to Int
            val beratBadanValue = beratBadan.text.toString().toInt()  // Convert to Int
            val tinggiBadanValue = tinggiBadan.text.toString().toInt()  // Convert to Int

// Get selected gender and diabetes status
            val selectedGender = if (jenisKelamin.checkedRadioButtonId == R.id.male) "Laki-laki" else "Perempuan"
            val selectedDiabetesStatus = if (statusDiabetes.checkedRadioButtonId == R.id.nondiabetes) "Normal" else "Diabetes"

// Generate token
            val token = generateToken()  // This will return a String

// Create Biodata object
            val biodata = Biodata(
                id = 0,  // Assuming `id` is auto-generated or you can set it to 0 for now
                namaLengkap = fullName,
                usia = usiaValue,
                beratBadan = beratBadanValue,
                tinggiBadan = tinggiBadanValue,
                jenisKelamin = selectedGender,
                statusDiabetes = selectedDiabetesStatus,
                token = token
            )

// Optionally, show the BMI calculation result
            val bmi = biodata.calculateBMI()



            // Call API to save biodata
            val call = RetrofitClient.apiService.saveBiodata(biodata)
            call.enqueue(object : Callback<ApiResponse> {
                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@EditProfileActivity, "Biodata berhasil disimpan", Toast.LENGTH_SHORT).show()

                        // Save data to SharedPreferences
                        val editor = sharedPreferences.edit()
                        editor.putString("fullName", fullName)
                        editor.putInt("usia", usiaValue)
                        editor.putFloat("beratBadan", beratBadanValue.toFloat())
                        editor.putFloat("tinggiBadan", tinggiBadanValue.toFloat())
                        editor.putString("jenisKelamin", selectedGender)
                        editor.putString("statusDiabetes", selectedDiabetesStatus)
                        editor.apply()

                        // Navigate to MainActivity
                        val intent = Intent(this@EditProfileActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@EditProfileActivity, "Gagal menyimpan biodata", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    Toast.makeText(this@EditProfileActivity, "Terjadi kesalahan: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        // Handle Generate Token button click
        generateTokenButton.setOnClickListener {
            val token = generateToken()
            sharedPreferences.edit().putString("userToken", token).apply()
            Toast.makeText(this, "Token Generated: $token", Toast.LENGTH_SHORT).show()
        }

        // Handle back button click
        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    // Validate input fields
    private fun validateInput(): Boolean {
        if (namaLengkap.text.isEmpty() || usia.text.isEmpty() || beratBadan.text.isEmpty() || tinggiBadan.text.isEmpty()) {
            Toast.makeText(this, "Mohon lengkapi semua data!", Toast.LENGTH_SHORT).show()
            return false
        }

        // Check if usia is a valid integer
        try {
            usia.text.toString().toInt()
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Usia harus berupa angka!", Toast.LENGTH_SHORT).show()
            return false
        }

        // Check if berat badan and tinggi badan are valid numbers
        try {
            beratBadan.text.toString().toDouble()
            tinggiBadan.text.toString().toDouble()
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Berat dan tinggi badan harus berupa angka!", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    // Function to generate a random 4-digit token
    private fun generateToken(): String {
        val random = Random()
        val token = random.nextInt(9000) + 1000 // Generates a random number between 1000 and 9999
        return token.toString()
    }
}

