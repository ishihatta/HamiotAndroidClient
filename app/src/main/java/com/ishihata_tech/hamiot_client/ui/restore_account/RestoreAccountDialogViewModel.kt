package com.ishihata_tech.hamiot_client.ui.restore_account

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RestoreAccountDialogViewModel @Inject constructor() : ViewModel() {
    val password = MutableLiveData("")
}