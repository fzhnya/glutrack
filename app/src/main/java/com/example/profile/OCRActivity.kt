package com.example.profile

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.method.ScrollingMovementMethod
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class OCRActivity : AppCompatActivity() {

    private var cameraImage: ImageView? = null
    private var captureImageButton: Button? = null
    private var resultText: TextView? = null
    private var compositionText: TextView? = null
    private var copyTextButton: Button? = null
    private var cropButton: Button? = null
    private var currentPhotoPath: String? = null
    private var currentBitmap: Bitmap? = null
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var takePictureLauncher: ActivityResultLauncher<Uri>
    private var photoUri: Uri? = null

    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            try {
                result.uriContent?.let { uri ->
                    val croppedBitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                    currentBitmap = croppedBitmap
                    cameraImage?.setImageBitmap(croppedBitmap)
                    processImageForOCR(croppedBitmap)
                }
            } catch (e: Exception) {
                showToast("Error processing cropped image: ${e.message}")
            }
        } else {
            showToast("Cropping failed: ${result.error?.message}")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
            initializeViews()
            setupListeners()
            setupLaunchers()
        } catch (e: Exception) {
            showToast("Error initializing activity: ${e.message}")
            finish()
        }
    }

    private fun initializeViews() {
        try {
            cameraImage = findViewById(R.id.camera_image)
            captureImageButton = findViewById(R.id.captureImgBtn)
            resultText = findViewById(R.id.resulttext)
            compositionText = findViewById(R.id.composition_text)
            copyTextButton = findViewById(R.id.copyTextBtn)
            cropButton = findViewById(R.id.cropButton)

            // Check if any essential views are null
            if (cameraImage == null || captureImageButton == null || resultText == null ||
                compositionText == null || copyTextButton == null || cropButton == null) {
                throw IllegalStateException("Failed to initialize one or more views")
            }

            resultText?.movementMethod = ScrollingMovementMethod()
            compositionText?.movementMethod = ScrollingMovementMethod()
            cropButton?.visibility = View.GONE
            copyTextButton?.visibility = View.GONE
        } catch (e: Exception) {
            throw IllegalStateException("Error initializing views: ${e.message}")
        }
    }

    private fun setupListeners() {
        captureImageButton?.setOnClickListener {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        cropButton?.setOnClickListener {
            photoUri?.let { uri -> startCrop(uri) }
        }
    }

    private fun setupLaunchers() {
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    startCamera()
                } else {
                    showToast("Camera permission denied")
                }
            }

        takePictureLauncher =
            registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
                if (success) {
                    try {
                        photoUri?.let { uri ->
                            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                            currentBitmap = bitmap
                            cameraImage?.setImageBitmap(bitmap)
                            cropButton?.visibility = View.VISIBLE
                            processImageForOCR(bitmap)
                        }
                    } catch (e: Exception) {
                        showToast("Error loading image: ${e.message}")
                    }
                }
            }
    }

    private fun startCamera() {
        try {
            createImageFile()?.also { file ->
                val uri = FileProvider.getUriForFile(
                    this,
                    "${applicationContext.packageName}.provider",
                    file
                )
                photoUri = uri
                takePictureLauncher.launch(uri)
            }
        } catch (e: IOException) {
            showToast("Error creating image file: ${e.message}")
        }
    }

    private fun startCrop(uri: Uri) {
        val cropOptions = CropImageContractOptions(
            uri,
            CropImageOptions().apply {
                guidelines = com.canhub.cropper.CropImageView.Guidelines.ON
                outputCompressFormat = Bitmap.CompressFormat.JPEG
                cropShape = com.canhub.cropper.CropImageView.CropShape.RECTANGLE
            }
        )
        cropImage.launch(cropOptions)
    }

    private fun createImageFile(): File? {
        return try {
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir).apply {
                currentPhotoPath = absolutePath
            }
        } catch (e: IOException) {
            showToast("Error creating image file: ${e.message}")
            null
        }
    }

    private fun processImageForOCR(bitmap: Bitmap) {
        try {
            val enhancedBitmap = enhanceImageForOCR(bitmap)
            recognizeText(enhancedBitmap)
        } catch (e: Exception) {
            showToast("Error processing image: ${e.message}")
        }
    }

    private fun enhanceImageForOCR(original: Bitmap): Bitmap {
        return try {
            val enhanced = original.copy(Bitmap.Config.ARGB_8888, true)
            val canvas = Canvas(enhanced)

            val paint = Paint().apply {
                colorFilter = ColorMatrixColorFilter(ColorMatrix().apply {
                    setSaturation(1.5f)
                })
            }
            canvas.drawBitmap(enhanced, 0f, 0f, paint)

            val paint2 = Paint().apply {
                colorFilter = ColorMatrixColorFilter(ColorMatrix().apply {
                    set(floatArrayOf(
                        2f, -1f, 0f, 0f, 0f,
                        -1f, 2f, -1f, 0f, 0f,
                        0f, -1f, 2f, 0f, 0f,
                        0f, 0f, 0f, 1f, 0f
                    ))
                })
            }
            canvas.drawBitmap(enhanced, 0f, 0f, paint2)

            enhanced
        } catch (e: Exception) {
            original // Return original bitmap if enhancement fails
        }
    }

    private fun recognizeText(bitmap: Bitmap) {
        try {
            val image = InputImage.fromBitmap(bitmap, 0)
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    val rawText = visionText.text
                    resultText?.text = rawText

                    val filteredText = parseTextWithLayout(visionText)
                    compositionText?.text = filteredText

                    setupCopyButton(filteredText)
                }
                .addOnFailureListener { e ->
                    showToast("Failed to recognize text: ${e.message}")
                }
        } catch (e: Exception) {
            showToast("Error in text recognition: ${e.message}")
        }
    }

    private fun parseTextWithLayout(visionText: com.google.mlkit.vision.text.Text): String {
        return try {
            val textBlocks = visionText.textBlocks
            val allLines = mutableListOf<Pair<String, Rect>>()

            for (block in textBlocks) {
                for (line in block.lines) {
                    line.boundingBox?.let { box ->
                        allLines.add(line.text to box)
                    }
                }
            }

            allLines.sortWith(compareBy({ it.second.top }, { it.second.left }))
            val sortedText = allLines.joinToString(" ") { it.first }

            val sugarValue = extractSugarContent(sortedText)?.replace(',', '.') ?: "Tidak ditemukan"
            "Kandungan Gula: $sugarValue g"
        } catch (e: Exception) {
            "Error parsing text: ${e.message}"
        }
    }

    private fun extractSugarContent(text: String): String? {
        val sugarPatterns = listOf(
            Regex("""(?i)gula\s*[:.\s]*\s*(\d+(?:[,.]\d+)?)\s*[gG]"""),
            Regex("""(?i)gula\s+total\s*[:.\s]*\s*(\d+(?:[,.]\d+)?)\s*[gG]"""),
            Regex("""(?i)gula\s*[\(\[{]?\s*sugars?\s*[\)\]}]?\s*[:.\s]*\s*(\d+(?:[,.]\d+)?)\s*[gG]"""),
            Regex("""(?i)total\s+gula\s*[:.\s]*\s*(\d+(?:[,.]\d+)?)\s*[gG]"""),
            Regex("""(?i)sugars?\s*[:.\s]*\s*(\d+(?:[,.]\d+)?)\s*[gG]"""),
            Regex("""(?i)gula[^0-9]*?(\d+(?:[,.]\d+)?)\s*[gG]""")
        )

        return sugarPatterns.firstNotNullOfOrNull { pattern ->
            pattern.find(text)?.groups?.get(1)?.value
        }
    }

    private fun setupCopyButton(text: String) {
        copyTextButton?.apply {
            visibility = View.VISIBLE
            setOnClickListener {
                try {
                    val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("recognized text", text)
                    clipboard.setPrimaryClip(clip)
                    showToast("Text copied to clipboard")
                } catch (e: Exception) {
                    showToast("Error copying text: ${e.message}")
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}