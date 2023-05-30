package com.dopamines.dlt.presentation.create

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.ToggleButton
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dopamines.dlt.R

class PeopleSearchAdapter(private val itemList: ArrayList<PeopleSearchResponse>, private val participantsList: ArrayList<PeopleSearchResponse>): RecyclerView.Adapter<PeopleSearchAdapter.ViewHolder>() {

    private lateinit var itemClickListener: OnItemClickListener
    private lateinit var participantCheckedChangeListener: OnParticipantCheckedChangeListener


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_dialog_search, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(itemList[position])
        // 아이템 클릭 이벤트
        holder.itemView.setOnClickListener {
            itemClickListener.onClick(it, position)
        }
        holder.btn.setOnCheckedChangeListener(null)
        holder.btn.isChecked = participantsList.contains(itemList[position])
        holder.btn.setOnCheckedChangeListener { buttonView, isChecked ->
            participantCheckedChangeListener.onChekedChanged(itemList[position], isChecked)
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val nickname: TextView = itemView.findViewById(R.id.tv_dialog_search_people)
        private val profile: ImageView = itemView.findViewById(R.id.iv_dialog_search_people)

        // 리사이클러 뷰 내 토글버튼
        val btn: ToggleButton = view.findViewById(R.id.tb_dialog_search_people)

        fun bind(item: PeopleSearchResponse) {
            nickname.text = item.nickname
            Glide.with(itemView).load(item.profile).circleCrop().into(profile)
        }
    }

    interface OnItemClickListener {
        fun onClick(v: View, position: Int)
    }

    interface OnParticipantCheckedChangeListener {
        fun onChekedChanged( ItemList: PeopleSearchResponse, isChecked: Boolean)
    }

    fun setItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }

    fun setParticipantCheckedChangeListener(listener: OnParticipantCheckedChangeListener) {
        participantCheckedChangeListener = listener
    }

}