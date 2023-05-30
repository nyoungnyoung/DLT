package com.dopamines.dlt.presentation.wallet

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dopamines.dlt.R
import java.text.NumberFormat
import java.time.LocalDate
import java.util.Locale

class GroupedAmountAdapter(
    private val walletData: WalletData,

    ) :
    RecyclerView.Adapter<GroupedAmountAdapter.GroupViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_amount_date, parent, false)
        return GroupViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val sortedKeys = walletData.details.keys.sortedByDescending { LocalDate.parse(it) } // 키를 최신순으로 정렬
        val key = sortedKeys[position]
        val transactionDataList = walletData.details.values.toList()[position]
        holder.bind(key, transactionDataList)
    }

    override fun getItemCount(): Int = walletData.details.size



    inner class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dateTextView: TextView = itemView.findViewById(R.id.tv_amount_group_date)
        private val transactionRecyclerView: RecyclerView =
            itemView.findViewById(R.id.rv_amount_list)


        fun bind(key: String, transactionDataList: List<TransactionData>) {
            dateTextView.text = key.substring(5).replace("-", ".")
            transactionRecyclerView.adapter = TransactionAdapter(
                transactionDataList
            )
            transactionRecyclerView.layoutManager = LinearLayoutManager(itemView.context)
        }

    }
}


class TransactionAdapter(
    private val transactionDataList: List<TransactionData>,

    ) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_amount_default, parent, false)
        return TransactionViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val sortedTransactionDataList = transactionDataList.sortedByDescending { it.transactionTime }
        val transactionData = sortedTransactionDataList[position]

        holder.bind(transactionData)
    }

    override fun getItemCount(): Int = transactionDataList.size

    inner class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val moneyTextView: TextView = itemView.findViewById(R.id.tv_amount_cost)
        private val titleTextView: TextView = itemView.findViewById(R.id.tv_amount_title)
        private val timeTextView: TextView = itemView.findViewById(R.id.tv_amount_time)
        private val totalTextView: TextView = itemView.findViewById(R.id.tv_cm_cost)

        @SuppressLint("ResourceAsColor")
        fun bind(transactionData: TransactionData) {
            val formattedMoney = NumberFormat.getNumberInstance(Locale.getDefault()).format(transactionData.money)
            moneyTextView.text = formattedMoney

            val formattedTotal = NumberFormat.getNumberInstance(Locale.getDefault()).format(transactionData.total)
            totalTextView.text = formattedTotal

            titleTextView.text = transactionData.title
            timeTextView.text = transactionData.transactionTime.substring(0,5)


            val colorType = when (transactionData.type) {
                // 충전, 출금, 얻은지각비, 잃은지각비 순
                0 -> R.color.orange1
                1 -> R.color.orange2
                2 -> R.color.violet1
                3 -> R.color.red1
                else -> android.R.color.black
            }

            val textColor = ContextCompat.getColor(itemView.context, colorType)
            moneyTextView.setTextColor(textColor)

            itemView.setOnClickListener {

            }
        }
    }
}