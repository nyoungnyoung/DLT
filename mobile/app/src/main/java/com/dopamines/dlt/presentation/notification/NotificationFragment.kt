package com.dopamines.dlt.presentation.notification

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.dopamines.dlt.R
import com.dopamines.dlt.databinding.FragmentNotificationBinding
import com.dopamines.dlt.presentation.auth.LogInViewModel
import com.dopamines.dlt.presentation.gallery.GalleryDetailReviewViewModel
import com.dopamines.dlt.presentation.gallery.GalleryRepository
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging



class NotificationFragment : Fragment() {

    private lateinit var binding: FragmentNotificationBinding

    private lateinit var notificationViewModel: NotificationViewModel


    // 사용 예시
//    val serverKey =
//        "AAAA4pfPpNU:APA91bFGhl86w9sIy5g9bl0_C8a5ovh2jk5Kwk7PZuaihKxeub3F5gG53SJYT1dZatbifUcDV5UQ3vwDMG0wthYfloeKmxTvi549prPKIaBQ33Tci55P3pHmidjwGTaQZVlvX6A_SSnF"
    val deviceToken =
        "cab0Nrj9Sn-f4qztzc8EDm:APA91bHY-7o8o0CW8IERh0s5xPZhDBpVK5nbGDCj1jn1Wzj4vfj1kDZ9gUSmEkgXtuUi51d8-ZVrxsB2ox3PWw16cur_tta5IPvqjYDAd5-_xGs4wn9wjDW14mHSwH1QNtGhglFWUHUN"
    val deviceToken2 = "feSJxJeMRAenyVH_EKoSZE:APA91bGj9trw_SVf5Abl2PD7Xs652pe5e9nm3myHtL7x_c7Ogpy7Xkq8yg4-kbeBNZpqNQsBVHN37sKI8p9xkUEmhv7EYDlAZ1YbYjVcmdkbvIG-jPJrExjHlqNXsr0F6cXn18uzE7OH"
    val deviceToken3 = "dGmGBsmlQpa4z-nc0tmDPf:APA91bF4ARwte_MHykmPSrkvnnMC2O76vQshHh3fc-RmDAM8Bt7d3DQXMnhxdL4mQbZGkZImJC1GJaYWPZUPhjmiLnRf06aCWR-vWbvLdKIARwqMWVJ914atvMts3lR5HNw3i1TbZR5P"

    val tokenList = listOf(deviceToken,deviceToken2,deviceToken3)


//    val title = "DLT"
//    val body = "왜 안보여"

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK 및 앱은 알림을 표시할 수 있습니다.
        } else {
            // TODO: 사용자에게 앱이 알림을 표시하지 않을 것임을 알리는 메시지를 보여줍니다.
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_notification, container, false)


        askNotificationPermission()

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            Log.i("뭐야!!!!!!!!!!1", "ㅠ")
            if (!task.isSuccessful) {
                Log.w(ContentValues.TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            val msg = "FCM Registration Token: $token"
            Log.d(ContentValues.TAG, msg)
        })


        val groupName = "newnewnew"

        val key = "APA91bFcg3cC9k1RPR9--HPLRIdEhmytUkms3qYU0Qy6d4LKh1MLaRrL8al7y6Q0WnRVM-QFO8kkUT2wQHxddMT5NyBdTbsahff2MhQEJ6E7rJnlr9wSszE"


        binding.btnNotification.setOnClickListener {
//            sendFCMRequest( deviceToken, title, body)

            sendGroupRequest(key, "으악", "알림")
        }
        binding.btnNotificationGroup.setOnClickListener {
            notificationRequest(groupName,tokenList)
        }



        binding.btnNotificationSubscribe.setOnClickListener {

            // 구독하기
            Firebase.messaging.subscribeToTopic("weather")
                .addOnCompleteListener { task ->
                    var msg = "Subscribed"
                    if (!task.isSuccessful) {
                        msg = "Subscribe failed"
                    }
                    Log.d("WEATHER", msg)
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                }

        }



        // 알림 복붙할거
        val notificationRepository = NotificationRepository(requireContext())
        notificationViewModel = NotificationViewModel(notificationRepository)

        // 약속 임박
        binding.btnNotification1.setOnClickListener {
            //indent필요업음

            val title = "[약속 임박 알림]"
            val body = "약속이 30분 남았습니다!"
            val planId = "3"
            val topic = "3"
            val type = "to"
            notificationViewModel.sendTopicPush(body, planId, title, topic, type)
        }

        // 게임 시작
        binding.btnNotification2.setOnClickListener {
            //detail로 가야함, 혹은 게임시작 트리거 가능?
            val title = "[게임 시작 알림]"
            val body = "지각비 쟁탈전이 시작됩니다!"
            val planId = "3"
            val topic = "3"
            val type = "toDetailPlanFragment"
            notificationViewModel.sendTopicPush(body, planId, title, topic, type)
        }


        // 약속 기록
        binding.btnNotification3.setOnClickListener {
            //detail로 가야함
            val title = "[약속 기록 알림]"
            val body = "사진으로 오늘의 기록을 남겨보세요"
            val planId = "3"
            val topic = "3"
            val type = "toDetailPlanFragment"
            notificationViewModel.sendTopicPush(body, planId, title, topic, type)
        }

        val name = "누구누구"
        // 친구 도착
        binding.btnNotification4.setOnClickListener {
            val title = "[친구 도착 알림]"
            val body = "${name}님이 도착하였습니다."
            val planId = "4"
            val topic = "4"
            val type = "toDetailPlanFragment"
            notificationViewModel.sendTopicPush(body, planId, title, topic, type)
        }


        binding.icNotificationToolbar.title = "알림"


        binding.icNotificationToolbar.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        return binding.root
    }


    private fun askNotificationPermission() {
        Log.i("DP!!!!!!!!!!!!1", "?")
        // API 레벨 33(TIRAMISU) 이상인 경우에만 필요합니다.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this.requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {

                Log.i("DP!!!!!!!!!!!!1", "1")
                // FCM SDK 및 앱은 알림을 표시할 수 있습니다.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: 사용자에게 POST_NOTIFICATION 권한을 부여함으로써 활성화되는 기능에 대해 설명하는 교육용 UI를 표시합니다.
                //       이 UI는 사용자에게 "확인"과 "거부" 버튼을 제공해야 합니다. 사용자가 "확인"을 선택하면 권한을 요청합니다.
                //       사용자가 "거부"를 선택하면 알림 없이 계속 진행할 수 있도록 허용합니다.

                Log.i("DP!!!!!!!!!!!!1", "2")
            } else {
                // 권한을 직접 요청합니다.
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                Log.i("DP!!!!!!!!!!!!1", "3")
            }
        }
    }


}




data class FCMData(
    val title: String,
    val message: String
)


