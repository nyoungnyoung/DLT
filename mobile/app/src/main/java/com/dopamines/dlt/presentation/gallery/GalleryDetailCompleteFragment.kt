package com.dopamines.dlt.presentation.gallery

import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView

import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton

import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.dopamines.dlt.R

import com.dopamines.dlt.databinding.FragmentGalleryDetailCompleteBinding

import com.dopamines.dlt.databinding.ItemCardFriendCompleteBinding

import com.dopamines.dlt.presentation.home.HomeActivity
import com.dopamines.dlt.presentation.notification.NotificationRepository
import com.dopamines.dlt.presentation.notification.NotificationViewModel
import java.time.LocalTime
import java.time.format.DateTimeFormatter

import java.util.Locale
import kotlin.math.abs


class GalleryDetailCompleteFragment : Fragment() {
    private lateinit var binding: FragmentGalleryDetailCompleteBinding
    private lateinit var galleryFriendAdapter: GalleryFriendAdapter

    private var planId: Int? = null
    private var planDetailTime: String? = null
    private var planName: String = ""

    private lateinit var galleryDetailCompleteViewModel: GalleryDetailCompleteViewModel
    private lateinit var notificationViewModel: NotificationViewModel



    private lateinit var tvNoParticipantsWaiter: TextView
    private lateinit var tvNoParticipantsLater: TextView


    override fun onAttach(context: Context) {
        super.onAttach(context)

        // 네비게이션바 숨겨주기
        val HomeActivity = activity as HomeActivity
        HomeActivity.HideBottomNavi(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_gallery_detail_complete,
            container,
            false
        )

        val galleryRepository = GalleryRepository(requireContext())
        galleryDetailCompleteViewModel = GalleryDetailCompleteViewModel(galleryRepository)

        val notificationRepository = NotificationRepository(requireContext())
        notificationViewModel = NotificationViewModel(notificationRepository)

        // 네비게이션바 숨겨주기
        val HomeActivity = activity as HomeActivity
        HomeActivity.HideBottomNavi(true)

        tvNoParticipantsWaiter = binding.tvNoParticipantsWaiter
        tvNoParticipantsLater = binding.tvNoParticipantsLater

        arguments?.let { bundle ->
            planId = bundle.getInt("planId", -1)
        }
        Log.i("PLANID IN Complete", planId.toString())

        planId?.let { galleryDetailCompleteViewModel.getPlanEndDetailData(it) }






        galleryDetailCompleteViewModel.planEndDetailData.observe(viewLifecycleOwner) { detailData ->
            Log.i("DETAILDATA", detailData.toString())

            binding.tvTitleCard.text = detailData?.title

            binding.tvTitlePlanLocation.text = detailData?.location
            binding.tvContentPlanLocation.text = detailData?.address


            binding.tvLateCost.text = detailData?.cost.toString() + "원"


            if (detailData?.myDetail?.getMoney!! >= 0) {
                binding.tvRecordCost.text =
                    "획득한 지각비 : " + detailData?.myDetail?.getMoney.toString() + "원"
            } else {
                binding.tvRecordCost.text =
                    "지출한 지각비 : " + abs(detailData?.myDetail?.getMoney).toString() + "원"
            }

            if (detailData.isSettle == true) {
                binding.btnEndDetail.isClickable = false
                binding.btnEndDetail.alpha = 0.5F
                binding.btnEndDetail.text = "정산 완료"

            } else {
                binding.btnEndDetail.isClickable = true

            }


            if (detailData?.myDetail?.lateTime!! > 0) {
                binding.ivStamp.setImageResource(R.drawable.stamp_late)
            } else {
                binding.ivStamp.setImageResource(R.drawable.stamp_safe)
            }

            planName = detailData.title!!


        }

        galleryDetailCompleteViewModel.myData.observe(viewLifecycleOwner) { myData ->
            Log.i("myData", myData.toString())
            val colorRes = when (myData.designation) {
                0 -> R.color.orange3
                1 -> R.color.violet3
                else -> R.color.red3
            }
            val color = ContextCompat.getColor(requireContext(), colorRes)
            ViewCompat.setBackgroundTintList(
                binding.itemCardMine.clBackground,
                ColorStateList.valueOf(color)
            )

            val formatter = DateTimeFormatter.ofPattern("HH:mm:ss", Locale.KOREA)
            val formattedTime = if (myData.arrivalTime != null) {
                val time = LocalTime.parse(myData.arrivalTime, formatter)
                time.format(DateTimeFormatter.ofPattern("도착 시간: HH시 mm분", Locale.KOREA))
            } else {
                "도착 시간: 정보 없음"
            }

            binding.tvRecordTime.text = formattedTime

            binding.itemCardMine.ivNameCardFriend.text = myData?.nickname.toString()

            Glide.with(binding.root)
                .load(myData.profile)
                .circleCrop()
                .into(binding.itemCardMine.ivImgCardFriend)

        }




        galleryDetailCompleteViewModel.particiapantList.observe(viewLifecycleOwner) { participantList ->
            val newparticipantList = participantList.participants
            if (participantList.success == true) {
                // dialog열고 닫는거
                showResultDialog(newparticipantList)

            } else {

                showFailDialog(newparticipantList)
            }
        }



        galleryDetailCompleteViewModel.waiterData.observe(viewLifecycleOwner) { waiterData ->
            if (waiterData.isNotEmpty()) {
                binding.rvCompleteFriendList.layoutManager = GridLayoutManager(requireContext(), 3)
                galleryFriendAdapter =
                    GalleryFriendAdapter(waiterData, planId!!, notificationViewModel, planName)
                binding.rvCompleteFriendList.adapter = galleryFriendAdapter
                galleryFriendAdapter.notifyDataSetChanged()

                // gone으로 만들어줘야함

                binding.icCountWaiter.tvContentCountPerson.text = waiterData.size.toString()

            } else {
                // 데이터가 없는 경우
                binding.rvCompleteFriendList.layoutManager = LinearLayoutManager(requireContext())
                binding.rvCompleteFriendList.adapter = null
                binding.icCountWaiter.tvContentCountPerson.text = "0"
                tvNoParticipantsWaiter.visibility = View.VISIBLE
            }
        }

        galleryDetailCompleteViewModel.laterData.observe(viewLifecycleOwner) { laterData ->
            Log.i("arlgne;igno", laterData.toString())
            if (laterData.isNotEmpty()) {

                binding.rvLateFriendList.layoutManager = GridLayoutManager(requireContext(), 3)
                galleryFriendAdapter =
                    GalleryFriendAdapter(laterData, planId!!, notificationViewModel, planName)
                binding.rvLateFriendList.adapter = galleryFriendAdapter
                galleryFriendAdapter.notifyDataSetChanged()



                binding.icCountLater.tvContentCountPerson.text = laterData.size.toString()
            } else {
                // 데이터가 없는 경우
                binding.rvLateFriendList.layoutManager = LinearLayoutManager(requireContext())
                binding.rvLateFriendList.adapter = null
                binding.icCountLater.tvContentCountPerson.text = "0"
                tvNoParticipantsLater.visibility = View.VISIBLE
            }
        }


        binding.btnEndDetail.setOnClickListener {
            //정산 요청 로직
            galleryDetailCompleteViewModel.settleTime(planId!!)


        }
        galleryDetailCompleteViewModel.particiapantList.observe(viewLifecycleOwner) { ppList ->
            // 디바이스 토큰 모아서 리스트로 만들어서
            val deviceTokens: List<String> = ppList.participants.map { token -> token.deviceToken }

            // fcm 요청 보내기(기기그룹 등록)
            notificationViewModel.registerGroup(planId.toString(), deviceTokens)

        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()

//        val HomeActivity = activity as HomeActivity
//        HomeActivity.HideBottomNavi(false)
    }

    override fun onPause() {
        super.onPause()

//        val HomeActivity = activity as HomeActivity
//        HomeActivity.HideBottomNavi(false)

    }

    private fun showResultDialog(list: List<PayParticipant>) {
        val resultDialog = Dialog(requireContext())
        resultDialog.setContentView(R.layout.fragment_dialog_settle)

        val completeMessage: TextView = resultDialog.findViewById(R.id.tv_complete_message_ment)
        completeMessage.isVisible = false

        val completeImageView: ImageView = resultDialog.findViewById(R.id.iv_purchase_complete)


        val closeButton: AppCompatButton = resultDialog.findViewById(R.id.btn_after_purchase)
        val animation = AnimationUtils.loadAnimation(requireContext(), R.anim.dialog_check)
        completeImageView.startAnimation(animation)
        completeImageView.isVisible


        val message = "지각비 정산이 완료되었습니다."

        val title = "정산 완료"
//        Log.i("planName", planName)
//        val body = message
        val body = "[${planName}] " + message
        val planId2 = planId.toString()
        val topic = planId.toString()
        val type = "toWalletFragment"
        notificationViewModel.sendTopicPush(title, body, planId2, topic, type)

        closeButton.setOnClickListener {
            planId?.let { galleryDetailCompleteViewModel.getPlanEndDetailData(it) }
            resultDialog.dismiss()
        }

        resultDialog.show()
    }

    private fun showFailDialog(list: List<PayParticipant>) {
        val resultDialog = Dialog(requireContext())
        resultDialog.setContentView(R.layout.fragment_dialog_settle)

        val completeImageView: ImageView = resultDialog.findViewById(R.id.iv_purchase_complete)
        context?.let {
            Glide.with(it)
                .load(R.drawable.ic_cancel)
                .into(completeImageView)
        }

        val recyclerView: RecyclerView = resultDialog.findViewById(R.id.rv_pay_participant)
        recyclerView.setHasFixedSize(true)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val participantAdapter = payParticipantAdapter(list)
        recyclerView.adapter = participantAdapter

        val participantList = list


        val stringBuilder = StringBuilder()
        val message = if (participantList.isNotEmpty()) {
            val failedMessage =
                "${participantList.joinToString { it.nickName }}님의 금액이 부족하여 지각비 정산에 실패하였습니다."
            stringBuilder.toString().trim() + failedMessage
        } else {
            ""
        }

        val title = "정산 실패"
        val body = "[${planName}] " + message
        val planId2 = planId.toString()
        val topic = planId.toString()
        val type = "toEndDetailFragment"

        notificationViewModel.sendTopicPush(title, body, planId2, topic, type)
        val resultTextView: TextView = resultDialog.findViewById(R.id.tv_purchase_amount)
        resultTextView.text = "정산 실패"


        val purchaseButton: AppCompatButton = resultDialog.findViewById(R.id.btn_after_purchase)
        purchaseButton.text = "확인"


        purchaseButton.setOnClickListener {

            resultDialog.dismiss()

        }

        resultDialog.show()
    }

    class GalleryFriendAdapter(
        private val data: List<EndPlanParticipantData>,
        private val planId: Int,
        private val notificationViewModel: NotificationViewModel,
        private val planName: String,
    ) :
        RecyclerView.Adapter<GalleryFriendAdapter.GalleryFriendViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryFriendViewHolder {
            val binding = ItemCardFriendCompleteBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return GalleryFriendViewHolder(binding)
        }

        override fun onBindViewHolder(holder: GalleryFriendViewHolder, position: Int) {
            holder.bind(data[position], planId)
        }

        override fun getItemCount(): Int {
            return data.size
        }

        inner class GalleryFriendViewHolder(private val binding: ItemCardFriendCompleteBinding) :
            RecyclerView.ViewHolder(binding.root) {

            private val imageView: ImageView = binding.ivImgCardFriend

            init {
                imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            }

            fun bind(participant: EndPlanParticipantData, planId: Int) {
                binding.participant = participant


                Glide.with(imageView)
                    .load(participant.profile)
                    .circleCrop()
                    .override(200, 200)
                    .priority(Priority.HIGH)
                    .into(imageView)


                val colorRes2 = when (participant.designation) {
                    0 -> R.color.orange1
                    1 -> R.color.violet1
                    else -> R.color.red1
                }
                val color2 = ContextCompat.getColor(imageView.context, colorRes2)
                ViewCompat.setBackgroundTintList(binding.btnJungsan, ColorStateList.valueOf(color2))

                // 만약 paymentAvailability가 false면
                if (!participant.paymentAvailability) {
                    // 버튼을 보이게하고
                    binding.btnJungsan.isVisible = true
                    // 독촉 메시지 이벤트 리스너
                    val title = "정산 요청 \uD83D\uDE20"
                    // 금액...있으면 좋을듯..?
                    val body = "[${planName}] 지각비 정산을 위한 보유금액(원)이 부족합니다"
                    val targetToken = participant.deviceToken
                    val planId2 = planId.toString()
                    val type = "toWalletFragment"

                    binding.btnJungsan.setOnClickListener {
                        binding.animationView.visibility = View.VISIBLE
                        binding.animationView.playAnimation()

                        notificationViewModel.singleFCMRequest(
                            body,
                            planId2,
                            targetToken,
                            title,
                            type
                        )

                        Toast.makeText(
                            imageView.context,
                            "${participant.nickname}님께 정산 요청을 보냈습니다.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                binding.executePendingBindings()
            }
        }


    }

}