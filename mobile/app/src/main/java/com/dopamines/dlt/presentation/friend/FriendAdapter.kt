package com.dopamines.dlt.presentation.friend

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dopamines.dlt.R
import com.dopamines.dlt.presentation.wallet.TransactionData
import com.dopamines.dlt.presentation.wallet.WalletData
import java.text.NumberFormat
import java.time.LocalDate
import java.util.Locale

class FriendTypeAdapter(
    private val friendTypeData: FriendTypeData

    ) :
    RecyclerView.Adapter<FriendTypeAdapter.GroupViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_friend_type, parent, false)
        return GroupViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        val Keys = listOf("friends", "waitings")
        val key = Keys[position]
        val dataList = when (key) {
            "friends" -> friendTypeData.friends ?: emptyList()
            "waitings" -> friendTypeData.waitings ?: emptyList()
            else -> emptyList()
        }
        holder.bind(key, dataList)
    }

    override fun getItemCount(): Int {
        return (friendTypeData.friends?.size ?: 0) + (friendTypeData.waitings?.size ?: 0)
    }




    inner class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val typeTextView: TextView = itemView.findViewById(R.id.tv_friend_type)
        private val friendItemRecyclerView: RecyclerView =
            itemView.findViewById(R.id.rv_my_friend_list_item)


        fun bind(key: String, FriendItemDataList: List<FriendItemData>) {
            typeTextView.text = key
            friendItemRecyclerView.adapter = FriendAdapter(
                FriendItemDataList
            )
            friendItemRecyclerView.layoutManager = LinearLayoutManager(itemView.context)
        }

    }
}




class FriendAdapter(
    private val friendItemDataList: List<FriendItemData>,

    ) : RecyclerView.Adapter<FriendAdapter.FriendItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendItemViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_friend_default, parent, false)
        return FriendItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FriendItemViewHolder, position: Int) {

        val transactionData = friendItemDataList[position]

        holder.bind(transactionData)
    }

    override fun getItemCount(): Int = friendItemDataList.size

    inner class FriendItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val profileImageView: ImageView = itemView.findViewById(R.id.iv_friend_profile)
        private val nicknameTextView: TextView = itemView.findViewById(R.id.tv_friend_nickname)
        private val messageTextView: TextView = itemView.findViewById(R.id.tv_friend_message)


        fun bind(friendItemData: FriendItemData) {
            nicknameTextView.text = friendItemData.nickname.toString()
            messageTextView.text = friendItemData.profileMessage

            Glide.with(itemView)
                .load(friendItemData.profile) // 이미지의 URL 또는 파일 경로를 설정합니다.
                .apply(RequestOptions.circleCropTransform()) // CircleCrop 적용
                .placeholder(R.drawable.test_image) // 로드되기 전까지 보여줄 기본 이미지 설정
                .error(R.drawable.cha_login) // 로드 중 오류 발생 시 보여줄 기본 이미지 설정
                .into(profileImageView)

            itemView.setOnClickListener {

            }
        }
    }
}