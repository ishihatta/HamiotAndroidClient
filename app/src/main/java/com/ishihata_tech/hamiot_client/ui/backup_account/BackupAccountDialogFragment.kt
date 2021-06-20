package com.ishihata_tech.hamiot_client.ui.backup_account

import android.app.Activity
import android.content.Intent
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.ishihata_tech.hamiot_client.R
import com.ishihata_tech.hamiot_client.databinding.BackupAccountDialogBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class BackupAccountDialogFragment : DialogFragment() {
    private val viewModel: BackupAccountDialogViewModel by viewModels()

    /**
     * アカウントのバックアップ先選択画面のコールバック
     */
    private val filePickerCallback =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult? ->
            val uri = result?.data?.data
            if (result?.resultCode == Activity.RESULT_OK && uri != null) {
                if (viewModel.backupAccount(uri)) {
                    // 成功
                    Toast.makeText(
                        requireContext(),
                        R.string.successfully_backup_account,
                        Toast.LENGTH_SHORT
                    ).show()
                    dismiss()
                } else {
                    // 失敗
                    Toast.makeText(
                        requireContext(),
                        R.string.error_backup_account,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DataBindingUtil.inflate<BackupAccountDialogBinding>(inflater, R.layout.backup_account_dialog, container, false).also {
            it.lifecycleOwner = this
            it.viewModel = viewModel
        }

        // 2つのパスワードが一致しているときだけOKボタンを有効にする
        viewModel.password.observe(viewLifecycleOwner) {
            binding.buttonOk.isEnabled = viewModel.password.value == viewModel.passwordConfirm.value
        }
        viewModel.passwordConfirm.observe(viewLifecycleOwner) {
            binding.buttonOk.isEnabled = viewModel.password.value == viewModel.passwordConfirm.value
        }

        // OKボタン
        binding.buttonOk.setOnClickListener {
            filePickerCallback.launch(Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "text/json"
                putExtra(Intent.EXTRA_TITLE, "hamiot_account.json")
            })
        }

        // キャンセルボタン
        binding.buttonCancel.setOnClickListener { dismiss() }

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        dialog?.window?.also { window ->
            @Suppress("DEPRECATION")
            val width = if (Build.VERSION.SDK_INT < 30) {
                val display = window.windowManager.defaultDisplay
                val size = Point()
                display.getSize(size)
                size.x
            } else {
                val bounds = window.windowManager.currentWindowMetrics.bounds
                bounds.width()
            }

            window.setLayout((width * 0.9).toInt(), WindowManager.LayoutParams.WRAP_CONTENT)
            window.setGravity(Gravity.CENTER)
        }
    }
}