package com.ishihata_tech.hamiot_client.ui.main_menu

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import com.google.zxing.BarcodeFormat
import com.google.zxing.integration.android.IntentIntegrator
import com.ishihata_tech.hamiot_client.Constants
import com.ishihata_tech.hamiot_client.R
import com.ishihata_tech.hamiot_client.databinding.MainMenuFragmentBinding
import com.ishihata_tech.hamiot_client.ui.backup_account.BackupAccountDialogFragment
import com.ishihata_tech.hamiot_client.ui.common.ProgressDialogFragment
import com.journeyapps.barcodescanner.BarcodeEncoder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainMenuFragment : Fragment(), LogoutDialogFragment.Listener {
    companion object {
        private const val TAG = "MainMenuFragment"
        private const val PROGRESS_DIALOG_TAG = "progressDialog"
        private const val LOGOUT_DIALOG_TAG = "logoutDialog"
        private const val BACKUP_DIALOG_TAG = "backupDialog"
    }

    /**
     * 残高が変わったことを検知するためのレシーバ
     */
    inner class RefreshBalanceBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            viewModel.refreshBalance()
        }
    }

    private val viewModel: MainMenuViewModel by viewModels()
    private val localBroadcastManager by lazy { LocalBroadcastManager.getInstance(requireContext()) }
    private val refreshBalanceBroadcastReceiver = RefreshBalanceBroadcastReceiver()

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

        // アクションの監視
        lifecycleScope.launch {
            viewModel.action.collect {
                when (it) {
                    MainMenuViewModel.Action.BACK_TO_ROOT -> {
                        findNavController().apply {
                            popBackStack()
                            navigate(R.id.newAccountFragment)
                        }
                    }
                }
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

        // ツールバー
        (requireActivity() as AppCompatActivity).setSupportActionBar(binding.toolbar)
        setHasOptionsMenu(true)

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

        // 造幣コマンドの有無の監視
        viewModel.showMakeMoneyCommand.observe(viewLifecycleOwner) {
            requireActivity().invalidateOptionsMenu()
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshBalance()

        // 残高の変化を検知する
        localBroadcastManager.registerReceiver(
                refreshBalanceBroadcastReceiver,
                IntentFilter().apply {
                    addAction(Constants.ACTION_REFRESH_BALANCE)
                }
        )
    }

    override fun onPause() {
        localBroadcastManager.unregisterReceiver(refreshBalanceBroadcastReceiver)
        super.onPause()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        result?.contents?.also { contents ->
            viewModel.onScanQr(contents)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        Log.d(TAG, "onCreateOptionsMenu")
        inflater.inflate(R.menu.main_menu, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.item_make_money).isVisible = viewModel.showMakeMoneyCommand.value == true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_make_money -> {
                findNavController().navigate(
                    MainMenuFragmentDirections.actionMainMenuFragmentToAddAssetFragment())
                true
            }
            R.id.item_history -> {
                findNavController().navigate(
                    MainMenuFragmentDirections.actionMainMenuFragmentToHistoryFragment())
                true
            }
            R.id.item_backup -> {
                BackupAccountDialogFragment().showNow(childFragmentManager, BACKUP_DIALOG_TAG)
                true
            }
            R.id.item_logout -> {
                Log.d(TAG, "Logout")
                LogoutDialogFragment().showNow(childFragmentManager, LOGOUT_DIALOG_TAG)
                true
            }
            else -> false
        }
    }

    // ログアウトダイアログのOKボタンが押されたら呼ばれる
    override fun onDoLogout() {
        viewModel.logout()
    }
}