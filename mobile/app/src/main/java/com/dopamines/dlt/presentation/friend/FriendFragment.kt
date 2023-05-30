package com.dopamines.dlt.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.dopamines.dlt.R
import com.dopamines.dlt.databinding.FragmentFriendBinding
import com.dopamines.dlt.presentation.friend.FriendRepository
import com.dopamines.dlt.presentation.friend.FriendTypeAdapter
import com.dopamines.dlt.presentation.friend.FriendViewModel
import com.dopamines.dlt.presentation.wallet.WalletRepository
import com.dopamines.dlt.presentation.wallet.WalletViewModel


class FriendFragment : Fragment() {

    private lateinit var binding: FragmentFriendBinding

    private lateinit var friendViewModel: FriendViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_friend, container, false
        )


        // RV adapter
        val friendRepository = FriendRepository(requireContext())
        friendViewModel = FriendViewModel(friendRepository)

        friendViewModel.getFriendData()

        binding.rvMyFriendList.layoutManager = LinearLayoutManager(requireContext())
        friendViewModel.friendData.observe(viewLifecycleOwner) { friendData ->
            binding.rvMyFriendList.adapter = friendData?.let { FriendTypeAdapter(it) }

        }

        return binding.root
    }
}