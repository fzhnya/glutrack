package com.example.profile

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.widget.Button
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import java.util.*

class TambahDataActivity : AppCompatActivity() {

    private lateinit var glucoseLevelInput: EditText
    private lateinit var saveButton: Button

    // List sementara untuk menyimpan data
    private val glucoseRecords = mutableListOf<GlucoseRecords>()
    private val calendar = Calendar.getInstance()

    private lateinit var yearPicker: NumberPicker
    private lateinit var monthPicker: NumberPicker
    private lateinit var dayPicker: NumberPicker
    private lateinit var hourPicker: NumberPicker
    private lateinit var minutePicker: NumberPicker

    private val months = arrayOf(
        "Jan", "Feb", "Mar", "Apr", "Mei", "Juni",
        "Juli", "Agu", "Sept", "Okt", "Nov", "Des"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tambah_data)
        window.statusBarColor = ContextCompat.getColor(this, R.color.pink)


        // Inisialisasi views
        glucoseLevelInput = findViewById(R.id.glucoseLevelInput)
        saveButton = findViewById(R.id.saveButton)

        // Inisialisasi NumberPickers
        yearPicker = findViewById(R.id.yearPicker)
        monthPicker = findViewById(R.id.monthPicker)
        dayPicker = findViewById(R.id.dayPicker)
        hourPicker = findViewById(R.id.hourPicker)
        minutePicker = findViewById(R.id.minutePicker)

        setupNumberPickers()

        // Listener untuk tombol simpan
        saveButton.setOnClickListener {
            saveData()
        }
    }

    private fun setupNumberPickers() {
        // Set Year Picker

        // Set Year Picker
        val currentYear = calendar.get(Calendar.YEAR)
        yearPicker.minValue = 2020
        yearPicker.maxValue = 2030
        yearPicker.value = currentYear

        // Set Month Picker with names
        monthPicker.minValue = 1
        monthPicker.maxValue = months.size
        monthPicker.displayedValues = months // Pastikan ini diatur setelah min & max
        monthPicker.value = calendar.get(Calendar.MONTH) + 1

        // Set Day Picker with two-digit format
        dayPicker.minValue = 1
        dayPicker.maxValue = 31
        dayPicker.value = calendar.get(Calendar.DAY_OF_MONTH)

        // Set Hour and Minute Pickers with two-digit format
        hourPicker.minValue = 0
        hourPicker.maxValue = 23
        hourPicker.value = calendar.get(Calendar.HOUR_OF_DAY)

        minutePicker.minValue = 0
        minutePicker.maxValue = 59
        minutePicker.value = calendar.get(Calendar.MINUTE)
    }

    @SuppressLint("DefaultLocale")
    private fun saveData() {
        val glucoseLevel = glucoseLevelInput.text.toString().toFloatOrNull()
        if (glucoseLevel == null) {
            Toast.makeText(this, "Please enter a valid glucose level", Toast.LENGTH_SHORT).show()
            return
        }

        // Membuat format tanggal dan waktu terpilih
        val selectedDate = "${yearPicker.value}-${months[monthPicker.value - 1]}-${
            String.format(
                "%02d",
                dayPicker.value
            )
        }"
        val selectedTime = "${String.format("%02d", hourPicker.value)}:${
            String.format(
                "%02d",
                minutePicker.value
            )
        }"


        val record = GlucoseRecords(
            date = selectedDate,
            time = selectedTime,
            level = glucoseLevel
        )

        // Tambahkan data ke dalam list sementara
        glucoseRecords.add(record)

        // Kirim data ke ChartActivity
        if (glucoseRecords.isNotEmpty()) {
            // Pass the data to ChartActivity
            val intent = Intent(this, TrackerActivity::class.java).apply {
                putParcelableArrayListExtra("glucose_records", ArrayList(glucoseRecords))
            }
            startActivity(intent)
            Toast.makeText(this, "Data added and sent to chart", Toast.LENGTH_SHORT).show()
            finish()
        } else {
            Toast.makeText(this, "No data to pass", Toast.LENGTH_SHORT).show()
        }
}


    data class GlucoseRecords(
        val date: String,
        val time: String,
        val level: Float
    ) : Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readFloat()
        )

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(date)
            parcel.writeString(time)
            parcel.writeFloat(level)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<GlucoseRecords> {
            override fun createFromParcel(parcel: Parcel): GlucoseRecords {
                return GlucoseRecords(parcel)
            }

            override fun newArray(size: Int): Array<GlucoseRecords?> {
                return arrayOfNulls(size)
            }
        }
    }
}


