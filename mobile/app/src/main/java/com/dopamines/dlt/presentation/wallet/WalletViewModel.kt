package com.dopamines.dlt.presentation.wallet

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.lang.Exception

class WalletViewModel(private val repository: WalletRepository) : ViewModel() {


    private val _walletData = MutableLiveData<WalletData?>()
    val walletData: MutableLiveData<WalletData?> = _walletData

    private val _chargeResult = MutableLiveData<Boolean>()
    val chargeResult: LiveData<Boolean>
        get() = _chargeResult


    private val _withDrawResult = MutableLiveData<Boolean>()
    val withDrawResult: LiveData<Boolean>
        get() = _withDrawResult


    fun getWalletData() {
        viewModelScope.launch {
            try {
                val response = repository.getWalletData()
                Log.i("RESPONSE IN WALLET", "${response.body()}")
                if (response.isSuccessful) {
                    val walletData = response.body()
                    _walletData.value = walletData
                }

            } catch (e: Exception) {
                Log.i("RESPONSE IN WALLET", e.toString())
            }
        }
    }

    suspend fun sendChargeData(
        money: Int,
        method: String,
        transactionDate: String,
        transactionTime: String,
        transactionReceipt: String
    ) {
        viewModelScope.launch {
            val response = repository.sendChargeData(
                money,
                method,
                transactionDate,
                transactionTime,
                transactionReceipt
            )
            Log.i("RESPONSE IN CHARGE", "${response.body()}")
            try {

                if (response.isSuccessful) {
                    val chargeData = response.body()
                    _walletData.value = chargeData
                    _chargeResult.value = true

                }
            } catch (e: Exception) {
                Log.e("RESPONSE IN CHARGE", e.toString())
            }
        }

    }

    fun resetChargeResult() {
        _chargeResult.value = false
    }


    suspend fun sendWithdrawData(money:Int){
        viewModelScope.launch {
            val response = repository.sendWithdrawData(money)
            Log.i("RESPONSE IN WITHDRAW", "${response.body()}")
            try {

                if(response.isSuccessful) {
                    val withDrawData = response.body()
                    _walletData.value = withDrawData
                    _withDrawResult.value  = true
                }
            } catch (e:Exception){
                Log.e("RESPONSE IN WITHDRAW", e.toString())
            }
        }
    }

    fun resetWithDrawResult() {
        _withDrawResult.value = false
    }


}