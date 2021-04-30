package com.ishihata_tech.hamiot_client.ui.transfer_asset

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ishihata_tech.hamiot_client.R
import com.ishihata_tech.hamiot_client.databinding.TransferAssetFragmentBinding
import com.ishihata_tech.hamiot_client.ui.common.ProgressDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TransferAssetFragment : Fragment(), SuccessDialogFragment.Listener {
    companion object {
        private const val PROGRESS_DIALOG_TAG = "progressDialog"
        private const val SUCCESS_DIALOG_TAG = "SuccessDialog"
    }

    private val viewModel: TransferAssetViewModel by viewModels()
    private val args: TransferAssetFragmentArgs by navArgs()
    private lateinit var binding: TransferAssetFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // エラーの監視
        lifecycleScope.launch {
            viewModel.errorMessage.collect {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }

        // ぐるぐるの表示状態の監視
        viewModel.progressDialogShown.observe(this) {
            when (it) {
                true -> {
                    ProgressDialogFragment().showNow(parentFragmentManager, PROGRESS_DIALOG_TAG)
                }
                false -> {
                    (parentFragmentManager.findFragmentByTag(PROGRESS_DIALOG_TAG) as? ProgressDialogFragment)?.dismiss()
                }
            }
        }

        // アクションの監視
        lifecycleScope.launch {
            viewModel.action.collect {
                when (it) {
                    TransferAssetViewModel.Action.GO_BACK -> {
                        findNavController().popBackStack()
                    }
                    TransferAssetViewModel.Action.DISPLAY_SUCCESS -> {
                        SuccessDialogFragment().showNow(childFragmentManager, SUCCESS_DIALOG_TAG)
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate<TransferAssetFragmentBinding>(
                inflater,
                R.layout.transfer_asset_fragment,
                container,
                false
        ).also {
            it.lifecycleOwner = this
            it.viewModel = viewModel
        }

        // 送金相手の表示名
        binding.textOpponentName.text = args.opponentDisplayName

        // 送金ボタン
        binding.buttonTransfer.setOnClickListener {
            viewModel.onTransferButton(args.opponentAccountId, binding.editAmount.text.toString())
        }

        // 戻るボタン
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        return binding.root
    }

    // 成功ダイアログが閉じられたら呼ばれる
    override fun onCloseSuccessDialog() {
        findNavController().popBackStack()
    }
}