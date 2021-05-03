package com.ishihata_tech.hamiot_client.ui.main_menu

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.ishihata_tech.hamiot_client.R

class LogoutDialogFragment : DialogFragment() {
    interface Listener {
        fun onDoLogout()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setMessage(R.string.logout_confirm_message)
                    .setPositiveButton(R.string.ok) { _, _ ->
                        val parent = parentFragment
                        if (parent is Listener) {
                            parent.onDoLogout()
                        }
                    }
                    .setNegativeButton(R.string.cancel, null)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}