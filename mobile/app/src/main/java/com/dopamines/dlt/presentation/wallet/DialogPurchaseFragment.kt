package com.dopamines.dlt.presentation.wallet

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.dopamines.dlt.R
import com.dopamines.dlt.databinding.FragmentDialogPurchaseBinding

class DialogPurchaseFragment : Fragment() {

    private lateinit var binding: FragmentDialogPurchaseBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_dialog_purchase, container, false
        )

        // Spinner 데이터 설정
        val spinnerData = resources.getStringArray(R.array.bank_names).toList()

        // ArrayAdapter를 사용하여 Spinner와 데이터를 연결
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, spinnerData)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.bankSpinner.adapter = adapter

        // Spinner 아이템 선택 이벤트 리스너 설정
        binding.bankSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem = spinnerData[position]
                // 선택된 항목 처리
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // 아무 항목도 선택되지 않았을 때 처리
            }
        }

        return binding.root
    }



}

