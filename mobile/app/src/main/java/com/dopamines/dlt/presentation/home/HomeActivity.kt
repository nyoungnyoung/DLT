package com.dopamines.dlt.presentation.home

import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.dopamines.dlt.R
import com.dopamines.dlt.databinding.ActivityHomeBinding
import com.dopamines.dlt.presentation.notification.NotificationRepository
import com.dopamines.dlt.presentation.notification.NotificationViewModel

class HomeActivity: AppCompatActivity() {

    private lateinit var binding : ActivityHomeBinding
    private lateinit var viewModel: HomeViewModel

    // 유저 현재위치 관련 변수 설정
    private var locationManager : LocationManager? = null
    private var userNowLocation: Location? = null
    private var userY: Double? = null
    private var userX: Double? = null

    private lateinit var notificationViewModel: NotificationViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        binding.lifecycleOwner = this

        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        initNavigation()

        // 알림 인텐트에서 전달된 값을 가져옴
        val planId = intent.getStringExtra("planId")?.toInt()
        val type = intent.getStringExtra("type")
        when (type) {
            "toDetailPlanFragment" -> {
                val bundle = Bundle().apply {
                    putInt("planId", planId!!)
                }

                val navController = findNavController(R.id.nav_fragment)
                navController.navigate(R.id.detailPlanFragment, bundle)
            }
            "toWalletFragment" -> {

                val navController = findNavController(R.id.nav_fragment)
                navController.navigate(R.id.walletFragment)
            }
            "toEndDetailFragment" -> {
                 val bundle = Bundle().apply {
                     putInt("planId", planId!!)
                     putBoolean("fromIndent", true)
                 }



                val navController = findNavController(R.id.nav_fragment)
                navController.navigate(R.id.galleryDetailFragment, bundle)
            }
        }







        val notificationRepository = NotificationRepository(this)
        notificationViewModel = NotificationViewModel(notificationRepository)



//        val workRequest = OneTimeWorkRequestBuilder<MyWorker>().build()
//        WorkManager.getInstance(this).enqueue(workRequest)
//
//        val worklogRequest = OneTimeWorkRequestBuilder<LogWorker>().build()
//        WorkManager.getInstance(this).enqueue(worklogRequest)




    }

    private fun initNavigation() {
        val navController = findNavController(R.id.nav_fragment)
        binding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    if (navController.currentDestination?.id != R.id.menu_home) {
                        navController.navigate(R.id.menu_home)
                    }
                }
                R.id.menu_gallery -> navController.navigate(R.id.menu_gallery)
                R.id.menu_store -> navController.navigate(R.id.menu_store)
                R.id.menu_my_page -> navController.navigate(R.id.menu_my_page)
            }
            true
        }
        binding.mainFab.setOnClickListener {
            navController.navigate(R.id.main_fab)
            binding.bottomNavigationView.selectedItemId = 0
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            binding.bottomNavigationView.menu.findItem(destination.id)?.isChecked = true
        }

    }

    fun HideBottomNavi(state: Boolean) {
        if (state) binding.bottomNavigationView.visibility = View.GONE else binding.bottomNavigationView.visibility = View.VISIBLE
        if (state) binding.mainFab.visibility = View.GONE else binding.mainFab.visibility = View.VISIBLE
    }
}

