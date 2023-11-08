package com.example.deniz_evrendilek_myruns4.ui.fragments.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.deniz_evrendilek_myruns4.R
import com.example.deniz_evrendilek_myruns4.ui.viewmodel.StartFragmentViewModel


class StartFragment : Fragment() {
    private lateinit var spinnerInputType: Spinner
    private lateinit var spinnerActivityType: Spinner
    private lateinit var buttonStart: Button
    private lateinit var startFragmentViewModel: StartFragmentViewModel
    private lateinit var inputTypes: Array<String>
    private lateinit var activityTypes: Array<String>
    private lateinit var view: View


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_start, container, false)
        setupLateInits()
        setupAdapters()
        setupListeners()
        setupViewModel()

        return view
    }

    private fun setupViewModel() {
        startFragmentViewModel =
            ViewModelProvider(requireActivity())[StartFragmentViewModel::class.java]

        startFragmentViewModel.inputType.observe(viewLifecycleOwner) { inputType ->
            if (inputType == null) {
                throw NoSuchFieldException("Input Type cannot be empty")
            }
            buttonStart.setOnClickListener {
                when (inputType) {
                    inputTypes[0] -> navigateToEntryCreation()
                    inputTypes[1] -> navigateToMap()
                    inputTypes[2] -> navigateToMap()
                    else -> throw IllegalStateException("Unexpected input type navigation: $inputType")
                }
            }
        }
    }

    private fun setupLateInits() {
        spinnerInputType = view.findViewById(R.id.spinner_input_type)
        spinnerActivityType = view.findViewById(R.id.spinner_activity_type)
        buttonStart = view.findViewById(R.id.start)
        inputTypes = resources.getStringArray(R.array.InputType)
        activityTypes = resources.getStringArray(R.array.ActivityType)
    }

    private fun setupAdapters() {
        spinnerInputType.adapter = ArrayAdapter(
            view.context, android.R.layout.simple_spinner_dropdown_item, inputTypes
        )
        spinnerActivityType.adapter = ArrayAdapter(
            view.context, android.R.layout.simple_spinner_dropdown_item, activityTypes
        )
    }

    private fun setupListeners() {
        spinnerActivityType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                if (parent == null) {
                    throw IllegalAccessError("No parent found")
                }
                val selected = parent.getItemAtPosition(position).toString()
                startFragmentViewModel.setActivityType(selected)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        spinnerInputType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                if (parent == null) {
                    throw IllegalAccessError("No parent found")
                }
                val selected = parent.getItemAtPosition(position).toString()
                startFragmentViewModel.setInputType(selected)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
    }

    private fun navigateToMap() {
        findNavController().navigate(R.id.action_mainFragment_to_mapFragment)
    }

    private fun navigateToEntryCreation() {
        findNavController().navigate(R.id.action_mainFragment_to_entryFragment)
    }
}