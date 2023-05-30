package com.dopamines.dlt.presentation.gallery

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.dopamines.dlt.R
import com.dopamines.dlt.databinding.FragmentGalleryDetailBinding
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


class GalleryDetailFragment : Fragment() {

    private lateinit var binding: FragmentGalleryDetailBinding
    private var planId: Int? = null
    private lateinit var galleryDetailCompleteViewModel: GalleryDetailCompleteViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGalleryDetailBinding.inflate(inflater, container, false)

        // 전달확인 메서드
        arguments?.let { bundle ->
            planId = bundle.getInt("planId", -1)
        }
        Log.i("PLANID", planId.toString())

        val pagerAdapter = planId?.let { PagerAdapter(childFragmentManager, lifecycle, it) }
        pagerAdapter?.addFragment(GalleryDetailReviewFragment(), "후기")
        pagerAdapter?.addFragment(GalleryDetailCompleteFragment(), "상세")

        // Set the ViewPager2 adapter
        binding.viewPager.adapter = pagerAdapter

        // Connect the TabLayout and ViewPager2
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            if (pagerAdapter != null) {
                tab.text = pagerAdapter.titles[position]
            }
        }.attach()


        val galleryRepository = GalleryRepository(requireActivity())
        galleryDetailCompleteViewModel = GalleryDetailCompleteViewModel(galleryRepository)


        planId?.let{galleryDetailCompleteViewModel.getPlanEndDetailData(it)}

        galleryDetailCompleteViewModel.planEndDetailData.observe(viewLifecycleOwner) { detailData->
            binding.signUpToolbar.title = detailData?.title.toString()
        }


        // Return the root view
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fromIndent = arguments?.getBoolean("fromIndent", false) ?: false

            // Set the initial page of the ViewPager2
            if (fromIndent) {
                binding.viewPager.setCurrentItem(1, false) // Complete Fragment
            } else {
                binding.viewPager.setCurrentItem(0, false) // Review Fragment
            }

        binding.signUpToolbar.btnBack.setOnClickListener {
            val navController = findNavController()
            navController.popBackStack() // 이전까지의 모든 Fragment 제거
            navController.navigate(R.id.menu_gallery)
        }
    }
}



class PagerAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle, private val planId: Int) :
    FragmentStateAdapter(fragmentManager, lifecycle) {

    private val fragments = mutableListOf<Fragment>()
    val titles = mutableListOf<String>()

    fun addFragment(fragment: Fragment, title: String) {
        fragments.add(fragment)
        titles.add(title)
    }

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> {
                val fragment = GalleryDetailReviewFragment()
                val bundle = Bundle().apply {
                    putInt("planId", planId)
                }
                fragment.arguments = bundle
                fragment
            }
            1 -> {
                val fragment = GalleryDetailCompleteFragment()
                val bundle = Bundle().apply {
                    putInt("planId", planId)
                }
                fragment.arguments = bundle
                fragment
            }
            else -> throw IllegalArgumentException("Invalid position $position")
        }
    }
}



