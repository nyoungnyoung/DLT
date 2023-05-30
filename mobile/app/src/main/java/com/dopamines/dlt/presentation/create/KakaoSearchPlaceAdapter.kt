package com.dopamines.dlt.presentation.create

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dopamines.dlt.R
import com.dopamines.dlt.databinding.RecyclerviewSearchBinding

class ListAdapter(val itemList: ArrayList<ListLayout>): RecyclerView.Adapter<ListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_search, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        if (itemList != null) {
            return itemList.size
        }
        return 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = itemList[position].name
        // 아이템 클릭 이벤트
        holder.itemView.setOnClickListener {
            itemClickListener.onClick(it, position)
        }
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.tv_promise_place_list_item)
    }

    interface OnItemClickListener {
        fun onClick(v: View, position: Int)
    }

    fun setItemClickListener(onItemClickListener: OnItemClickListener) {
        this.itemClickListener = onItemClickListener
    }

    private lateinit var itemClickListener : OnItemClickListener
}



//class SearchPlaceAdapter: RecyclerView.Adapter<SearchPlaceAdapter.SearchResultViewHolder>() {
//
//    private var searchResultList: List<SearchPlaceData> = listOf()
//    var currentPage = 1
//    var currentSearchString = ""
//
//    private lateinit var searchResultClickListener: (SearchPlaceData) -> Unit
//
//    // 뷰홀더 정의
//    inner class SearchResultViewHolder(
//        private val binding: RecyclerviewSearchBinding,
//        private val searchResultClickListener: (SearchPlaceData) -> Unit
//    ) : RecyclerView.ViewHolder(binding.root) {
//        fun bindData(data: SearchPlaceData) = with(binding) {
//            tvPromisePlaceListItem.text = data.name
//        }
//        fun bindViews(data: SearchPlaceData) {
//            binding.root.setOnClickListener {
//                searchResultClickListener(data)
//            }
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
//        val binding = RecyclerviewSearchBinding.inflate(
//            LayoutInflater.from(parent.context),
//            parent,
//            false
//        )
//        return SearchResultViewHolder(binding, searchResultClickListener)
//    }
//
//    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
//        holder.bindData(searchResultList[position])
//        holder.bindViews(searchResultList[position])
//    }
//
//    override fun getItemCount(): Int {
//        return searchResultList.size
//    }
//
//    fun setSearchResultList(
//        searchResultList: List<SearchPlaceData>,
//        searchResultClickListener: (SearchPlaceData) -> Unit
//    ) {
//        this.searchResultList = this.searchResultList + searchResultList
//        this.searchResultClickListener = searchResultClickListener
//        notifyDataSetChanged()
//    }
//
//    fun clearList() {
//        searchResultList = listOf()
//    }
//}