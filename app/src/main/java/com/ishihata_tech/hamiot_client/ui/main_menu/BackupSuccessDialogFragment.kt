package com.ishihata_tech.hamiot_client.ui.main_menu

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.ishihata_tech.hamiot_client.R

class BackupSuccessDialogFragment : DialogFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setMessage(R.string.successfully_backup_account)
                    .setPositiveButton(R.string.ok, null)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}