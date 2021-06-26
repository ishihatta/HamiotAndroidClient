package com.ishihata_tech.hamiot_client.ui.add_asset

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ishihata_tech.hamiot_client.R
import com.ishihata_tech.hamiot_client.databinding.AddAssetFragmentBinding
import com.ishihata_tech.hamiot_client.ui.common.ProgressDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddAssetFragment : Fragment() {
    companion object {
        private const val PROGRESS_DIALOG_TAG = "progressDialog"
    }

    private val viewModel: AddAssetViewModel by viewModels()
    private lateinit var binding: AddAssetFragmentBinding

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
                    AddAssetViewModel.Action.GO_BACK -> {
                        findNavController().popBackStack()
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate<AddAssetFragmentBinding>(
                inflater,
                R.layout.add_asset_fragment,
                container,
                false
        ).also {
            it.lifecycleOwner = this
            it.viewModel = viewModel
        }

        // ツールバー
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)

        // 実行ボタン
        binding.buttonSubmit.setOnClickListener {
            viewModel.onAddAssetButton(binding.editAmount.text.toString())
        }

        // 戻るボタン
        binding.toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }

        return binding.root
    }
}