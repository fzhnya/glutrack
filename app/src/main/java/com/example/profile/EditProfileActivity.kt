package com.example.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.profile.databinding.ActivityInputDataBinding
import kotlinx.coroutines.launch

// ViewModel untuk InputDataActivity
class InputDataViewModel : ViewModel() {
    private val _state = MutableLiveData<InputState>()
    val state: LiveData<InputState> = _state

    fun submitBiodata(biodata: Biodata) {
        viewModelScope.launch {
            _state.value = InputState.Loading
            try {
                Log.d("ViewModel", "Submitting biodata: $biodata")
                val response = RetrofitClient.apiService.submitBiodata(biodata) // Pastikan RetrofitClient telah disiapkan
                Log.d("ViewModel", "Received response: $response")
                _state.value = InputState.Success(response)
            } catch (e: Exception) {
                Log.e("ViewModel", "Error submitting biodata: ${e.message}", e)
                _state.value = InputState.Error(e.message ?: "Terjadi kesalahan")
            }
        }
    }
}

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInputDataBinding
    private val viewModel: InputDataViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInputDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        observeViewModel()
    }

    private fun setupViews() {
        binding.btnSubmit.setOnClickListener {
            if (validateInput()) {
                submitData()
            }
        }

        binding.progressBar.visibility = View.GONE
    }

    private fun validateInput(): Boolean {
        with(binding) {
            if (edtNama.text.isNullOrEmpty()) {
                edtNama.error = "Nama harus diisi"
                return false
            }
            if (edtUsia.text.isNullOrEmpty() || edtUsia.text.toString().toIntOrNull() == null) {
                edtUsia.error = "Usia harus diisi dengan angka"
                return false
            }
            if (edtBeratBadan.text.isNullOrEmpty() || edtBeratBadan.text.toString().toDoubleOrNull() == null) {
                edtBeratBadan.error = "Berat badan harus diisi dengan angka"
                return false
            }
            if (edtTinggiBadan.text.isNullOrEmpty() || edtTinggiBadan.text.toString().toDoubleOrNull() == null) {
                edtTinggiBadan.error = "Tinggi badan harus diisi dengan angka"
                return false
            }
            if (rgJenisKelamin.checkedRadioButtonId == -1) {
                Toast.makeText(this@EditProfileActivity, "Pilih jenis kelamin", Toast.LENGTH_SHORT).show(); return false
            }
            if (rgRiwayatKeturunan.checkedRadioButtonId == -1) {
                Toast.makeText(this@EditProfileActivity, "Pilih riwayat keturunan", Toast.LENGTH_SHORT).show()
                return false
            }
            return true
        }
    }

    private fun submitData() {
        with(binding) {
            val biodata = Biodata(
                nama = edtNama.text.toString(),
                usia = edtUsia.text.toString().toInt(),
                jenis_kelamin = if (rgJenisKelamin.checkedRadioButtonId == R.id.rbLakiLaki) "L" else "P",
                berat_badan = edtBeratBadan.text.toString().toDouble(),
                tinggi_badan = edtTinggiBadan.text.toString().toDouble(),
                riwayat_keturunan = when (rgRiwayatKeturunan.checkedRadioButtonId) {
                    R.id.rbTidakAda -> 0
                    R.id.rbSatuOrtu -> 1
                    else -> 2
                },
                riwayat_diabetes = if (switchDiabetes.isChecked) 1 else 0,
                prediksi_gula = -1.0 // Nilai awal untuk menandai bahwa prediksi belum diterima
            )
            viewModel.submitBiodata(biodata)
        }
    }

    private fun observeViewModel() {
        viewModel.state.observe(this) { state ->
            when (state) {
                is InputState.Loading -> showLoading()
                is InputState.Success -> {
                    hideLoading()

                    // Use the Biodata from the ApiResponse
                    val biodata = state.response.user_data // assuming response.data contains the Biodata object
                    navigateToResult(biodata)

                }
                is InputState.Error -> {
                    hideLoading()
                    showError(state.message)
                }
            }
        }
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.btnSubmit.isEnabled = false
    }

    private fun hideLoading() {
        binding.progressBar.visibility = View.GONE
        binding.btnSubmit.isEnabled = true
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun navigateToResult(biodata: Biodata) {
        try {
            val intent = Intent(this, ResultActivity::class.java).apply {
                putExtra("biodata", biodata)
            }
            startActivity(intent)


            // Reset form setelah berhasil
            resetForm()
        } catch (e: Exception) {
            Log.e("InputActivity", "Error navigating to result: ${e.message}", e)
            Toast.makeText(this, "Terjadi kesalahan: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun resetForm() {
        binding.apply {
            edtNama.text?.clear()
            edtUsia.text?.clear()
            edtBeratBadan.text?.clear()
            edtTinggiBadan.text?.clear()
            rgJenisKelamin.clearCheck()
            rgRiwayatKeturunan.clearCheck()
            switchDiabetes.isChecked = false
        }
    }
}



