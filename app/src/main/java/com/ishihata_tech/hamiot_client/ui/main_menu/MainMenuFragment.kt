package com.ishihata_tech.hamiot_client.ui.main_menu

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.zxing.BarcodeFormat
import com.google.zxing.integration.android.IntentIntegrator
import com.ishihata_tech.hamiot_client.R
import com.ishihata_tech.hamiot_client.databinding.MainMenuFragmentBinding
import com.ishihata_tech.hamiot_client.ui.common.ProgressDialogFragment
import com.journeyapps.barcodescanner.BarcodeEncoder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


@AndroidEntryPoint
class MainMenuFragment : Fragment() {
    companion object {
        private const val TAG = "MainMenuFragment"
        private const val PROGRESS_DIALOG_TAG = "progressDialog"
    }

    private val viewModel: MainMenuViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // エラーの監視
        lifecycleScope.launch {
            viewModel.errorMessage.collect { message ->
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }

        // ぐるぐる表示の監視
        viewModel.progressDialogShown.observe(this) {
            when (it) {
                true -> {
                    ProgressDialogFragment().show(parentFragmentManager, PROGRESS_DIALOG_TAG)
                }
                false -> {
                    (parentFragmentManager.findFragmentByTag(PROGRESS_DIALOG_TAG) as? ProgressDialogFragment)?.dismiss()
                }
            }
        }

        // 送金の監視
        lifecycleScope.launch {
            viewModel.transferAccountIdAndDisplayName.collect {
                val accountId = it.first
                val displayName = it.second
                findNavController().navigate(
                        MainMenuFragmentDirections.actionMainMenuFragmentToTransferAssetFragment(
                                displayName, accountId)
                )
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DataBindingUtil.inflate<MainMenuFragmentBinding>(
            inflater,
            R.layout.main_menu_fragment,
            container,
            false
        ).also {
            it.lifecycleOwner = this
            it.viewModel = viewModel
        }

        // 残高を表示する
        viewModel.balance.observe(viewLifecycleOwner) {
            binding.textBalance.text = "$it HMT"
        }

        // QRコードを表示する
        try {
            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.encodeBitmap(
                viewModel.qrCodeContent, BarcodeFormat.QR_CODE, 256, 256
            )
            val drawable = BitmapDrawable(resources, bitmap)
            drawable.isFilterBitmap = false
            binding.imageQr.setImageDrawable(drawable)
        } catch (e: Exception) {
            Log.d(TAG, "Failure to create qr code", e)
        }

        // スキャンボタン
        binding.buttonScan.setOnClickListener {
            IntentIntegrator.forSupportFragment(this@MainMenuFragment).initiateScan()
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshBalance()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        result?.contents?.also { contents ->
            viewModel.onScanQr(contents)
        }
    }
}