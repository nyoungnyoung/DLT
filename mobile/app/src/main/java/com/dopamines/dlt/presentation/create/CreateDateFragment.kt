package com.dopamines.dlt.presentation.create

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.dopamines.dlt.R
import com.dopamines.dlt.databinding.FragmentCreateDateBinding
import com.dopamines.dlt.presentation.home.HomeActivity
import java.util.Calendar

class CreateDateFragment: Fragment() {
    // view가 생성되었을 때 : 프레그먼트와 레이아웃을 연결시켜주는 부분
    private lateinit var binding: FragmentCreateDateBinding
//    private lateinit var previousBinding : FragmentPlusBinding
    private lateinit var viewModel: CreateViewModel

    var PromiseInfoDate: String = ""
    var PromiseInfoTime: String = ""
    var InputDate: String = ""
    var InputTime: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        // binding & viewModel 설정
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_create_date, container, false
        )

        viewModel = ViewModelProvider(requireActivity()).get(CreateViewModel::class.java)

        // 툴바 제목 설정 및 뒤로가기
        binding.toolbarPromiseCreate.title = "약속 생성"
        binding.toolbarPromiseCreate.btnBack.setOnClickListener{
            findNavController().popBackStack()
        }

        // 하단바 숨기기
        (activity as HomeActivity).HideBottomNavi(true)

        // 달력 Custom
        binding.cvPromiseDate.apply {
            setDateTextAppearance(R.style.CustomDateTextAppearance)
            setWeekDayTextAppearance(R.style.CustomWeekDayAppearance)
            setHeaderTextAppearance(R.style.CustomHeaderTextAppearance)
        }

        // viewModel의 dateText에 값이 있을 경우 -> 해당 값으로 선택된 일정 출력 및 viewModel에 저장
        if (viewModel.dateText.value != null) {
            Log.i("dateText값 이미 있음", viewModel.dateText.value.toString())
            Log.i("planDate", viewModel.planDate.value.toString())
            Log.i("planTime", viewModel.planTime.value.toString())
            binding.tmpPromiseDate.text = viewModel.dateText.value.toString()
        }
        Log.i("dateText값", viewModel.dateText.value.toString())


        // 선택한 날짜 출력 및 viewModel에 저장, 시간 선택 칸 보이게 만들기
        binding.cvPromiseDate.setOnDateChangedListener { widget, date, selected ->
            val calendar = Calendar.getInstance()

            var selectedMonth: String = ""
            var selectedDay: String = ""

            if (date.month.toString().length < 2) {
                selectedMonth = "0" + date.month.toString()
            } else {
                selectedMonth = date.month.toString()
            }

            if (date.day.toString().length < 2) {
                selectedDay = "0" + date.day.toString()
            } else {
                selectedDay = date.day.toString()
            }

            binding.tpPromiseTime.visibility = View.VISIBLE
            binding.tvCreatePromiseDate.text = "약속 시간을 설정해 주세요."

            binding.tmpPromiseDate.text = "${date.year}/${selectedMonth}/${selectedDay}"
            InputDate = "${date.year.toString()}/${selectedMonth}/${selectedDay}"

            PromiseInfoDate = "${date.year.toString()}-${selectedMonth}-${selectedDay}"
            viewModel.setPlanDate(PromiseInfoDate)
        }

        // 선택한 시간 출력 및 viewModel에 저장
        binding.tpPromiseTime.setOnTimeChangedListener { view, hourOfDay, minute ->
            var amPm: String = ""
            var selectedTmpHour: String = ""        // 선택된 시간 표시
            var selectedHour: String = ""           // ViewModel에 반영해줄 시간
            var selectedMinute: String = ""         // ViewModel에 반영해줄 분

            if (hourOfDay >= 12) {
                selectedTmpHour = if (hourOfDay - 12 < 10) "0${(hourOfDay - 12)}" else (hourOfDay - 12).toString()
                selectedHour = hourOfDay.toString()
                amPm = "오후"
            } else {
                selectedTmpHour = if (hourOfDay < 10) "0${hourOfDay}" else hourOfDay.toString()
                selectedHour = if (hourOfDay < 10) "0${hourOfDay}" else hourOfDay.toString()
                amPm = "오전"
            }

            if (minute < 10) {
                selectedMinute = "0${minute}"
            } else {
                selectedMinute = minute.toString()
            }

            binding.tmpPromiseAmpm.text = amPm
            binding.tmpPromiseTime.text = "${selectedTmpHour}시 ${selectedMinute}분"
            InputTime = "${amPm} ${selectedTmpHour}시 ${selectedMinute}분"

            PromiseInfoTime = "${selectedHour}:${selectedMinute}:00"
            viewModel.setPlanTime(PromiseInfoTime)
        }

        // 확인 버튼 클릭 시 navigation & 선택한 약속 일정 editText에 반영
        binding.btnPromiseCreate.setOnClickListener {
            if (InputDate != "" && InputTime != "") {
                viewModel.setDateText(
                    Editable.Factory.getInstance().newEditable("${InputDate} ${InputTime}")
                )
                findNavController().popBackStack()
            } else {
                Toast.makeText(context, "약속 날짜와 시간을 모두 설정 해 주세요.", Toast.LENGTH_SHORT).show()
            }
        }
        return binding.root
    }

//    override fun onDestroyView() {
//        super.onDestroyView()
//        (activity as HomeActivity).HideBottomNavi(true)
//    }
}