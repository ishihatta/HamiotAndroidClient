package com.ishihata_tech.hamiot_client.ui.restore_account

import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import com.ishihata_tech.hamiot_client.R
import com.ishihata_tech.hamiot_client.databinding.RestoreAccountDialogBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RestoreAccountDialogFragment : DialogFragment() {
    companion object {
        const val REQUEST_KEY = "RestoreAccountDialog"
    }

    private val viewModel: RestoreAccountDialogViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DataBindingUtil.inflate<RestoreAccountDialogBinding>(inflater, R.layout.restore_account_dialog, container, false).also {
            it.lifecycleOwner = this
            it.viewModel = viewModel
        }

        // OKボタン
        binding.buttonOk.setOnClickListener {
            setFragmentResult(REQUEST_KEY, bundleOf("password" to viewModel.password.value))
            dismiss()
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