package com.ishihata_tech.hamiot_client.ui.new_account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ishihata_tech.hamiot_client.usecase.CreateNewAccount
import com.ishihata_tech.hamiot_client.usecase.CreateUserKeyPair
import com.ishihata_tech.hamiot_client.usecase.GetUserKeyPair
import com.ishihata_tech.hamiot_client.usecase.StoreUserKeyPair
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import org.spongycastle.util.encoders.Hex
import javax.inject.Inject

@HiltViewModel
class NewAccountViewModel @Inject constructor(
    getUserKeyPair: GetUserKeyPair,
    private val createUserKeyPair: CreateUserKeyPair,
    private val storeUserKeyPair: StoreUserKeyPair,
    private val createNewAccount: CreateNewAccount,
) : ViewModel() {
    enum class Action {
        GO_TO_MAIN_MENU
    }

    // アクション
    private val _action = MutableSharedFlow<Action>()
    val action: SharedFlow<Action> = _action

    // プログレスダイアログの表示状態
    private val _progressDialogShown = MutableLiveData(false)
    val progressDialogShown: LiveData<Boolean> = _progressDialogShown

    // エラーメッセージ
    private val _errorMessage = MutableSharedFlow<String>()
    val errorMessage: SharedFlow<String> = _errorMessage

    init {
        // すでにキーペアが存在する場合はメインメニューに遷移する
        getUserKeyPair.invoke()?.also {
            viewModelScope.launch {
                _action.emit(Action.GO_TO_MAIN_MENU)
            }
        }
    }

    fun createNewAccount(displayName: String) {
        viewModelScope.launch {
            // ぐるぐる表示
            _progressDialogShown.value = true

            // キーペア作成
            val keyPair = createUserKeyPair.invoke()

            // アカウント作成APIを叩く
            if (createNewAccount.invoke(Hex.toHexString(keyPair.public.encoded), displayName)) {
                // キーペアをローカルに保存する
                storeUserKeyPair.invoke(keyPair)

                // メインメニューに遷移する
                _action.emit(Action.GO_TO_MAIN_MENU)
            } else {
                _errorMessage.emit("アカウントの作成に失敗しました")
            }

            // ぐるぐるを消す
            _progressDialogShown.value = false
        }
    }
}