package com.hromovych.android.bookstats.helpersItems

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.hromovych.android.bookstats.R

fun Context.showNotYetImplementedDialog() {
    AlertDialog.Builder(this, R.style.AlertDialogCustom)
            .setTitle(R.string.not_yet_implemented)
            .setPositiveButton(R.string.ok, null)
            .show()
}