package com.ishihata_tech.hamiot_client.ui.history

import android.text.format.DateFormat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ishihata_tech.hamiot_client.R
import com.ishihata_tech.hamiot_client.usecase.GetAssetTransactions
import com.ishihata_tech.hamiot_client.usecase.GetDisplayName
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val getAssetTransactions: GetAssetTransactions,
    private val getDisplayName: GetDisplayName,
) : ViewModel() {
    companion object {
        private val df = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault(Locale.Category.DISPLAY))
    }

    data class Row (
        val time: String,
        val isReceived: Boolean,
        val opponent: String,
        val amount: String,
    )

    private val _list = MutableLiveData<List<Row>>(emptyList())
    val list: LiveData<List<Row>> = _list

    private val _errorMessage = MutableSharedFlow<Int>()
    val errorMessage: SharedFlow<Int> = _errorMessage

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val realList = mutableListOf<Row>()

    var nextPageHash: String? = null

    init {
        loadPage()
    }

    fun loadPage() = viewModelScope.launch {
        if (_isLoading.value == true) return@launch
        _isLoading.value = true

        val result = getAssetTransactions.invoke(nextPageHash)
        if (result == null) {
            _isLoading.value = false
            _errorMessage.emit(R.string.error_network)
            return@launch
        }

        result.transactionList.forEach { transaction ->
            val accountId = transaction.opponentAccountId
            val displayName = getDisplayName.invoke(accountId) ?: accountId
            realList.add(Row(
                DateFormat.format("yyyy/MM/dd kk:mm:ss",
                    Calendar.getInstance().apply { timeInMillis = transaction.time }).toString(),
                transaction.isReceived,
                displayName,
                transaction.amount.toString()
            ))
        }

        nextPageHash = result.nextHash
        _list.value = realList.toList()

        _isLoading.value = false
    }
}