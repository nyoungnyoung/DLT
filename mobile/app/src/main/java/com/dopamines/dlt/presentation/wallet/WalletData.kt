                                                                                 package com.dopamines.dlt.presentation.wallet

import com.google.gson.annotations.SerializedName

data class WalletData(
    @SerializedName("details")
    val details: Map<String, List<TransactionData>>,
    @SerializedName("total")
    val total: Int
)
data class TransactionData(
    @SerializedName("total")
    val total: Int,
    @SerializedName("money")
    val money: Int,
    @SerializedName("title")
    val title: String,
    @SerializedName("transactionTime")
    val transactionTime: String,
    @SerializedName("type")
    val type: Int,
    @SerializedName("receipt")
    val receipt: String,
)


// Charge 관련
//data class ChargeData(
//    @SerializedName("money")
//    val money: Int,
//    @SerializedName("method")
//    val transactionMethod: String,
//    @SerializedName("transactionDate")
//    val date: String,
//    @SerializedName("transactionTime")
//    val time: String,
//    @SerializedName("receipt")
//    val transactionReceipt: String
//)

data class JsonResponseData(
    val event: String,
    val data: JsonData,
    val bootpay_event: Boolean
)

data class JsonData(
    val receipt_id: String,
    val order_id: String,
    val price: Int,
    val method_origin: String,
    val purchased_at: String,
    val receipt_url: String
)