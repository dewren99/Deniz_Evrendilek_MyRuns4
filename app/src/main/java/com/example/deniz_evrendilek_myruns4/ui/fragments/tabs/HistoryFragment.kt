package com.example.deniz_evrendilek_myruns4.ui.fragments.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.deniz_evrendilek_myruns4.R
import com.example.deniz_evrendilek_myruns4.constants.PreferenceConstants.UNIT_PREFERENCE_DEFAULT
import com.example.deniz_evrendilek_myruns4.data.model.ExerciseEntry
import com.example.deniz_evrendilek_myruns4.ui.adapters.ListViewAdapter
import com.example.deniz_evrendilek_myruns4.ui.viewmodel.ExerciseEntryViewModel
import com.example.deniz_evrendilek_myruns4.ui.viewmodel.ExerciseEntryViewModelFactory

class HistoryFragment : Fragment() {
    private lateinit var view: View
    private lateinit var exerciseEntryViewModelFactory: ExerciseEntryViewModelFactory
    private lateinit var exerciseEntryViewModel: ExerciseEntryViewModel
    private lateinit var listView: ListView
    private lateinit var listViewAdapter: ListViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        view = inflater.inflate(R.layout.fragment_history, container, false)
        listView = view.findViewById(R.id.history_list_view)
        listViewAdapter = ListViewAdapter(
            requireContext(), emptyArray(), UNIT_PREFERENCE_DEFAULT, ::handleHistoryItemClick
        )
        listView.adapter = listViewAdapter

        exerciseEntryViewModelFactory = ExerciseEntryViewModelFactory(requireActivity())
        exerciseEntryViewModel = ViewModelProvider(
            requireActivity(), exerciseEntryViewModelFactory
        )[ExerciseEntryViewModel::class.java]

        exerciseEntryViewModel.allExerciseEntries.observe(viewLifecycleOwner) { (items, unit) ->
            if (items == null || unit == null) {
                return@observe
            }
            listViewAdapter = ListViewAdapter(
                requireContext(), items.toTypedArray(), unit, ::handleHistoryItemClick
            )
            listView.adapter = listViewAdapter
        }
        return view
    }

    private fun handleHistoryItemClick(exerciseEntry: ExerciseEntry) {
        exerciseEntryViewModel.display(exerciseEntry)
        findNavController().navigate(R.id.action_mainFragment_to_displayEntryFragment)
    }
}