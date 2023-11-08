package com.example.deniz_evrendilek_myruns4.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.deniz_evrendilek_myruns4.R
import com.example.deniz_evrendilek_myruns4.constants.ExerciseTypes
import com.example.deniz_evrendilek_myruns4.constants.InputTypes
import com.example.deniz_evrendilek_myruns4.data.model.ExerciseEntry
import com.example.deniz_evrendilek_myruns4.data.model.ManualExerciseEntryForm


class ListViewAdapter(
    private val context: Context,
    private var exerciseEntryList: Array<ExerciseEntry>,
    private val unitPreference: String,
    private val onHistoryItemClick: (ExerciseEntry) -> Unit,
) : ArrayAdapter<ExerciseEntry>(context, R.layout.history_item, exerciseEntryList) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view =
            convertView ?: LayoutInflater.from(context)
                .inflate(R.layout.history_item, parent, false)

        val item = exerciseEntryList[position]

        val inputType = InputTypes.getString(item.inputType)
        val exerciseType = ExerciseTypes.getString(item.activityType)
        val dateTime = ManualExerciseEntryForm.getDateTimeStr(item)
        val duration = ManualExerciseEntryForm.getDurationStr(item)
        val distance = ManualExerciseEntryForm.getDistanceStr(unitPreference, item)

        val title = "$inputType: $exerciseType, $dateTime"
        val text = "$distance $duration"

        view.findViewById<TextView>(R.id.history_item_title).text = title
        view.findViewById<TextView>(R.id.history_item_text).text = text

        view.setOnClickListener { _ ->
            onHistoryItemClick(item)
        }
        return view
    }
}