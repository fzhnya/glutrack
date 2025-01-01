package com.example.profile

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Locale

class GlucoseRecordAdapter(private val glucoseRecords: List<GlucoseData>) : RecyclerView.Adapter<GlucoseRecordAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val glucoseRecords: MutableList<GlucoseData> = mutableListOf()

        val valueTextView: TextView = itemView.findViewById(R.id.valueTextView)
        val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)

        private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val sortedGlucoseRecords = glucoseRecords.sortedByDescending {
            dateFormat.parse(it.timestamp)?.time
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_reading, parent, false)
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val record = glucoseRecords[position]
        holder.valueTextView.text = "${record.glucose} mg/dL"
        holder.dateTextView.text = record.timestamp
    }

    override fun getItemCount(): Int {
        return glucoseRecords.size
    }
}
