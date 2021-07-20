package com.bosha.pomodoro.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bosha.pomodoro.DialogHolder
import com.bosha.pomodoro.databinding.BottomSheetPickerBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlin.time.ExperimentalTime


@ExperimentalTime
class TimePickerBottomSheet() : BottomSheetDialogFragment() {

    private var _binding: BottomSheetPickerBinding? = null
    private val binding get() = _binding!!

    private var selectedHours = 0
    private var selectedMinutes = 0
    private var selectedSeconds = 0
    private var mlUnitFinish = 0L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetPickerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.apply {
            pickerHours.maxValue = 24
            pickerHours.minValue = 0
            pickerHours.value = 0

            pickerMinutes.maxValue = 60
            pickerMinutes.minValue = 0
            pickerMinutes.value = 0

            pickerSeconds.maxValue = 60
            pickerSeconds.minValue = 0
            pickerSeconds.value = 0

            pickerHours.setOnValueChangedListener { _, _, newVal -> selectedHours = newVal }
            pickerMinutes.setOnValueChangedListener { _, _, newVal -> selectedMinutes = newVal }
            pickerSeconds.setOnValueChangedListener { _, _, newVal -> selectedSeconds = newVal }

            fabAddTimer.setOnClickListener {
                mlUnitFinish = (selectedHours * 1000 * 3600).toLong() +
                        (selectedMinutes * 60 * 1000).toLong() +
                        (selectedSeconds * 1000 ).toLong()
                if(mlUnitFinish == 0L) {
                    Toast.makeText(requireContext(), "Choose time", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                (parentFragment as DialogHolder).perform(mlUnitFinish)
                dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}