package com.ishihata_tech.hamiot_client.ui.new_account

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ishihata_tech.hamiot_client.R
import com.ishihata_tech.hamiot_client.repo.UserAccountRepository
import com.ishihata_tech.hamiot_client.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewAccountViewModel @Inject constructor(
    getUserKeyPair: GetUserKeyPair,
    private val createUserKeyPair: CreateUserKeyPair,
    private val storeUserKeyPair: StoreUserKeyPair,
    private val createNewAccount: CreateNewAccount,
    private val getFcmToken: GetFcmToken,
    private val restoreAccount: RestoreAccount,
    private val userAccountRepository: UserAccountRepository,
    private val setIrohaAccountDetail: SetIrohaAccountDetail,
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
    private val _errorMessage = MutableSharedFlow<Int>()
    val errorMessage: SharedFlow<Int> = _errorMessage

    init {
        // すでにキーペアが存在する場合はメインメニューに遷移する
        getUserKeyPair.invoke()?.also {
            viewModelScope.launch {
                _action.emit(Action.GO_TO_MAIN_MENU)
            }
        }
    }

    /**
     * アカウントの作成
     *
     * @param displayName 表示名
     */
    fun createNewAccount(displayName: String) = viewModelScope.launch {
        // ぐるぐる表示
        _progressDialogShown.value = true

        // FCMトークン取得
        getFcmToken.invoke()?.also { fcmToken ->

            // キーペア作成
            val keyPair = createUserKeyPair.invoke()
            // キーペアをローカルに保存する
            storeUserKeyPair.invoke(keyPair)

            // アカウント作成APIを叩く
            if (createNewAccount.invoke(
                displayName,
                fcmToken
            )) {
                userAccountRepository.displayName = displayName

                // メインメニューに遷移する
                _action.emit(Action.GO_TO_MAIN_MENU)
            } else {
                _errorMessage.emit(R.string.error_create_account)
            }
        }

        // ぐるぐるを消す
        _progressDialogShown.value = false
    }

    /**
     * アカウントのリストア
     *
     * @param uri 読み込み先
     * @return 成功したらtrue, 失敗したらfalse
     */
    fun restoreAccount(uri: Uri) = viewModelScope.launch {
        if (!restoreAccount.invoke(uri)) {
            _errorMessage.emit(R.string.error_restore_account)
            return@launch
        }

        // ぐるぐる表示
        _progressDialogShown.value = true

        // FCMトークン取得
        val fcmToken = getFcmToken.invoke()
        if (fcmToken != null) {
            // FCMトークンをIrohaノードに送る
            if (setIrohaAccountDetail.invoke("fcmToken", fcmToken)) {
                // 成功！
                // メインメニューに遷移する
                _action.emit(Action.GO_TO_MAIN_MENU)
            } else {
                // 失敗！
                userAccountRepository.clear()
                _errorMessage.emit(R.string.error_network)
            }
        }

        // ぐるぐるを消す
        _progressDialogShown.value = false
    }
}