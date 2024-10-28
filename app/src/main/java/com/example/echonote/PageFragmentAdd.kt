package com.example.echonote

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment

enum class ItemOptions(val displayName: String) {
    OPTION_ONE("CS240"),
    OPTION_TWO("CS241"),
    OPTION_THREE("CS242");
    override fun toString(): String {
        return displayName
    }
}

class PageFragmentAdd : Fragment() {
    private lateinit var summarization: Summarization
    private lateinit var etQuestion: EditText
    private lateinit var btnRecord: Button
    private lateinit var tvRecordedText: TextView
    private lateinit var radioGroupToggle: RadioGroup

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_page_add, container, false)
        summarization = Summarization()
        etQuestion = view.findViewById(R.id.etQuestion)
        btnRecord = view.findViewById(R.id.btnRecord)
        tvRecordedText = view.findViewById(R.id.tvRecordedText)
        radioGroupToggle = view.findViewById(R.id.toggle)

        val btnSubmit = view.findViewById<Button>(R.id.btnSubmit)
        val spinnerOptions = view.findViewById<Spinner>(R.id.spinnerOptions)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, ItemOptions.entries)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerOptions.adapter = adapter
        spinnerOptions.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedOption = ItemOptions.entries[position]
                Toast.makeText(requireContext(), "Selected: ${selectedOption.displayName}", Toast.LENGTH_SHORT).show()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // function for no selection
            }
        }

        // Set the "Record" RadioButton as checked by default
        val recordRadioButton = view.findViewById<RadioButton>(R.id.record)
        recordRadioButton.isChecked = true

        // Show "Record" mode UI elements by default
        etQuestion.visibility = View.GONE
        btnRecord.visibility = View.VISIBLE
        tvRecordedText.visibility = View.VISIBLE

        btnSubmit.setOnClickListener {
            val question = etQuestion.text.toString()
            if (question.isNotEmpty()) {
                summarization.getSummary(question) { response ->
                    activity?.runOnUiThread {
                        val bottomSheetFragment = BottomSheetFragment.newInstance(response)
                        bottomSheetFragment.show(parentFragmentManager, "BottomSheetFragment")
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Please input text to summarize!", Toast.LENGTH_SHORT).show()
            }
        }

        radioGroupToggle.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.record -> { // "Record" mode
                    etQuestion.visibility = View.GONE
                    btnRecord.visibility = View.VISIBLE
                    tvRecordedText.visibility = View.VISIBLE
                }
                R.id.text -> { // "Text" mode
                    etQuestion.visibility = View.VISIBLE
                    btnRecord.visibility = View.GONE
                    tvRecordedText.visibility = View.GONE
                }
            }
        }

        return view
    }
}
