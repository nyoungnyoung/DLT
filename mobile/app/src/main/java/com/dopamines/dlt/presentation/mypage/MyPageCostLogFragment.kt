package com.dopamines.dlt.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.dopamines.dlt.R
import com.dopamines.dlt.databinding.FragmentMyPageBinding
import com.dopamines.dlt.databinding.FragmentMyPageCostLogBinding

class MyPageCostLogFragment : Fragment() {

    private lateinit var binding: FragmentMyPageCostLogBinding
    // view가 생성되었을 때 : 프레그먼트와 레이아웃을 연결시켜주는 부분

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_my_page_cost_log, container, false
        )
        return binding.root
    }
}