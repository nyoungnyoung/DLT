package com.dopamines.dlt.presentation.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dopamines.dlt.databinding.RecyclerviewHomePeopleProfileBinding

class HomeparticipantAdapter(var participantList: ArrayList<participantInfo>): RecyclerView.Adapter<HomeparticipantAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = RecyclerviewHomePeopleProfileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(participantList[position])
    }

    override fun getItemCount(): Int = participantList.size

    inner class ViewHolder(private val binding: RecyclerviewHomePeopleProfileBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: participantInfo) {
            Glide.with(binding.root).load(item.profile).circleCrop().into(binding.ivHomePeopleProfile)
        }
    }
}
