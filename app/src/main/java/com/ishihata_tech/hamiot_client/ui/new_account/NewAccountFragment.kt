package com.ishihata_tech.hamiot_client.ui.new_account

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ishihata_tech.hamiot_client.R
import com.ishihata_tech.hamiot_client.databinding.NewAccountFragmentBinding
import com.ishihata_tech.hamiot_client.ui.common.ProgressDialogFragment
import com.ishihata_tech.hamiot_client.ui.restore_account.RestoreAccountDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NewAccountFragment : Fragment() {
    companion object {
        private const val PROGRESS_DIALOG_TAG = "progressDialog"
        private const val RESTORE_ACCOUNT_DIALOG_TAG = "restoreAccountDialog"
    }

    private val viewModel: NewAccountViewModel by viewModels()

    private val filePickerCallback =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult? ->
                val uri = result?.data?.data
                if (result?.resultCode == Activity.RESULT_OK && uri != null) {
                    // ファイル選択が終わったらパスワード入力ダイアログを表示する
                    childFragmentManager.setFragmentResultListener(
                        RestoreAccountDialogFragment.REQUEST_KEY,
                        this
                    ) { _, bundle ->
                        bundle.getString("password")?.also { password ->
                            viewModel.restoreAccount(uri, password)
                        }
                    }
                    RestoreAccountDialogFragment().showNow(childFragmentManager, RESTORE_ACCOUNT_DIALOG_TAG)
                }
            }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // アクションの監視
        lifecycleScope.launch {
            viewModel.action.collect {
                when(it) {
                    NewAccountViewModel.Action.GO_TO_MAIN_MENU -> {
                        findNavController().navigate(
                            NewAccountFragmentDirections.actionNewAccountFragmentToMainMenuFragment()
                        )
                    }
                }
            }
        }

        // エラーの監視
        lifecycleScope.launch {
            viewModel.errorMessage.collect {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
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
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = DataBindingUtil.inflate<NewAccountFragmentBinding>(inflater, R.layout.new_account_fragment, container, false).also {
            it.lifecycleOwner = this
            it.viewModel = viewModel
        }

        // ツールバー
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)

        // 送信ボタン
        binding.buttonSubmit.setOnClickListener {
            val displayName = binding.editName.text.toString().trim()
            if (displayName.isNotEmpty()) {
                viewModel.createNewAccount(displayName)
            }
        }

        // リストアボタン
        binding.buttonRestore.setOnClickListener {
            filePickerCallback.launch(Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "text/json"
            })
        }

        return binding.root
    }
}