//package com.dopamines.dlt.presentation.home
//
//import android.app.DatePickerDialog
//import android.app.TimePickerDialog
//import android.icu.util.Calendar
//import android.os.Bundle
//import android.text.Editable
//import android.text.TextWatcher
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.databinding.DataBindingUtil
//import androidx.fragment.app.Fragment
//import androidx.recyclerview.widget.LinearLayoutManager
//import com.dopamines.dlt.R
//import com.dopamines.dlt.databinding.FragmentPlusBinding
////import com.dopamines.dlt.presentation.create.TmpViewModel
//import com.dopamines.dlt.presentation.create.KakaoApi
//import com.dopamines.dlt.presentation.create.ListAdapter
//import com.dopamines.dlt.presentation.create.ListLayout
////import com.dopamines.dlt.presentation.create.SearchPlaceResponse
//import net.daum.mf.map.api.MapPoint
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//import java.text.SimpleDateFormat
//import java.util.Locale
//
//// 나중에 tmp만 PlusFragment로 바꾸면 됨!
//class tmp : Fragment() {
//
//    // view가 생성되었을 때 : 프레그먼트와 레이아웃을 연결시켜주는 부분
//    private lateinit var binding: FragmentPlusBinding
//    private lateinit var viewModel: TmpViewModel
//
//    private var selectedDate: Calendar = Calendar.getInstance()
//    private var selectedTime: Calendar = Calendar.getInstance()
//
//    // 장소 검색을 위한 카카오 API 설정
//    companion object {
//        const val BASE_URL = "https://dapi.kakao.com/"
//        const val API_KEY = "KakaoAK b1ad2839ad51d75ceb55ee5ed706608e"
//    }
//
//    private val listItems = arrayListOf<ListLayout>() // 리사이클러 뷰 아이템
//    private val listAdapter = ListAdapter(listItems) // 리사이클러 뷰 어댑터
//    private var pageNumber = 1 // 검색 페이지 번호
//    private var keyword = "" // 검색 키워드
//
//
//    // 각 editText가 입력되었을 때 다음 LinearLayout이 보이도록 설정
//    private val nameTextWatcher = object : TextWatcher {
//        override fun afterTextChanged(s: Editable?) {
//            if (!s.isNullOrEmpty()) {
//                binding.llPromiseDate.visibility = View.VISIBLE
//            } else {
//                binding.llPromiseDate.visibility = View.GONE
//            }
//        }
//
//        override fun beforeTextChanged(
//            s: CharSequence?, start: Int, count: Int, after: Int
//        ) {}
//
//        override fun onTextChanged(
//            s: CharSequence?, start: Int, before: Int, count: Int
//        ) {}
//    }
//
//    private val dateTextWatcher = object : TextWatcher {
//        override fun afterTextChanged(s: Editable?) {
//            if (!s.isNullOrEmpty()) {
//                binding.llPromisePlace.visibility = View.VISIBLE
//            } else {
//                binding.llPromisePlace.visibility = View.GONE
//            }
//        }
//
//        override fun beforeTextChanged(
//            s: CharSequence?, start: Int, count: Int, after: Int
//        ) {}
//
//        override fun onTextChanged(
//            s: CharSequence?, start: Int, before: Int, count: Int
//        ) {}
//    }
//
//    private val placeTextWatcher = object : TextWatcher {
//        override fun afterTextChanged(s: Editable?) {
//            if (!s.isNullOrEmpty()) {
//                binding.llPromisePeople.visibility = View.VISIBLE
//            } else {
//                binding.llPromisePeople.visibility = View.GONE
//            }
//        }
//
//        override fun beforeTextChanged(
//            s: CharSequence?, start: Int, count: Int, after: Int
//        ) {}
//
//        override fun onTextChanged(
//            s: CharSequence?, start: Int, before: Int, count: Int
//        ) {}
//    }
//
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        super.onCreateView(inflater, container, savedInstanceState)
//
//        binding = DataBindingUtil.inflate(
//            inflater, R.layout.fragment_plus, container, false
//
//        )
//
//        binding.toolbarPromiseCreate.title = "약속 생성"
//
//        // 약속 이름 글자 수 표시
//        binding.etPromiseName.addTextChangedListener(object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                val textLenght = s?.length ?: 0
//                binding.tvPromiseNameCnt.text = "$textLenght/20"
//            }
//            override fun afterTextChanged(s: Editable?) {
//                val textLenght = s?.length ?: 0
//                binding.tvPromiseNameCnt.text = "$textLenght/20"
//            }
//        })
//
//        // 각 editText가 입력되었을 때 다음 LinearLayout이 보이도록 설정
//        binding.etPromiseName.addTextChangedListener(nameTextWatcher)
//        binding.etPromiseDate.addTextChangedListener(dateTextWatcher)
//        binding.etPromisePlace.addTextChangedListener(placeTextWatcher)
//
//        // 약속 일정 클릭 시 날짜, 시간 select
//        binding.etPromiseDate.setOnFocusChangeListener { _, hasFocus ->
//            if (hasFocus) {
//                showDateTimePicker()
//            }
//        }
//
//        binding.rvPromisePlaceList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
//        binding.rvPromisePlaceList.adapter = listAdapter
//        listAdapter.setItemClickListener(object : ListAdapter.OnItemClickListener {
//            override fun onClick(v: View, position: Int) {
//                val mapPoint = MapPoint.mapPointWithGeoCoord(listItems[position].y, listItems[position].x)
//            }
//
//        })
//
//
////        binding.etPromisePlace.setOnEditorActionListener { v, actionId, event ->
////            var handled = false
////            if (actionId == EditorInfo.IME_ACTION_DONE) {
////                var keyword = binding.etPromisePlace.text.toString()
////                Log.d("keyword", "${keyword}")
////            }
////        }
////        binding.etPromisePlace.setOnEditorActionListener { _, actionId, _ ->
////            if (actionId == EditorInfo.IME_ACTION_DONE) {
////                keyword = binding.etPromisePlace.text.toString()
//////                Log.d("keyword", keyword)
////            } else {
//////                Log.w("키워드 받아오기 실패", "실패")
////            }
////        }
//
//        // checkbox 클릭 시 isSelected값 토글
//        binding.llPromisePeopleLater.setOnClickListener {
//            binding.ivPromisePeopleLater.isSelected = !binding.ivPromisePeopleLater.isSelected
//            binding.tvPromisePeopleLater.isSelected = !binding.tvPromisePeopleLater.isSelected
//        }
//
//
//
//        searchKeyword("대구은행")
//
//        return binding.root
//    }
//
//    // date, time 설정하는 dialog 보여주는 함수
//    private fun showDateTimePicker() {
//        val currentYear = selectedDate.get(Calendar.YEAR)
//        val currentMonth = selectedDate.get(Calendar.MONTH)
//        val currentDay = selectedDate.get(Calendar.DAY_OF_MONTH)
//
//        val dpd = DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
//            selectedDate.set(year, month, dayOfMonth)
//
//            val currentHour = selectedTime.get(Calendar.HOUR_OF_DAY)
//            val currentMinute = selectedTime.get(Calendar.MINUTE)
//
//            val tpd = TimePickerDialog(requireContext(), { _, hourOfDay, minute ->
//                selectedTime.set(year, month, dayOfMonth, hourOfDay, minute)
//
//                val dateTime = selectedDate.time
//                val dateTimeFormat = SimpleDateFormat("yyyy년 MM월 dd일 a hh시 mm분", Locale.KOREA)
//                binding.etPromiseDate.setText(dateTimeFormat.format(dateTime))
//            }, currentHour, currentMinute, false)
//
//            tpd.show()
//
//        }, currentYear, currentMonth, currentDay)
//        dpd.show()
//    }
//
//    // 장소 키워드 검색 함수
//    private fun searchKeyword(keyword: String) {
//        val retrofit = Retrofit.Builder() // Retrofit 구성
//            .baseUrl(BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//        val api = retrofit.create(KakaoApi::class.java) // 통신 인터페이스를 객체로 생성
//        val call = api.getSearchKeyword(API_KEY, keyword, 1) // 검색 조건 입력
//
//        call.enqueue(object : Callback<SearchPlaceResponse> {
//            override fun onResponse(
//                call: Call<SearchPlaceResponse>,
//                response: Response<SearchPlaceResponse>
//            ) {
//                Log.d("통신성공", "Raw: ${response.raw()}")
//                Log.d("통신성공", "Body: ${response.body()}")
//
//            }
//
//            override fun onFailure(call: Call<SearchPlaceResponse>, t: Throwable) {
//                Log.w("MainActivity", "통신실패: ${t.message}")
//            }
//        })
//    }
//}