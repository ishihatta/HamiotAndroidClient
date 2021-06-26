package com.ishihata_tech.hamiot_client.ui.add_asset

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ishihata_tech.hamiot_client.R
import com.ishihata_tech.hamiot_client.usecase.AddAssetQuantity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddAssetViewModel @Inject constructor(
    private val addAssetQuantity: AddAssetQuantity,
) : ViewModel() {
    enum class Action {
        GO_BACK,
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
     * 実行ボタンが押された
     *
     * @param amount 額
     */
    fun onAddAssetButton(amount: String) = viewModelScope.launch {
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

        // 実行する
        val result = addAssetQuantity.invoke(amountInt)

        // ぐるぐるを消す
        _progressDialogShown.value = false

        // 成功したら前の画面に戻る
        if (result) {
            _action.emit(Action.GO_BACK)
        } else {
            // 失敗
            _errorMessage.emit(R.string.error_add_asset)
        }
    }
}