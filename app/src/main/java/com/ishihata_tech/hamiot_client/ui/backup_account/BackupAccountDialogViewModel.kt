package com.ishihata_tech.hamiot_client.ui.backup_account

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ishihata_tech.hamiot_client.usecase.BackupAccount
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BackupAccountDialogViewModel @Inject constructor(
    private val backupAccount: BackupAccount,
) : ViewModel() {
    val password = MutableLiveData("")
    val passwordConfirm = MutableLiveData("")

    /**
     * アカウントデータを保存する
     */
    fun backupAccount(uri: Uri): Boolean {
        return password.value?.let { backupAccount.invoke(uri, it) } ?: false
    }
}