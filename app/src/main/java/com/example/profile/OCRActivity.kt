package com.example.profile

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class OCRActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var resultText: EditText
    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private val CAMERA_PERMISSION_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ocr)

        // Initialize views
        imageView = findViewById(R.id.imageview)
        resultText = findViewById(R.id.result_text)
        val btnTakePicture: Button = findViewById(R.id.btn_picture)
        val btnCopyText: Button = findViewById(R.id.btn_copy)

        // Register Camera Launcher
        cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val imageBitmap = result.data?.extras?.get("data") as? Bitmap
                if (imageBitmap != null) {
                    imageView.setImageBitmap(imageBitmap)
                    processImageForText(imageBitmap)
                } else {
                    Toast.makeText(this, "Failed to capture image", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Set onClickListener for buttons
        btnTakePicture.setOnClickListener {
            checkCameraPermission()
        }

        btnCopyText.setOnClickListener {
            copyTextToClipboard()
        }
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
        } else {
            openCamera()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            cameraLauncher.launch(takePictureIntent)
        } else {
            Toast.makeText(this, "Camera not available", Toast.LENGTH_SHORT).show()
        }
    }

    private fun processImageForText(bitmap: Bitmap) {
        try {
            val image = InputImage.fromBitmap(bitmap, 0)
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    val fullText = visionText.text
                    if (fullText.isNotBlank()) {
                        val extractedData = extractNutritionalData(fullText)
                        resultText.setText(extractedData)
                    } else {
                        Toast.makeText(this, "No text found in image", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to extract text: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } catch (e: Exception) {
            Toast.makeText(this, "Error processing image: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun extractNutritionalData(text: String): String {
        val nutrients = listOf("karbohidrat", "gula", "lemak jenuh")
        val extractedData = StringBuilder()

        extractedData.append("Informasi Nutrisi yang Ditemukan:\n")

        // Pastikan teks dalam huruf kecil untuk mempermudah pencocokan
        val normalizedText = text.toLowerCase()

        for (nutrient in nutrients) {
            // Regex untuk mencocokkan nama nutrisi dan angka setelahnya
            // Memperbaiki regex untuk menangkap angka desimal dan angka bulat
            val regex = Regex("(?i)$nutrient\\s*:?\\s*(\\d+\\.\\d+|\\d+)?")

            // Cari kecocokan dengan regex
            val match = regex.find(normalizedText)

            if (match != null) {
                // Ambil jumlah dari hasil pencocokan, jika ada
                val amount = match.groups[1]?.value ?: "Tidak Ditemukan"
                extractedData.append("$nutrient: $amount\n")
            } else {
                extractedData.append("$nutrient: Tidak Ditemukan\n")
            }
        }

        return extractedData.toString()
    }




    private fun copyTextToClipboard() {
        val textToCopy = resultText.text.toString()
        if (textToCopy.isNotBlank()) {
            val clipboard = getSystemService(CLIPBOARD_SERVICE) as android.content.ClipboardManager
            val clip = android.content.ClipData.newPlainText("Copied Text", textToCopy)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "Text copied to clipboard", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "No text to copy", Toast.LENGTH_SHORT).show()
        }
    }
}