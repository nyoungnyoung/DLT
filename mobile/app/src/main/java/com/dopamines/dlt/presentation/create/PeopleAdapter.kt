package com.dopamines.dlt.presentation.create

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dopamines.dlt.R

class PeopleAdapter(private val participantsList: ArrayList<PeopleSearchResponse>): RecyclerView.Adapter<PeopleAdapter.ViewHolder>() {

    // 기본 함수 override
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PeopleAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_dialog_people, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: PeopleAdapter.ViewHolder, position: Int) {
        holder.bind(participantsList[position])
    }

    override fun getItemCount(): Int {
        return participantsList.size
    }

    // ViewHolder 정의
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        // 리사이클러뷰 내 객체 변수 선언
        private val nickname: TextView = itemView.findViewById(R.id.tv_dialog_people)
        private val profile: ImageView = itemView.findViewById(R.id.iv_dialog_people)

        // 객체에 각각의 item 값 할당
        fun bind(item: PeopleSearchResponse) {
            nickname.text = item.nickname
            Glide.with(itemView).load(item.profile).circleCrop().into(profile)
        }

    }
}