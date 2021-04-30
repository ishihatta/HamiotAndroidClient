package com.ishihata_tech.hamiot_client.ui.new_account

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ishihata_tech.hamiot_client.R
import com.ishihata_tech.hamiot_client.databinding.NewAccountFragmentBinding
import com.ishihata_tech.hamiot_client.ui.common.ProgressDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NewAccountFragment : Fragment() {
    companion object {
        private const val PROGRESS_DIALOG_TAG = "progressDialog"
    }

    private val viewModel: NewAccountViewModel by viewModels()

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
                    ProgressDialogFragment().showNow(parentFragmentManager, PROGRESS_DIALOG_TAG)
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

        binding.buttonSubmit.setOnClickListener {
            val displayName = binding.editName.text.toString().trim()
            if (displayName.isNotEmpty()) {
                viewModel.createNewAccount(displayName)
            }
        }

        return binding.root
    }
}