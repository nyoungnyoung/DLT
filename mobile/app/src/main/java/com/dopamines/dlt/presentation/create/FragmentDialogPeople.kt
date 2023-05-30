package com.dopamines.dlt.presentation.create

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dopamines.dlt.R
import com.dopamines.dlt.databinding.FragmentDialogPeopleBinding
import com.dopamines.dlt.presentation.home.PlusFragment
import kotlinx.coroutines.launch

class FragmentDialogPeople : DialogFragment() {

    private lateinit var binding: FragmentDialogPeopleBinding
    private lateinit var viewModel: CreateViewModel

    // 리사이클러뷰 설정
    private var listItems = arrayListOf<PeopleSearchResponse>()
    private var participantsList = arrayListOf<PeopleSearchResponse>()
    private val listAdapter = PeopleSearchAdapter(listItems, participantsList)
    private val participantAdapter = PeopleAdapter(participantsList)

    // 약속 생성 API 요청용 변수
    private var participantsIdList = arrayListOf<Int>()

    // 참여자 검색 관련 변수 설정
    private var keyword = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // binding & viewModel 정의
        binding = FragmentDialogPeopleBinding.inflate(inflater, container, false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        viewModel = ViewModelProvider(requireActivity()).get(CreateViewModel::class.java)

        // 뷰모델 변경될때마다 listItems값 업데이트
        viewModel.searchPeople.observe(viewLifecycleOwner) {
            searchPeople ->
            if (searchPeople != null) {
                listItems.clear()
                listItems.addAll(searchPeople)
                listAdapter.notifyDataSetChanged()
                Log.i("뷰모델 업데이트", listItems.toString())
            }
        }

        // 뷰모델의 참가자 id리스트 변경될 때마다 어뎁터에 데이터 변경 알려주기
        viewModel.participantIds.observe(viewLifecycleOwner) {
            participantsid ->
            if (participantsid != null) {
                binding.tvDialogPeopleCnt.text = "${participantsIdList.size}명"
                participantAdapter.notifyDataSetChanged()
                Log.i("Dialog 내 참여자목록Id 업데이트", participantsid.toString())
                Log.i("Dialog 내 참여자목록Info 업데이트", participantsList.toString())
                Log.i("Dialog 내에서 viewModel id확인", viewModel.participantIds.value.toString())
                Log.i("Dialog 내에서 viewModel info확인", viewModel.participantInfo.value.toString())
            }
        }

        // 리사이클러뷰 설정
        binding.rvPromiseDialogSearchList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvPromiseDialogSearchList.adapter = listAdapter
        binding.rvPromiseDialogPeopleList.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvPromiseDialogPeopleList.adapter = participantAdapter

        // keyword 입력 후 완료(엔터) 버튼 누를 경우 참가자 검색 API 실행
        binding.etDialogPeople.setOnEditorActionListener { v, actionId, event ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT) {
                keyword = binding.etDialogPeople.text.toString()
                lifecycleScope.launch {
                    viewModel.searchPeople(keyword)
                }
            }
            handled
        }

        // 리사이클러뷰 내 토글 버튼 클릭 시 업데이트 되는 참여자 목록
        listAdapter.setParticipantCheckedChangeListener(object : PeopleSearchAdapter.OnParticipantCheckedChangeListener {
            override fun onChekedChanged(ItemList: PeopleSearchResponse, isChecked: Boolean) {
                if (isChecked) {
                    // check 해제 된 상태일 때 -> 참여자 목록에 해당 사용자 정보 추가, 참여자 id 리스트에 해당 사용자 id추가
                    participantsList.add(ItemList)
                    participantsIdList.add(ItemList.accountId)
                } else {
                    // check된 상태일 때(참가자 목록에 있을 때) -> 참여자 목록에서 해당 사용자 정보 제거
                    participantsList.remove(ItemList)
                    participantsIdList.remove(ItemList.accountId)
                }
                viewModel.setParticipantIds(participantsIdList.joinToString(","))
            }
        })

        // 확인 버튼 클릭 시 뷰모델 업데이트 & 팝업창 닫기
        binding.btnPromisePeople.setOnClickListener {
            viewModel.setParticipantIds(participantsIdList.joinToString(","))
            viewModel.setParticipantInfo(participantsList)

            Log.i("dialog 종료시 viewModel id목록", viewModel.participantIds.value.toString())
            Log.i("dialog 종료시 viewModel 참가자목록", viewModel.participantInfo.value.toString())

            dismiss()
        }

        // 닫기 버튼 클릭 시 팝업창 닫기
        binding.ivDialogClose.setOnClickListener {
            dismiss()
        }

        return binding.root
    }

    override fun dismiss() {
        super.dismiss()
    }
}