import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.dopamines.dlt.presentation.notification.NotificationViewModel
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

//class MyWorker(context: Context, params: WorkerParameters, private val notificationViewModel: NotificationViewModel) : Worker(context, params) {
//
//
//    override fun doWork(): Result {
//
//        Log.i("WORKMANAGER", "작동")
//        // 특정 시간 설정
////        val targetDateTime = LocalDateTime.parse("2023-05-18T06:33:00") // 특정 시간 예시
//
//
//        return Result.success()
//    }
//
//
//}


//class LogWorker(appContext: Context, val workerParams: WorkerParameters, )
//    : Worker(appContext, workerParams) {
//    override fun doWork(): Result {
//        // 작업을 정의합니다
//        Log.d("LogWorker", "Worker : ${workerParams.id}, ${workerParams.tags}")
//
//        val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
//        val targetDateTime = LocalDateTime.parse("2023-05-18T10:43:20", dateTimeFormatter)
//            .atZone(ZoneId.of("Asia/Seoul"))
//            .toLocalDateTime()
//
//        // 현재 시간
//        val currentDateTime = LocalDateTime.now()
//
//        // 특정 시간의 1시간 전
//        val oneHourBeforeDateTime = targetDateTime.minusHours(1)
//
//        // 특정 시간의 1시간 후
//        val oneHourAfterDateTime = targetDateTime.plusHours(1)
//
//        // 현재 시간이 특정 시간의 1시간 전인지 비교
//        if (currentDateTime == oneHourBeforeDateTime) {
//            // 특정 시간의 1시간 전에 작업 실행
//            sendNotification("[약속 임박 알림]")
//        }
//
//        // 현재 시간이 특정 시간과 같은지 비교
//        if (currentDateTime == targetDateTime) {
//            // 특정 시간에 작업 실행
//            sendNotification("[게임 시작 알림]")
//        }
//
//        // 현재 시간이 특정 시간의 1시간 후인지 비교
//        if (currentDateTime == oneHourAfterDateTime) {
//            // 특정 시간의 1시간 후에 작업 실행
//            sendNotification("[약속 기록 알림]")
//        }
//
//
//        // 결과와 함께 work가 성공적으로 끝났는지 나타냄
//        return Result.success()
//    }
//
//    private fun sendNotification(message: String) {
//        // FCM 메시지 전송 로직
//        // TODO: FCM 메시지 전송 코드 작성
//
//        // 이미 작성된 viewmodel에서 FCM 관련 로직을 사용하거나 호출할 수 있습니다.
//        // notificationViewModel.sendTopicPush(...)
//
//        when (message) {
//            "[약속 임박 알림]" -> {
//
//                val title = "[약속 임박 알림]"
//                val body = "약속이 30분 남았습니다!"
//                val planId = "3"
//                val topic = "3"
//                val type = "to"
//                notificationViewModel.sendTopicPush(body, planId, title, topic, type)
//            }
//            "[게임 시작 알림]" -> {
//                //detail로 가야함, 혹은 게임시작 트리거 가능?
//                val title = "[게임 시작 알림]"
//                val body = "지각비 쟁탈전이 시작됩니다!"
//                val planId = "20"
//                val topic = "20"
//                val type = "toDetailPlanFragment"
//                notificationViewModel.sendTopicPush(body, planId, title, topic, type)
//            }
//            "[약속 기록 알림]" -> {
//                //detail로 가야함
//                val title = "[약속 기록 알림]"
//                val body = "사진으로 오늘의 기록을 남겨보세요"
//                val planId = "20"
//                val topic = "20"
//                val type = "toDetailPlanFragment"
//                notificationViewModel.sendTopicPush(body, planId, title, topic, type)
//            }
//        }
//
//
//        // 알림 메시지 출력
//        Log.d("Notification", message)
//    }
//}