package com.ishihata_tech.hamiot_client.ui.main_menu

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ishihata_tech.hamiot_client.R
import com.ishihata_tech.hamiot_client.repo.UserAccountRepository
import com.ishihata_tech.hamiot_client.usecase.GetBalance
import com.ishihata_tech.hamiot_client.usecase.GetDisplayName
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class MainMenuViewModel @Inject constructor(
        private val getBalance: GetBalance,
        private val getDisplayName: GetDisplayName,
        private val userAccountRepository: UserAccountRepository,
) : ViewModel() {
    companion object {
        private const val TAG = "MainMenuViewModel"
    }

    // 残高
    private val _balance = MutableLiveData(0)
    val balance: LiveData<Int> = _balance

    // プログレスダイアログの表示状態
    private val _progressDialogShown = MutableLiveData(false)
    val progressDialogShown: LiveData<Boolean> = _progressDialogShown

    // エラー出力
    private val _errorMessage = MutableSharedFlow<Int>()
    val errorMessage: SharedFlow<Int> = _errorMessage

    // 表示名
    private val _displayName = MutableLiveData("")
    val displayName: LiveData<String> = _displayName

    // 送金先のアカウントIDと表示名
    private val _transferAccountIdAndDisplayName = MutableSharedFlow<Pair<String, String>>()
    val transferAccountIdAndDisplayName: SharedFlow<Pair<String, String>> = _transferAccountIdAndDisplayName

    /**
     * QRコードにする内容
     */
    val qrCodeContent: String
        get() {
            val json = JSONObject().apply {
                put("accountId", userAccountRepository.accountId)
            }
            return json.toString()
        }

    init {
        _displayName.value = userAccountRepository.displayName
    }

    /**
     * Irohaノードから残高を取得して画面に反映させる
     */
    fun refreshBalance() = viewModelScope.launch {
        val irohaBalance = getBalance.invoke()
        if (irohaBalance != null) {
            _balance.value = irohaBalance
        } else {
            _errorMessage.emit(R.string.fail_get_balance)
        }
    }

    /**
     * QRコードをスキャンしたら呼ばれる
     */
    fun onScanQr(contents: String) = viewModelScope.launch {
        Log.d(TAG, "onScanQr:")

        // accountIdの取得
        val json = try {
            JSONObject(contents)
        } catch (e: Exception) {
            _errorMessage.emit(R.string.unsupported_qr_code)
            return@launch
        }
        val accountId = json.optString("accountId", "")
        if (accountId.isEmpty()) {
            _errorMessage.emit(R.string.unsupported_qr_code)
            return@launch
        }

        // ぐるぐる表示
        _progressDialogShown.value = true

        // 相手の表示名を取得する
        val opponentDisplayName = getDisplayName.invoke(accountId)

        // ぐるぐるを消す
        _progressDialogShown.value = false

        // エラーチェック
        if (opponentDisplayName == null) {
            _errorMessage.emit(R.string.error_network)
            return@launch
        }

        // 送金画面に遷移する
        _transferAccountIdAndDisplayName.emit(Pair(accountId, opponentDisplayName))
    }
}