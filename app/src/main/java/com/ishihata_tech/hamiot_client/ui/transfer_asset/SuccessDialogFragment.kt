package com.ishihata_tech.hamiot_client.ui.transfer_asset

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.ishihata_tech.hamiot_client.R

/**
 * 送金に成功したときに表示するダイアログ
 */
class SuccessDialogFragment : DialogFragment() {
    interface Listener {
        fun onCloseSuccessDialog()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setMessage(R.string.successfully_transfer_message)
                    .setPositiveButton(R.string.ok) { _, _ ->
                        val parent = parentFragment
                        if (parent is Listener) {
                            parent.onCloseSuccessDialog()
                        }
                    }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}