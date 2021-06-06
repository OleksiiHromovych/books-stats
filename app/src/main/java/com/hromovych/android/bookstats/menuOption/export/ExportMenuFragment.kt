package com.hromovych.android.bookstats.menuOption.export

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import com.hromovych.android.bookstats.R
import com.hromovych.android.bookstats.helpersItems.showNotYetImplementedDialog

class ExportMenuFragment : Fragment(R.layout.fragment_export_menu), View.OnClickListener {

    companion object {
        @JvmStatic
        fun newInstance(): Fragment {
            return ExportMenuFragment()
        }
    }

    private lateinit var exportText: Button
    private lateinit var exportBD: Button


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        exportText = view.findViewById(R.id.export_as_text_btn)
        exportBD = view.findViewById(R.id.export_as_bd_btn)

        exportText.setOnClickListener(this)
        exportBD.setOnClickListener(this)

    }

    override fun onClick(v: View) {
        val fragment: Fragment = when (v.id) {
            R.id.export_as_text_btn -> ToTextFragment.newInstance()
            R.id.export_as_bd_btn -> {
                requireContext().showNotYetImplementedDialog()
                //                fragment = ToFileFragment.newInstance();
                //break;
                return
            }
            else -> throw IllegalStateException("Unexpected value: " + v.id)
        }
        requireActivity().supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, fragment)
            .addToBackStack(null)
            .commit()
    }
}
