package com.ishihata_tech.hamiot_client.ui.transfer_asset

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ishihata_tech.hamiot_client.R
import com.ishihata_tech.hamiot_client.usecase.TransferAssetOnServer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TransferAssetViewModel @Inject constructor(
    private val transferAsset: TransferAssetOnServer,
) : ViewModel() {
    enum class Action {
        GO_BACK,
        DISPLAY_SUCCESS,
    }

    // アクション
    private val _action = MutableSharedFlow<Action>()
    val action: SharedFlow<Action> = _action

    // エラーメッセージ
    private val _errorMessage = MutableSharedFlow<Int>()
    val errorMessage: SharedFlow<Int> = _errorMessage

    // プログレスダイアログの表示状態
    private val _progressDialogShown = MutableLiveData(false)
    val progressDialogShown: LiveData<Boolean> = _progressDialogShown

    /**
     * 送金ボタンが押された
     *
     * @param opponentAccountId 送金先のアカウントID
     * @param amount 送金額
     */
    fun onTransferButton(opponentAccountId: String, amount: String) = viewModelScope.launch {
        // 送金額をパースする
        val amountInt = try {
            amount.toInt()
        } catch (e: Exception) {
            _errorMessage.emit(R.string.error_invalid_amount)
            return@launch
        }

        // 金額のバリデーション
        if (amountInt <= 0) {
            _errorMessage.emit(R.string.error_invalid_amount)
            return@launch
        }

        // ぐるぐる表示
        _progressDialogShown.value = true

        // 送金する
        val transferResult = transferAsset.invoke(opponentAccountId, amountInt)

        // ぐるぐるを消す
        _progressDialogShown.value = false

        // 成功したら前の画面に戻る
        if (transferResult) {
            _action.emit(Action.DISPLAY_SUCCESS)
        } else {
            // 失敗
            _errorMessage.emit(R.string.error_transfer)
        }
    }
}