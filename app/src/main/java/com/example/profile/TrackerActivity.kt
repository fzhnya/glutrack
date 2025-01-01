package com.example.profile

import ApiService
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class TrackerActivity : AppCompatActivity() {

    private lateinit var lineChart: LineChart
    private val glucoseRecords: MutableList<GlucoseData> = mutableListOf()

    private lateinit var recentTitle: TextView
    private lateinit var recentReadingValue: TextView
    private lateinit var recentReadingDate: TextView
    private lateinit var averageTitle: TextView
    private lateinit var averageReadingValue: TextView
    private lateinit var averageReadingMinMax: TextView
    private lateinit var readingsRecyclerView: RecyclerView
    private lateinit var addButton: Button
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracker)

        initializeViews()
        fetchGlucoseRecords()
    }

    private fun initializeViews() {
        // Setup bottom navigation
        bottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.selectedItemId = R.id.nav_tracker
        window.statusBarColor = ContextCompat.getColor(this, R.color.pink)

        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_ocr -> {
                    startActivity(Intent(this, OCRActivity::class.java))
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                R.id.nav_notifikasi -> {
                    startActivity(Intent(this, NotifikasiActivity::class.java))
                    true
                }
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                else -> false
            }
        }

        // Inisialisasi komponen
        lineChart = findViewById(R.id.lineChart)
        recentTitle = findViewById(R.id.recentTitle)
        recentReadingValue = findViewById(R.id.recentReadingValue)
        recentReadingDate = findViewById(R.id.recentReadingDate)
        averageTitle = findViewById(R.id.averageTitle)
        averageReadingValue = findViewById(R.id.averageReadingValue)
        averageReadingMinMax = findViewById(R.id.averageReadingMinMax)
        readingsRecyclerView = findViewById(R.id.readingsRecyclerView)
        addButton = findViewById(R.id.addButton)

        addButton.setOnClickListener {
            val intent = Intent(this, TambahDataActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupLineChart(records: List<GlucoseData>) {
        // Filter records for today only
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val todayRecords = records.filter {
            it.timestamp.startsWith(today)
        }

        if (todayRecords.isEmpty()) {
            Log.e("ChartSetup", "No glucose records for today")
            return
        }

        // Sorting records by timestamp
        val sortedRecords = todayRecords.sortedBy {
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(it.timestamp)
        }

        // Prepare chart entries with gap
        val entries = sortedRecords.mapIndexed { index, record ->
            try {
                val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(record.timestamp)
                Entry(index.toFloat(), record.glucose.toFloat())
            } catch (e: Exception) {
                Log.e("ChartData", "Error parsing date for record: ${e.message}")
                null
            }
        }.filterNotNull()

        // Buat dataset
        // Buat dataset
        // Buat dataset
        val dataSet = LineDataSet(entries, "Kadar Gula Darah Hari Ini").apply {
            color = ContextCompat.getColor(this@TrackerActivity, R.color.light_red)
            setCircleColor(ContextCompat.getColor(this@TrackerActivity, R.color.white))
            circleRadius = 4f
            lineWidth = 2f
            setDrawValues(true)
            valueTextColor = ContextCompat.getColor(this@TrackerActivity, R.color.white)
            mode = LineDataSet.Mode.CUBIC_BEZIER
            valueTextSize = 10f

            // Tambahkan pengaturan untuk highlight
            highLightColor = Color.RED
            setDrawHighlightIndicators(true)
            setDrawVerticalHighlightIndicator(true)
            setDrawHorizontalHighlightIndicator(false)
        }

// Aktifkan highlight pada level chart
        lineChart.apply {
            setHighlightPerTapEnabled(true)  // Tambahkan ini di konfigurasi chart
        }

        // Konfigurasi chart
        lineChart.apply {
            // Pengaturan dasar
            data = LineData(dataSet)
            description.isEnabled = false
            setBackgroundColor(ContextCompat.getColor(this@TrackerActivity, R.color.navy_blue))

            // Tambahkan scroll dan zoom
            setScaleEnabled(true)
            setPinchZoom(true)
            isScrollContainer = true

            // X-Axis configuration
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                textColor = ContextCompat.getColor(this@TrackerActivity, R.color.white)
                setDrawGridLines(false)
                granularity = 1f  // Membuat jarak antar label lebih konsisten
                valueFormatter = object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        val index = value.toInt()
                        return if (index >= 0 && index < sortedRecords.size) {
                            val timestamp = sortedRecords[index].timestamp
                            SimpleDateFormat("HH:mm", Locale.getDefault()).format(
                                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(timestamp) ?: Date()
                            )
                        } else ""
                    }
                }
            }

            // Left Y-Axis configuration
            axisLeft.apply {
                textColor = ContextCompat.getColor(this@TrackerActivity, R.color.white)
                setDrawGridLines(true)
                axisMinimum = 0f
                granularity = 20f
            }

            // Remove right Y-Axis
            axisRight.isEnabled = false

            // Legend configuration
            legend.apply {
                textColor = ContextCompat.getColor(this@TrackerActivity, R.color.white)
                form = Legend.LegendForm.LINE
                horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
            }

            // Animasikan chart
            animateX(1500)

            // Tambahkan marker view untuk detail
            val markerView = CustomMarkerView(this@TrackerActivity, R.layout.marker_view)
            marker = markerView

            invalidate()
        }
    }

    private fun fetchGlucoseRecords() {
        val apiService = RetrofitClient.apiService
        apiService.getGlucoseData().enqueue(object : Callback<List<GlucoseData>> {
            override fun onResponse(call: Call<List<GlucoseData>>, response: Response<List<GlucoseData>>) {
                if (response.isSuccessful && response.body() != null) {
                    response.body()?.let { records ->
                        glucoseRecords.clear()
                        glucoseRecords.addAll(records)

                        // Setup chart dan recycler view
                        setupLineChart(glucoseRecords)
                        setupRecyclerView()
                        updateRecentAndAverageReadings()
                    }
                } else {
                    Log.e("FetchData", "Response Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<GlucoseData>>, t: Throwable) {
                Log.e("FetchData", "Failed: ${t.message}")
            }
        })
    }
    // change 1

    private fun setupRecyclerView() {
        // Filter records for today and sort in descending order
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val todayRecords = glucoseRecords
            .filter { it.timestamp.startsWith(today) }
            .sortedByDescending {
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(it.timestamp)?.time ?: 0
            }

        readingsRecyclerView.layoutManager = LinearLayoutManager(this)
        readingsRecyclerView.adapter = GlucoseRecordAdapter(todayRecords)
    }

    @SuppressLint("SetTextI18n")
    private fun updateRecentAndAverageReadings() {
        if (glucoseRecords.isNotEmpty()) {
            val recentRecord = glucoseRecords.last()

            // Format tanggal dan waktu
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            try {
                val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(recentRecord.timestamp)
                recentTitle.text = "Recent"
                recentReadingValue.text = "${recentRecord.glucose} mg/dL"
                recentReadingDate.text = dateFormat.format(date ?: Date()) // Tampilkan tanggal dengan format default
            } catch (e: ParseException) {
                recentReadingDate.text = "Invalid Date"
            }

            val averageLevel = glucoseRecords.map { it.glucose }.average()
            val minLevel = glucoseRecords.minOf { it.glucose }
            val maxLevel = glucoseRecords.maxOf { it.glucose }

            averageTitle.text = "Average (All)"
            averageReadingValue.text = String.format("%.1f mg/dL", averageLevel)
            averageReadingMinMax.text = "Min: $minLevel - Max: $maxLevel"
        }
    }
}