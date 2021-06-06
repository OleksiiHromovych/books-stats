package com.hromovych.android.bookstats.menuOption.import_option

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.hromovych.android.bookstats.R
import com.hromovych.android.bookstats.helpersItems.showNotYetImplementedDialog

class ImportMenuFragment : Fragment() {

    companion object {
        @JvmStatic
        fun newInstance() = ImportMenuFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_import_menu, container, false)

        val importFromClipboard = view.findViewById<Button>(R.id.import_from_clipboard_btn)
        val importFromFile = view.findViewById<Button>(R.id.import_from_file_btn)

        importFromClipboard.setOnClickListener {
            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, ClipboardFragment.newInstance())
                .commit()
        }

        importFromFile.setOnClickListener {
            requireActivity().showNotYetImplementedDialog()
        }

        return view
    }

}