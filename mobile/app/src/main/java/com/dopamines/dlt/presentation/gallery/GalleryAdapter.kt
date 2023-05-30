package com.dopamines.dlt.presentation.gallery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dopamines.dlt.R
import com.dopamines.dlt.presentation.detail.Participant

class payParticipantAdapter(private val participantList: List<PayParticipant>) : RecyclerView.Adapter<payParticipantAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nicknameTextView: TextView = itemView.findViewById(R.id.tv_nickname)
        val amountTextView: TextView = itemView.findViewById(R.id.tv_amount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_dialog_participant, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val participant = participantList[position]
        holder.nicknameTextView.text = "${participant.nickName}"
        holder.amountTextView.text = String.format("%,d원", participant.paymentAmount)
    }

    override fun getItemCount(): Int {
        return participantList.size
    }
}
