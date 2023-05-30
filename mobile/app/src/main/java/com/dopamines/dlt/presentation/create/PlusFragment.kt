package com.dopamines.dlt.presentation.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintSet.Motion
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dopamines.dlt.R
import com.dopamines.dlt.databinding.FragmentPlusBinding
import com.dopamines.dlt.presentation.create.CreateViewModel
import com.dopamines.dlt.presentation.create.FragmentDialogPeople
import com.dopamines.dlt.presentation.create.KakaoApi
import com.dopamines.dlt.presentation.create.ListAdapter
import com.dopamines.dlt.presentation.create.ListLayout
import com.dopamines.dlt.presentation.create.PeopleAdapter
import com.dopamines.dlt.presentation.create.PeopleSearchAdapter
import com.dopamines.dlt.presentation.create.PeopleSearchResponse
import com.dopamines.dlt.presentation.create.ResultSearchPlace
import kotlinx.coroutines.launch
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PlusFragment : Fragment() {

    // 카카오맵 API 관련 설정
    companion object {
        const val BASE_URL = "https://dapi.kakao.com/"
        const val API_KEY = "KakaoAK b1ad2839ad51d75ceb55ee5ed706608e"
    }

    // view가 생성되었을 때 : 프레그먼트와 레이아웃을 연결시켜주는 부분
    private lateinit var binding: FragmentPlusBinding
    private lateinit var viewModel: CreateViewModel

    // 카카오맵 띄워줄 mapView 변수 설정
    private var mapView: MapView? = null

    // 리사이클러뷰 설정
    private val listItems = arrayListOf<ListLayout>()       // 리사이클러뷰 아이템
    private val listAdapter = ListAdapter(listItems)        // 리사이클러뷰 어댑터
    private val pageNumber = 1                              // 검색 페이지 번호
    private var keyword = ""                                // 검색 키워드
    private var participantsList = arrayListOf<PeopleSearchResponse>()
    private val participantAdapter = PeopleAdapter(participantsList)


    // 현재위치 관련 변수 설정
    private var locationManager : LocationManager? = null
    private var userNowLocation: Location? = null
    private var userY: Double? = null
    private var userX: Double? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        super.onCreateView(inflater, container, savedInstanceState)

        // 네비게이션바 숨겨주기
        val HomeActivity = activity as HomeActivity
        HomeActivity.HideBottomNavi(true)

        // binding & viewModel 정의
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_plus, container, false

        )
        viewModel = ViewModelProvider(requireActivity()).get(CreateViewModel::class.java)

        // 리사이클러뷰 설정
        binding.rvPromisePlaceList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvPromisePlaceList.adapter = listAdapter
        binding.rvPromisePeopleList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvPromisePeopleList.adapter = participantAdapter

        // 뷰모델 상태 확인
        Log.i("현재 뷰모델", "타이틀 ${viewModel.title.value.toString()} 약속날짜 ${viewModel.planDate.value.toString()}" +
                "약속 시간 ${viewModel.planTime.value.toString()} 약속장소 ${viewModel.address.value.toString()} 지각비 ${viewModel.cost
                    .value.toString()} 참여자 ${viewModel.participantInfo.value.toString()}")

        // mapView 초기화
        mapView = MapView(requireContext())
        binding.llPromisePlaceMap.addView(mapView)




        // 뷰모델의 참여자 목록 정보 변경될 때마다 어뎁터에 데이터 변경 알려주기
//        viewModel.participantInfo.observe(viewLifecycleOwner) {
//                participantInfo ->
//            Log.i("생성페이지 내에서 viewModel의 참여자목록 출력", participantInfo.toString())
//            if (participantInfo != null) {
//                participantsList.clear()
//                participantsList.addAll(participantInfo)
//                participantAdapter.notifyDataSetChanged()
//                Log.i("생성페이지 참여자목록 업데이트", participantsList.toString())
//                binding.rvPromisePeopleList.visibility = View.VISIBLE
//            }
//        }

        // dateText가 변하면 약속 일정 editText에 입력되는 값 dateText로 변경
        viewModel.dateText.observe(viewLifecycleOwner) {
                dateText -> binding.etPromiseDate.text = dateText
        }

        // 툴바 제목 설정 및 뒤로가기
        binding.toolbarPromiseCreate.title = "약속 생성"
        binding.toolbarPromiseCreate.btnBack.setOnClickListener{
            findNavController().popBackStack()
        }

        // 약속 title 글자 수 표시
        binding.etPromiseName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val textLenght = s?.length ?: 0
                binding.tvPromiseNameCnt.text = "$textLenght/20"
            }
            override fun afterTextChanged(s: Editable?) {
                val textLenght = s?.length ?: 0
                binding.tvPromiseNameCnt.text = "$textLenght/20"
            }
        })

        // 약속 이름이 입력되었을 때 다음 LinearLayout(약속 일정)이 보이도록 설정
        binding.etPromiseName.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrEmpty()) {
                    binding.llPromiseDate.visibility = View.VISIBLE
                    // 뷰모델 업데이트
                    viewModel.setTitle(binding.etPromiseName.text.toString())
                } else {
                    binding.llPromiseDate.visibility = View.GONE
                    // 뷰모델 업데이트
                    viewModel.setTitle(binding.etPromiseName.text.toString())
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // 약속 이름 입력 후 완료 or 다음 버튼 누르면 focus 해제
        binding.etPromiseName.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                binding.etPromiseName.clearFocus()
                return@setOnEditorActionListener true
            }
            false
        }



        // etPromiseDate 누르면 CreateDateFrament로 이동
        binding.etPromiseDate.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) findNavController().navigate(R.id.createDateFragment)
        }

        // 약속 일정이 입력되었을 때 다음 LinearLayout(약속 장소)가 보이도록 설정
        binding.etPromiseDate.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrEmpty()) {
                    if (binding.tvPromiseNameCnt.text != "0/20")
                    binding.llPromisePlace.visibility = View.VISIBLE
                } else {
                    binding.llPromisePlace.visibility = View.GONE
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // 약속 장소가 입력되었을 때 다음 LinearLayout(지각비)이 보이도록 설정 & 뷰모델 업데이트
        binding.etPromisePlace.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrEmpty()) {
                    binding.llPromiseCost.visibility = View.VISIBLE
                    viewModel.setLocation(binding.etPromisePlace.text.toString())
                } else {
                    binding.llPromiseCost.visibility = View.GONE
                    viewModel.setLocation(binding.etPromisePlace.text.toString())
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // 약속장소 입력후 완료(엔터) 버튼 누를 경우 키워드 검색 함수 실행, 결과의 첫번째 장소로 지도 이동, 리사이클러뷰 visibility 재설정, focus해제
        binding.etPromisePlace.setOnEditorActionListener { v, actionId, event ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                keyword = binding.etPromisePlace.text.toString()
                searchPlace(keyword, pageNumber)
                binding.rvPromisePlaceList.visibility = View.VISIBLE
                binding.etPromisePlace.clearFocus()
            }
            handled
        }

        // 리사이클러뷰 아이템 클릭 시 지도 해당 위치로 이동 & 뷰모델 업데이트 & 서치바에 반영
        listAdapter.setItemClickListener(object: ListAdapter.OnItemClickListener {
            override fun onClick(v: View, position: Int) {
                val mapPoint = MapPoint.mapPointWithGeoCoord(listItems[position].y, listItems[position].x)
                mapView!!.setMapCenterPointAndZoomLevel(mapPoint, 1, true)
                viewModel.setLocation(listItems[position].name)
                viewModel.setAddress(listItems[position].road_address_name)
                viewModel.setLatitude(listItems[position].y)
                viewModel.setLongitude(listItems[position].x)
                binding.etPromisePlace.text = Editable.Factory.getInstance().newEditable(listItems[position].name)
                binding.etPromisePlace.setSelection(binding.etPromisePlace.text.length)
            }
        })

        // 카카오맵 & 스크롤뷰 터치이벤트 조정
        mapView!!.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> binding.svPromiseForm.requestDisallowInterceptTouchEvent(true)
                MotionEvent.ACTION_UP -> binding.svPromiseForm.requestDisallowInterceptTouchEvent(false)
            }
            false
        }

        // 지각비가 입력되었을 때 다음 LinearLayout(참석자)이 보이도록 설정 & 뷰모델 업데이트
        binding.etPromiseCost.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (!s.isNullOrEmpty()) {
                    binding.llPromisePeople.visibility = View.VISIBLE
                    if (binding.etPromiseCost.text.isNotEmpty()) viewModel.setCost(Integer.parseInt(binding.etPromiseCost.text?.toString()))
                } else {
                    binding.llPromisePeople.visibility = View.GONE
                    if (binding.etPromiseCost.text.isNotEmpty()) viewModel.setCost(Integer.parseInt(binding.etPromiseCost.text?.toString()))
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // etPromisePeople 누르면 팝업창 뜨기
        binding.etPromisePeople.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                val dialog = FragmentDialogPeople()
                dialog.show(childFragmentManager, null)
            }
        }

        // checkbox 클릭 시 isSelected값 토글
        binding.llPromisePeopleLater.setOnClickListener {
            binding.ivPromisePeopleLater.isSelected = !binding.ivPromisePeopleLater.isSelected
            binding.tvPromisePeopleLater.isSelected = !binding.tvPromisePeopleLater.isSelected
        }

        // 생성 버튼 클릭 시 생성 API 요청
        binding.btnPromiseCreate.setOnClickListener {
            lifecycleScope.launch {
                viewModel.createPromise(
                    viewModel.title.value!!,
                    viewModel.planDate.value!!,
                    viewModel.planTime.value!!,
                    viewModel.location.value!!,
                    viewModel.address.value!!,
                    viewModel.latitude.value!!,
                    viewModel.longitude.value!!,
                    viewModel.cost.value!!,
                    viewModel.participantIds.value!!)
            }
            viewModel.setTitle("")
            viewModel.setPlanDate("")
            viewModel.setPlanTime("")
            viewModel.setDateText(null)
            viewModel.setLocation("")
            viewModel.setAddress("")
            viewModel.setLatitude(null)
            viewModel.setLongitude(null)
            viewModel.setCost(null)
            viewModel.setParticipantInfo(null)
            viewModel.setParticipantIds("")
            findNavController().navigate(R.id.menu_home)
        }


        permissionCheck()
        return binding.root
    }


    // Dialog를 닫고 돌아왔을 때
    override fun onResume() {
        super.onResume()

        viewModel.participantInfo.observe(viewLifecycleOwner) {
             participantInfo ->
                Log.i("생성페이지 내에서 viewModel의 참여자목록 출력", participantInfo.toString())
                if (participantInfo != null) {
                    binding.llPromisePeopleList.visibility = View.VISIBLE
                    binding.llPromisePeopleLater.visibility = View.GONE
                    binding.tvPromisePeopleCnt.text = "${participantInfo.size}명"
                    participantsList.clear()
                    participantsList.addAll(participantInfo)
                    participantAdapter.notifyDataSetChanged()
                    Log.i("생성페이지 참여자목록 업데이트", participantsList.toString())
                }
            }
    }

    override fun onPause() {
        super.onPause()

        val HomeActivity = activity as HomeActivity
        HomeActivity.HideBottomNavi(false)

    }


    override fun onDestroyView() {
        super.onDestroyView()

        binding.llPromisePlaceMap.removeView(mapView)
        mapView?.onSurfaceDestroyed()

        viewModel.setTitle("")
        viewModel.setPlanDate("")
        viewModel.setPlanTime("")
        viewModel.setDateText(null)
        viewModel.setLocation("")
        viewModel.setAddress("")
        viewModel.setLatitude(null)
        viewModel.setLongitude(null)
        viewModel.setCost(null)
        viewModel.setParticipantInfo(null)
        viewModel.setParticipantIds("")
    }

    // 위치권한
    private fun permissionCheck() {
        if (ContextCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(requireActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // 권한이 있을 때
//            Toast.makeText(context, "위치권한이 허용된 상태입니다.", Toast.LENGTH_SHORT).show()

            // 현재위치 받아오기
            locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
            userNowLocation = locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            // 현재위치 위도 경도
            userY = userNowLocation?.latitude
            userX = userNowLocation?.longitude

        } else {
            // 권한이 없을 때 권한 요구 팝업
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                1)
        }
    }

    // 위치권한 요청 결과 처리
    private val requestPermissionLancher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            // 권한 허용됐을 때
            Toast.makeText(context, "위치 권한이 허용되었습니다.", Toast.LENGTH_SHORT).show()
        } else {
            // 권한 거부됐을 때
            Toast.makeText(context, "권한에 동의하지 않을 경우 위치 기반 검색을 사용할 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    // 키워드 검색 함수
    private fun searchPlace(keyword: String, page: Int) {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(KakaoApi::class.java)
        val call = api.getSearchKeyword(API_KEY, keyword, page, "$userX", "$userY")

        // API 서버에 요청
        call.enqueue(object:Callback<ResultSearchPlace> {
            override fun onResponse(
                call: Call<ResultSearchPlace>,
                response: Response<ResultSearchPlace>
            ) {
                // 통신 성공 -> 검색 결과 리사이클러뷰에 담아주기
                Log.w("통신성공", "통신성공 : ${response.body()}")
                addItemAndMarkers(response.body())
            }

            override fun onFailure(call: Call<ResultSearchPlace>, t: Throwable) {
                // 통신 실패
                Log.w("LocalSearch", "통신 실패: ${t.message}")
            }
        })
    }

    // 검색 결과 리사이클러뷰에 담아줄 함수
    private fun addItemAndMarkers(searchPlace: ResultSearchPlace?) {
        if (!searchPlace?.documents.isNullOrEmpty()) {
            // 검색 결과 있으면
            listItems.clear() // 리스트 초기화 해주고
            mapView?.removeAllPOIItems()  // 지도 마커 지우기

            // 결과 for문으로 리사이클러뷰에 추가
            for ((index, document) in searchPlace!!.documents.withIndex()) {
                // 첫번째 인덱스 구분해주기 위한 변수 설정
                val isFirst = index == 0

                // 검색 결과 리사이클러뷰 아이템에 추가
                val item = ListLayout(document.place_name, document.x.toDouble(), document.y.toDouble(), document.road_address_name)
                listItems.add(item)

                // 지도에 마커 추가
                val point = MapPOIItem()
                point.apply {
                    itemName = document.place_name
                    mapPoint = MapPoint.mapPointWithGeoCoord(document.y.toDouble(), document.x.toDouble())
                    markerType = MapPOIItem.MarkerType.BluePin
                    selectedMarkerType = MapPOIItem.MarkerType.RedPin
                }

                // 첫번째 document인 경우 해당 좌표로 지도 이동 & 마커 selected로 표시
                if (isFirst) {
                    val mapPoint = MapPoint.mapPointWithGeoCoord(document.y.toDouble(), document.x.toDouble())
                    mapView?.setMapCenterPointAndZoomLevel(mapPoint, 1, true)
                }

                mapView?.addPOIItem(point)
            }
            listAdapter.notifyDataSetChanged()
        } else {
            // 검색 결과 없음
            Toast.makeText(context, "검색 결과가 없습니다", Toast.LENGTH_SHORT).show()
        }
    }

    // 마커 이벤트 리스너
    class MarkerEventListener(val context: Context): MapView.POIItemEventListener {
        override fun onPOIItemSelected(p0: MapView?, p1: MapPOIItem?) {
            // 마커 클릭 -> 데이터 바인딩하고 검색창에 반영하기

        }

        override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?) {
            // deprecated
        }

        override fun onCalloutBalloonOfPOIItemTouched(
            p0: MapView?,
            p1: MapPOIItem?,
            p2: MapPOIItem.CalloutBalloonButtonType?
        ) {
            // 말풍선 클릭
        }

        override fun onDraggablePOIItemMoved(p0: MapView?, p1: MapPOIItem?, p2: MapPoint?) {
            // is Draggable = true일 때 마커 이동하는 경우
        }

    }


}