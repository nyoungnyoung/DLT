package com.dopamines.dlt.presentation.wallet

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dopamines.dlt.R
import com.dopamines.dlt.databinding.FragmentDialogPurchaseBinding
import com.dopamines.dlt.databinding.FragmentWalletBinding
import com.dopamines.dlt.presentation.gallery.CommentData
import com.dopamines.dlt.presentation.gallery.GalleryDetailReviewFragment
import com.dopamines.dlt.presentation.gallery.GalleryRepository
import com.dopamines.dlt.presentation.gallery.GalleryViewModel
import com.google.gson.Gson
import kotlinx.coroutines.launch
import kr.co.bootpay.android.*
import kr.co.bootpay.android.events.BootpayEventListener
import kr.co.bootpay.android.models.BootExtra
import kr.co.bootpay.android.models.BootItem
import kr.co.bootpay.android.models.BootUser
import kr.co.bootpay.android.models.Payload
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class WalletFragment : Fragment() {
    private lateinit var binding: FragmentWalletBinding

    // 부트페이
    private val application_id = "6461b5bb3049c8001d9686d3"
    private lateinit var dialogBinding: FragmentDialogPurchaseBinding


    private var myWallet: Int = 0
    private lateinit var viewModel: WalletViewModel


    private var resultDialog: Dialog? = null
    private var resultDialog2: Dialog? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_wallet, container, false
        )


        val walletRepository = WalletRepository(requireContext())
        viewModel = WalletViewModel(walletRepository)

        binding.btnMoneyIn.setOnClickListener {
            showDialog(0)
        }
        binding.btnMoneyOut.setOnClickListener {
            showDialog(1)
        }


        viewModel.getWalletData()

        binding.rvAmountList.layoutManager = LinearLayoutManager(requireContext())

        viewModel.walletData.observe(viewLifecycleOwner) { walletData ->
            binding.rvAmountList.adapter = walletData?.let { GroupedAmountAdapter(it) }
            binding.tvWonCost.text = walletData?.let { it.total.toString() }
            myWallet = walletData?.total!!
        }


        return binding.root
    }

    private fun showDialog(type: Int) {
        val purchaseDialog = Dialog(requireContext())
        dialogBinding = FragmentDialogPurchaseBinding.inflate(LayoutInflater.from(requireContext()))
        purchaseDialog.setContentView(dialogBinding.root)

        purchaseDialog.window?.setBackgroundDrawableResource(R.drawable.card_rounded_detail)

        when (type) {
            0 -> {
                dialogBinding.tvTitleSetCost.text = "충전하기"
                dialogBinding.llTemp.visibility = View.GONE
                dialogBinding.btnPurchase.setOnClickListener {
                    val inputCost = dialogBinding.etInputCost.text.toString()
                    val price = inputCost.toDoubleOrNull() ?: 0.0
                    if (price >= 100) {
                        // goRequest(price, purchaseDialog)

                        lifecycleScope.launch {
                            viewModel.sendChargeData(
                                inputCost.toInt(),
                                "임시",
                                "2023-05-26",
                                "10:35:20",
                                "receipt"
                            )
                            viewModel.chargeResult.observe(viewLifecycleOwner) { res ->
                                Log.i("RESULT", res.toString())
                                if (res) {
                                    Log.i("RESINWALLET", res.toString())
                                    purchaseDialog.dismiss()
                                    showResultDialog(inputCost.toDouble())
                                    // 응답 처리가 완료되었으므로 chargeResult를 다시 false로 설정
//
                                }
                            }
                        }
                    } else {
                        Toast.makeText(requireContext(), "100원 이상 입력해주세요", Toast.LENGTH_SHORT)
                            .show()

                    }

                }

            }

            1 -> {
                dialogBinding.tvTitleSetCost.text = "인출하기"

                dialogBinding.btnPurchase.setOnClickListener {
                    val inputCost = dialogBinding.etInputCost.text.toString()
                    val price = inputCost.toDoubleOrNull() ?: 0.0

                    if (price >= 1) {
                        if (price <= myWallet) {
                            val inputAccount = dialogBinding.etBackAccount.text.toString()
                            val selectedItem = dialogBinding.bankSpinner.selectedItem as String

                            if (inputAccount.isNotEmpty() && selectedItem.isNotEmpty()) {
                                lifecycleScope.launch {
                                    viewModel.sendWithdrawData(price.toInt())
                                    viewModel.withDrawResult.observe(viewLifecycleOwner) { res ->
                                        if (res) {
                                            purchaseDialog.dismiss()
                                            showWithDrawDialog(
                                                price.toInt(),
                                                selectedItem,
                                                inputAccount
                                            )
                                            viewModel.resetChargeResult()
                                        }
                                    }
                                }
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "계좌 정보를 입력해주세요.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "보유금액을 초과하여 인출할 수 없습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "1원 이상 인출이 가능합니다.", Toast.LENGTH_SHORT)
                            .show()
                    }


                }

                dialogBinding.btnPurchase.text = "인출"

            }
        }

        dialogBinding.btnClosePurchase.setOnClickListener {
            purchaseDialog.dismiss()

        }


        purchaseDialog.show()
    }


    private fun showResultDialog(inputPrice: Double) {
        if (resultDialog2 == null) {
            resultDialog2 = Dialog(requireContext())
            resultDialog2?.setContentView(R.layout.fragment_dialog_purchase_after)
        }

        resultDialog2?.window?.setBackgroundDrawableResource(R.drawable.card_rounded_detail)

        resultDialog2?.let { dialog ->
            val resultTextView: TextView = dialog.findViewById(R.id.tv_purchase_amount)
            resultTextView.text = "${inputPrice.toInt()}원"


            val resultTypeTextView: TextView = dialog.findViewById(R.id.tv_result_type)
            resultTypeTextView.text = "충전 완료"
            val completeImageView: ImageView = dialog.findViewById(R.id.iv_purchase_complete)
            val completeTextView: TextView = dialog.findViewById(R.id.tv_complete_message)
            completeTextView.text = "충전이 완료되었습니다."

            val closeButton: AppCompatButton = dialog.findViewById(R.id.btn_after_purchase)

            val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.dialog_check)
            completeImageView.startAnimation(animation)
            completeImageView.isVisible = true

            closeButton.setOnClickListener {
                viewModel.getWalletData()
                viewModel.resetChargeResult()
                dialog.dismiss()
            }

            dialog.show()
        }
    }


    private fun showWithDrawDialog(inputPrice: Int, selectedItem: String, inputAccount: String) {
        if (resultDialog == null) {
            resultDialog = Dialog(requireContext())
            resultDialog?.setContentView(R.layout.fragment_dialog_purchase_after)
        }

        resultDialog?.window?.setBackgroundDrawableResource(R.drawable.card_rounded_detail)

        val resultTextView: TextView? = resultDialog?.findViewById(R.id.tv_purchase_amount)
        resultTextView?.text = "${inputPrice}원"

        val resultTypeTextView: TextView = resultDialog!!.findViewById(R.id.tv_result_type)
        resultTypeTextView.text = "인출 완료"

        val completeImageView: ImageView = resultDialog!!.findViewById(R.id.iv_purchase_complete)
        val completeTextView: TextView = resultDialog!!.findViewById(R.id.tv_complete_message)
        val message = "은행: $selectedItem\n\n계좌번호: $inputAccount"
        completeTextView.text = message

        val closeButton: AppCompatButton = resultDialog!!.findViewById(R.id.btn_after_purchase)

        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.dialog_check)
        completeImageView.startAnimation(animation)
        completeImageView.isVisible

        closeButton.setOnClickListener {
            viewModel.getWalletData()
            resultDialog?.dismiss()

        }

        resultDialog?.show()
    }
}


    // 부트페이
//    private fun goRequest(inputPrice: Double, purchaseDialog: Dialog) {
//        val user = BootUser().setPhone("010-1234-5678") // 구매자 정보
//        val extra = BootExtra()
//            .setCardQuota("0,2,3") // 일시불, 2개월, 3개월 할부 허용, 할부는 최대 12개월까지 사용됨 (5만원 이상 구매시 할부허용 범위)
//
//
//        val pg: String = "이니시스"
//        val method: String = "카드"
//
//        val items: MutableList<BootItem> = ArrayList()
//
//        val item1 =
//            BootItem().setName("충전하기").setId("ITEM_CODE_MOUSE").setQty(1).setPrice(inputPrice)
//        items.add(item1)
//
//
//        val payload = Payload()
//        payload.setApplicationId(application_id)
//            .setOrderName("Don't Be Late Together")
//            .setPg(pg)
//            .setOrderId("1234")
//            .setMethod(method)
//            .setPrice(inputPrice)
//            .setUser(user)
//            .setExtra(extra).items = items
//
//        val fragmentManager = requireActivity().supportFragmentManager
//
//
//        val map: MutableMap<String, Any> = HashMap()
//        map["1"] = "abcdef"
//        map["2"] = "abcdef55"
//        map["3"] = 1234
//        payload.metadata = map
//        Bootpay.init(fragmentManager, requireContext().applicationContext)
//            .setPayload(payload)
//            .setEventListener(object : BootpayEventListener {
//                override fun onCancel(data: String) {
//                    Log.d("bootpay", "cancel: $data")
//                }
//
//                override fun onError(data: String) {
//                    Log.d("bootpay", "error: $data")
//                }
//
//                override fun onClose() {
//                    val data = ""
//                    Log.d("bootpay", "close: $data")
//                    Bootpay.removePaymentWindow()
//                }
//
//                override fun onIssued(data: String) {
//                    Log.d("bootpay", "issued: $data")
//                }
//
//                override fun onConfirm(data: String): Boolean {
//                    Log.d("bootpay", "confirm: $data")
//                    //                        Bootpay.transactionConfirm(data); //재고가 있어서 결제를 진행하려 할때 true (방법 1)
//                    return true //재고가 있어서 결제를 진행하려 할때 true (방법 2)
//                    //                        return false; //결제를 진행하지 않을때 false
//                }
//
//                override fun onDone(data: String) {
//                    Log.d("done", data)
//
//
//                    val extractedData = extractDataForCharge(data)
//
//                    // 추출된 데이터 사용
//                    val money = extractedData["money"] as Int
//                    val method = extractedData["method"] as String
//                    val transactionDate = extractedData["transactionDate"] as String
//                    val transactionTime = extractedData["transactionTime"] as String
//                    val receipt = extractedData["receipt"] as String
//
//                    Log.i("gkdk", money.toString())
//                    Log.i("tranationTIME", transactionTime)
//                    lifecycleScope.launch {
//                        viewModel.sendChargeData(
//                            money,
//                            method,
//                            transactionDate,
//                            transactionTime,
//                            receipt
//                        )
//                        viewModel.chargeResult.observe(viewLifecycleOwner) { res ->
//                            Log.i("RESULT", res.toString())
//                            if (res) {
//                                Log.i("RESINWALLET", res.toString())
//                                purchaseDialog.dismiss()
//                                showResultDialog(inputPrice)
//                                // 응답 처리가 완료되었으므로 chargeResult를 다시 false로 설정
////
//                            }
//                        }
//                    }
//
//
//                }
//            }).requestPayment()
//    }
//
//
//    fun extractDataForCharge(responseJson: String): Map<String, Any?> {
//        val gson = Gson()
//        val response = gson.fromJson(responseJson, JsonResponseData::class.java)
//
//        val price = response.data.price
//        val methodOrigin = response.data.method_origin
//        val purchasedAt = response.data.purchased_at
//        val receiptUrl = response.data.receipt_url
//
//        // purchasedAt을 날짜와 시간으로 분리
//        val dateTimeFormatter =
//            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssxxx", Locale.getDefault())
//        val parsedDateTime = OffsetDateTime.parse(purchasedAt, dateTimeFormatter)
//        val date =
//            parsedDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault()))
//        val time =
//            parsedDateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss", Locale.getDefault()))
//
//        // 데이터 맵으로 변환
//        return mapOf(
//            "money" to price,
//            "method" to methodOrigin,
//            "transactionDate" to date,
//            "transactionTime" to time,
//            "receipt" to receiptUrl
//        )
//    }
//}