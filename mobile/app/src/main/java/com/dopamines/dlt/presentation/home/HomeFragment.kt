package com.dopamines.dlt.presentation.home

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dopamines.dlt.GlobalApplication
import com.dopamines.dlt.R
import com.dopamines.dlt.databinding.FragmentHomeBinding
import com.dopamines.dlt.util.CustomWebSocketListener
import com.dopamines.dlt.util.GlobalWebSocket
import com.dopamines.dlt.presentation.notification.NotificationViewModel
import com.dopamines.dlt.presentation.auth.SharedPreferences
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import kotlinx.coroutines.launch
import net.daum.mf.map.api.MapView
import java.util.Calendar

class HomeFragment : Fragment(), CustomWebSocketListener {

    // binding & viewModel 설정
    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: HomeViewModel

    // 글로벌 웹소켓
    private lateinit var globalWebSocket: GlobalWebSocket

    // accessToken 받아오기
    val sharedPreferences = context?.let { SharedPreferences(it) }
    val accessToken = sharedPreferences?.accessToken

    // 리사이클러뷰 설정
//    var mapView: MapView? = null
    private lateinit var listItems: ArrayList<HomeData>
    private lateinit var listAdapter: HomeMultiAdapter

    // 날짜 선택 관련 설정
    var selectedYear: String = ""
    var selectedMonth: String = ""
    var selectedDay: String = ""

    // 유저 현재위치 관련 변수 설정
//    private var locationManager : LocationManager? = null
//    private var userNowLocation: Location? = null
//    private var userY: Double? = null
//    private var userX: Double? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)
        // GlobalWebSocket 인스턴스 가져오기
        globalWebSocket = (requireActivity().application as GlobalApplication).globalWebSocket
    }

    override fun onResume() {
        super.onResume()
        // CustomWebSocketListener 등록
        globalWebSocket.addMessageListener(this)

        // 뷰모델 변경될 때마다 listItems값 업데이트
        viewModel.planList.observe(viewLifecycleOwner) {
                planList ->
            if (planList != null) {
                listItems.clear()
                listItems.addAll(planList)
                // 비어있으면 '약속이 없습니다' 표시
                if (listItems.isEmpty()) {
                    binding.llPromiseNone.visibility = View.VISIBLE
                } else {
                    binding.llPromiseNone.visibility = View.GONE
                }
                listAdapter.notifyDataSetChanged()
                Log.i("뷰모델 업데이트", listItems.toString())
            }
        }
    }

    override fun onPause() {
        super.onPause()
        // CustomWebSocketListener 제거
        globalWebSocket.removeMessageListener(this)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_home, container, false
        )

        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        // GlobalApplication에서 GlobalWebSocket 인스턴스 가져오기
        val myApplication = activity?.application as GlobalApplication
        globalWebSocket = myApplication.globalWebSocket

        viewModel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)

        val sharedPreferences = context?.let { SharedPreferences(it) }
        val accessToken = sharedPreferences?.accessToken

        // 리사이클러뷰 설정
        listItems = arrayListOf<HomeData>()
        listAdapter = HomeMultiAdapter(context, listItems, viewModel, globalWebSocket, accessToken)
        binding.rvHomePromiseList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvHomePromiseList.adapter = listAdapter

        // 홈화면 들어오자마자 user정보 viewModel에 저장해주기
        lifecycleScope.launch {
            viewModel.getUserId()
        }

        // 뷰모델 변경될 때마다 listItems값 업데이트
        viewModel.planList.observe(viewLifecycleOwner) {
                planList ->
            if (planList != null) {
                listItems.clear()
                listItems.addAll(planList)
                // 비어있으면 '약속이 없습니다' 표시
                if (listItems.isEmpty()) {
                    binding.llPromiseNone.visibility = View.VISIBLE
                } else {
                    binding.llPromiseNone.visibility = View.GONE
                }
                listAdapter.notifyDataSetChanged()
                Log.i("뷰모델 업데이트", listItems.toString())
            }
        }

        binding.calendarView.apply {
            addDecorator(SaturdayDecorator())
            addDecorator(SundayDecorator())
            setWeekDayLabels(arrayOf("월", "화", "수", "목", "금", "토", "일"))
            topbarVisible = false
        }

        // 달력 Custom
        binding.calendarView.apply {
            setDateTextAppearance(R.style.CustomDateTextAppearance)
            setWeekDayTextAppearance(R.style.CustomWeekDayAppearance)
        }

        // 처음에 선택되어 있는 날짜를 오늘로 지정
        binding.calendarView.selectedDate = CalendarDay.today()
        selectedYear = binding.calendarView.selectedDate!!.year.toString()
        selectedMonth = if (binding.calendarView.selectedDate!!.month < 10) "0${binding.calendarView.selectedDate!!.month}"
        else binding.calendarView.selectedDate!!.month.toString()
        selectedDay = if (binding.calendarView.selectedDate!!.day <10 ) "0${binding.calendarView.selectedDate!!.day}"
        else binding.calendarView.selectedDate!!.day.toString()

        Log.i("현재 선택 날짜", "$selectedYear-$selectedMonth-$selectedDay")
        // 오늘의 약속 목록 Api 요청
        lifecycleScope.launch {
            viewModel.getPromiseList("$selectedYear-$selectedMonth-$selectedDay")
        }

        // tv_home_selected_month 값 변경
        binding.tvHomeSelectedMonth.text = "${selectedYear}.${selectedMonth}"

        // tv_home_selected_date 값 변경
        binding.tvHomeSelectedDate.text = "${selectedYear}.${selectedMonth}.${selectedDay}"

        // 달력의 날짜 선택 시 selected_month, selected_date 변경, 글자 색 white로 변경
        binding.calendarView.setOnDateChangedListener { widget, date, selected ->

            // selectedYear 설정
            selectedYear = date.year.toString()

            // selectedMonth 설정 (10보다 작으면 앞에 0 붙히기)
            if (date.month < 10) selectedMonth = "0${date.month}"
            else selectedMonth = date.month.toString()

            // selectedDay 설정 (10보다 작으면 앞에 0 붙히기)
            if (date.day < 10) selectedDay = "0${date.day}"
            else selectedDay = date.day.toString()

            // tv_home_selected_month 값 변경
            binding.tvHomeSelectedMonth.text = "${selectedYear}.${selectedMonth}"

            // tv_home_selected_date 값 변경
            binding.tvHomeSelectedDate.text = "${selectedYear}.${selectedMonth}.${selectedDay}"

            // 선택된 날짜 글자색 변경
            binding.calendarView.selectionColor

            // 달력 날짜 선택 시 약속 목록 API 요청
            lifecycleScope.launch {
                viewModel.getPromiseList("${selectedYear}-${selectedMonth}-${selectedDay}")
            }
        }

        // 달력 스크롤 시 tv_home_selected_month 값 변경
        binding.calendarView.setOnMonthChangedListener { widget, date ->

            val scrollMonth = if (date.month < 10) "0${date.month}"
            else date.month.toString()
            binding.tvHomeSelectedMonth.text = "${selectedYear}.${scrollMonth}"
            Log.i("달 변경", date.toString())
        }


        // detail로 이동
        listAdapter.setOnItemClickListener(object : HomeMultiAdapter.OnItemClickListener {
            override fun onItemClick(item: HomeData) {
                item.planId?.let { planId ->
                    val bundle = Bundle().apply {
                        putInt("planId", planId)
                    }
                    Log.i("시간차", item.diffHours.toString())
                    Log.i("시간차분", item.diffMinutes.toString())

                        if (item.diffHours >= 2) {
                        findNavController().navigate(
                            R.id.galleryDetailFragment,
                            bundle
                        )
                    } else {
                        findNavController().navigate(
                            R.id.detailPlanFragment,
                            bundle
                        )
                    }
                }
            }
        })



    viewModel.getPlanIdList()
    viewModel.planIdList.observe(viewLifecycleOwner) { planIdList ->
        planIdList?.forEach { topic ->
            Log.i("구독목록", planIdList.toString())

            Firebase.messaging.subscribeToTopic(topic.toString())
                .addOnCompleteListener { task ->
                    var msg = "Subscribed ${task}"
                    if (!task.isSuccessful) {
                        msg = "Subscribe failed"
                    }
                    Log.d("WEATHER", msg)
                }
        }
    }


        // 권한 체크 & 위치정보 업데이트
//        permissionCheck()
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
//        mapView?.onSurfaceDestroyed()
//        mapView = null
        binding.rvHomePromiseList.adapter = null
    }

    override fun onMessageReceived(message: String) {
        Log.i("홈Fragment에서 메시지 수신", message)
    }

    // 위치권한
//    private fun permissionCheck() {
//        if (ContextCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
//            && ContextCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            // 권한이 있을 때
//            Toast.makeText(context, "위치권한이 허용된 상태입니다.", Toast.LENGTH_SHORT).show()
//
//            // 현재위치 받아오기
//            locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
//            userNowLocation = locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
//            // 현재위치 위도 경도 HomeViewModel에 저장해주기
//            userY = userNowLocation?.latitude
//            viewModel.userY.value = userY
//            userX = userNowLocation?.longitude
//            viewModel.userX.value = userX
//
//            // 나중에 주석처리 하거나 삭제하기
//            Log.i("userY", viewModel.userY.value.toString())
//            Log.i("userX", viewModel.userX.value.toString())
//
//        } else {
//            // 권한이 없을 때 권한 요구 팝업
//            ActivityCompat.requestPermissions(
//                requireActivity(),
//                arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,
//                    android.Manifest.permission.ACCESS_FINE_LOCATION,
//                    android.Manifest.permission.ACCESS_BACKGROUND_LOCATION),
//                1)
//        }
//    }
//
//    // 위치권한 요청 결과 처리
//    private val requestPermissionLancher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
//        if (isGranted) {
//            // 권한 허용됐을 때
//            Toast.makeText(context, "위치 권한이 허용되었습니다.", Toast.LENGTH_SHORT).show()
//        } else {
//            // 권한 거부됐을 때
//            Toast.makeText(context, "권한에 동의하지 않을 경우 위치 기반 검색을 사용할 수 없습니다.", Toast.LENGTH_SHORT).show()
//        }
//    }

}


// 달력 Custom을 위한 class
class SaturdayDecorator : DayViewDecorator {

    private val calendar = Calendar.getInstance()

    override fun shouldDecorate(day: CalendarDay): Boolean {
        val year = day.year
        val month = day.month - 1
        val dayOfMonth = day.day
        calendar.set(year, month, dayOfMonth)
        val weekDay: Int = calendar.get(Calendar.DAY_OF_WEEK)
        return weekDay == Calendar.SATURDAY
    }

    override fun decorate(view: DayViewFacade) {
        view.addSpan(ForegroundColorSpan(Color.parseColor("#5B47D5")))
    }
}

class SundayDecorator : DayViewDecorator {

    private val calendar = Calendar.getInstance()

    override fun shouldDecorate(day: CalendarDay): Boolean {
        val year = day.year
        val month = day.month - 1
        val dayOfMonth = day.day
        calendar.set(year, month, dayOfMonth)
        val weekDay: Int = calendar.get(Calendar.DAY_OF_WEEK)
        return weekDay == Calendar.SUNDAY
    }

    override fun decorate(view: DayViewFacade) {
        view.addSpan(ForegroundColorSpan(Color.parseColor("#E25E6D")))
    }
}


