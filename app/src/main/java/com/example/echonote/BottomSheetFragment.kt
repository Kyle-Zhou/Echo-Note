package com.example.echonote

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import io.noties.markwon.Markwon
import io.noties.markwon.ext.latex.JLatexMathPlugin

class BottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var summaryText: String
    private lateinit var markwon: Markwon

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bottom_sheet, container, false)
        val txtSummary = view.findViewById<TextView>(R.id.txtSummary)

        // Initialize Markwon with LaTeX support
        markwon = Markwon.builder(requireContext())
            .usePlugin(JLatexMathPlugin.create(txtSummary.textSize, txtSummary.textSize))
            .build()
        // Apply Markwon to render markdown in summary text
        markwon.setMarkdown(txtSummary, summaryText)

        return view
    }

    companion object {
        fun newInstance(summary: String): BottomSheetFragment {
            val fragment = BottomSheetFragment()
            fragment.summaryText = summary
            return fragment
        }
    }
}
