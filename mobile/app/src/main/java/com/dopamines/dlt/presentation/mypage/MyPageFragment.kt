package com.dopamines.dlt.presentation.home

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.dopamines.dlt.R
import com.dopamines.dlt.databinding.FragmentMyPageBinding
import com.dopamines.dlt.presentation.auth.SharedPreferences
import com.dopamines.dlt.presentation.gallery.GalleryRepository
import com.dopamines.dlt.presentation.gallery.GalleryViewModel
import com.dopamines.dlt.presentation.mypage.MyPageRepository
import com.dopamines.dlt.presentation.mypage.MyPageViewModel
import com.unity3d.player.UnityPlayerActivity
import kotlinx.coroutines.launch
import java.lang.Math.abs
import java.util.Arrays

class MyPageFragment : Fragment() {

    private lateinit var binding: FragmentMyPageBinding

    // view가 생성되었을 때 : 프레그먼트와 레이아웃을 연결시켜주는 부분
    private lateinit var myPageViewModel: MyPageViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)


        val myPageRepository = MyPageRepository(requireContext())
        myPageViewModel = MyPageViewModel(myPageRepository)

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_my_page, container, false
        )


        binding.clUserInfo.setOnClickListener {
            findNavController().navigate(R.id.myPageUserInfoFragment)
        }
        binding.clPlanLog.setOnClickListener {
            findNavController().navigate(R.id.myPagePlanLogFragment)
        }
        binding.clCostLog.setOnClickListener {
            findNavController().navigate(R.id.myPageCostLogFragment)
        }
        binding.clCharacterInfo.setOnClickListener {
            val sharedPreferences = context?.let { SharedPreferences(it) }
            val accessToken = sharedPreferences?.accessToken
            Log.i("ACCESSTOKEN", "${accessToken}")

            val intent = Intent(context, UnityPlayerActivity::class.java)
            intent.putExtra("unity", "unity")
            intent.putExtra("scene", "1")
            intent.putExtra("accessToken", accessToken)

            startActivity(intent)
        }



        lifecycleScope.launch {
            myPageViewModel.getMyInfo()

        }



        myPageViewModel.myInfoData.observe(viewLifecycleOwner) { myData ->

            val drawableResId: Int = R.drawable.card_rounded_detail // drawable 리소스 ID
            binding.tvStatus.setBackgroundResource(drawableResId)


            if (myData.averageArrivalTime > 0) {
                binding.tvStatus.text = "LATER"
                binding.tvStatus.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.red1))
            } else if (myData.averageArrivalTime < 0) {
                binding.tvStatus.text = "WAITER"
                binding.tvStatus.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.violet1))
            } else {
                binding.tvStatus.text = "NEW"
                binding.tvStatus.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.orange1))
            }


                binding.tvProfileNickname.text = myData.nickname
            binding.tvLateRateContent.text = myData.latenessRate.toString() + "%"

            if (myData.averageArrivalTime > 0) {
                binding.tvAverageArrivalTimeContent.text =
                    abs(myData.averageArrivalTime).toString() + "분 후"
            } else {
                binding.tvAverageArrivalTimeContent.text =
                    abs(myData.averageArrivalTime).toString() + "분 전"
            }

            if (myData.totalCost > 0) {
                binding.tvLateCostTitle.text = "잃은 지각비"


            } else {
                binding.tvLateCostTitle.text = "획득한 지각비"
            }
            binding.tvLateCostContent.text = abs(myData.totalCost).toString() + "원"


            Glide.with(requireContext())
                .load(myData.profile)
                .centerCrop()
                .circleCrop()
                .into(binding.ivProfileImage)

        }

        return binding.root
    }

}