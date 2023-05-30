package com.dopamines.dlt.presentation.home

import android.app.Activity
import android.app.Application
import android.app.appsearch.GlobalSearchSession
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.util.Log
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dopamines.dlt.GlobalApplication
import com.dopamines.dlt.R
import com.dopamines.dlt.util.GlobalWebSocket
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.unity3d.player.UnityPlayerActivity

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.daum.mf.map.api.MapCircle
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import okhttp3.OkHttp
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.lang.reflect.Modifier
import java.lang.reflect.Type
import java.time.LocalDate
import kotlin.math.abs

class HomeMultiAdapter(private val mContext: Context?, private val itemList: ArrayList<HomeData>, private val viewModel: HomeViewModel, private val globalWebSocket: GlobalWebSocket, private val accessToken: String?): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return itemList[position].state
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View?
        return when(viewType) {
            0 -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_home_default, parent, false)
                DefaultViewHolder(view)
            }
            1 -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_home_active, parent, false)
                ActiveViewHolder(view)
            }
            2 -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_home_active, parent, false)
                ActiveViewHolder(view)
            }
            else -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_home_default, parent, false)
                DefaultViewHolder(view)
            }
        }
    }

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(itemList[position].state) {
            0 -> {
                (holder as DefaultViewHolder).bind(itemList[position])
                holder.setIsRecyclable(false)
            }
            1 -> {
                (holder as ActiveViewHolder).bind(itemList[position])
                holder.setIsRecyclable(false)
            }
            2 -> {
                (holder as ActiveViewHolder).bind(itemList[position])
                holder.setIsRecyclable(false)
            }
            else -> {
                (holder as DefaultViewHolder).bind(itemList[position])
                holder.setIsRecyclable(false)
            }
        }
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        if (holder is ActiveViewHolder) {
            // 카카오맵 다시 추가해주기
//            holder.onMapAttached()
        }
    }


    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
//        if (holder is ActiveViewHolder) {
//            holder.onMapRecycled()
//        }
//        // 수정: holder.itemView를 기준으로 address 뷰 참조
//        val addressLayout: LinearLayout = holder.itemView.findViewById(R.id.ll_promise_active_map)
//        addressLayout.removeAllViews()
    }

    interface OnItemClickListener {
        fun onItemClick(item: HomeData)
    }

    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }
    inner class DefaultViewHolder(view: View): RecyclerView.ViewHolder(view) {

        private val diffHours: TextView = itemView.findViewById(R.id.tv_default_diff_hours)
        private val title: TextView = itemView.findViewById(R.id.tv_default_title)
        private val planTime: TextView = itemView.findViewById(R.id.tv_default_planTime)
        private val location: TextView = itemView.findViewById(R.id.tv_default_location)
        private val participantCount: TextView = itemView.findViewById(R.id.tv_default_participantCount)

        private val participantRecyclerView: RecyclerView = itemView.findViewById(R.id.rv_home_people_list)
        private val participantAdapter: HomeparticipantAdapter = HomeparticipantAdapter(ArrayList())

        init {
            itemView.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = itemList[position]
                    onItemClickListener?.onItemClick(item)
                }
            }
            // 내부 리사이클러뷰 설정
            participantRecyclerView.layoutManager = LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
            participantRecyclerView.adapter = participantAdapter
        }

        fun bind(item: HomeData) {

            // diffHours 처리
            var diff = ""
            val todayYear = CalendarDay.today().year.toString()
            val todayMonth = if (CalendarDay.today().month < 10) "0${CalendarDay.today().month}" else CalendarDay.today().month.toString()
            val todayDay = if (CalendarDay.today().day < 10) "0${CalendarDay.today().day}" else CalendarDay.today().day.toString()
            val today = "${todayYear}-${todayMonth}-${todayDay}"
            Log.i("오늘 날짜", "${todayYear}-${todayMonth}-${todayDay}")
            val selectedYear = item.planDate.substring(0,4)
            val selectedMonth = item.planDate.substring(5,7)
            val selectedDay = item.planDate.substring(8,10)
            Log.i("선택 날짜", "${selectedYear}-${selectedMonth}-${selectedDay}")


            // diffHours, diffMinutes 양수면 지난 약속
            if (item.diffHours > 0) {
                // 약속날짜가 오늘날짜와 다르고, 오늘 날짜와 달이 다르면 달 단위로 표시
                diff = if (item.planDate != today && selectedMonth != todayMonth) "${todayMonth.toInt()-selectedMonth.toInt()}달 전"
                // 약속날짜가 오늘날짜가 아니면 일 단위로 표시
                else if (item.planDate != today) "${todayDay.toInt()-selectedDay.toInt()}일 전"
                // 24시간 미만이면 시간으로 표시
                else "${item.diffHours}시간 전"
//                else if (item.diffHours < 24) "${item.diffHours}시간 전"
            }
            // diffHours가 0, diffMinutes <= 60이면 분 단위로 표시
            if (item.diffHours == 0 && item.diffMinutes <= 60) diff = "${item.diffMinutes}분 전"

            // diffHours 혹은 diffMinutes가 음수면 미래의 약속
            if (item.diffHours < 0 || item.diffMinutes < 0) {
                diff = if (item.diffHours == 0 && abs(item.diffMinutes) > 0) "${abs(item.diffMinutes)+1}분 후"
                else {
                    // 약속 날짜가 오늘 날짜와 다르고, 오늘 날짜와 달이 다르면 달 단위로 표시
                    if (item.planDate != today && selectedMonth != todayMonth) "${selectedMonth.toInt()-todayMonth.toInt()}달 후"
                    // 약속 날짜가 오늘 날짜가 아니면 일 단위로 표시
                    else if (item.planDate != today) "${selectedDay.toInt()-todayDay.toInt()}일 후"
                    else "${abs(item.diffHours)}시간 후"
//                    else if (abs(item.diffHours) < 24) "${abs(item.diffHours)}시간 후"
                }
            }

            val time = item.planTime.split(":")[0].toInt()
            val minute = item.planTime.split(":")[1].toInt()
            var promise = ""

            // planTime "(오전/오후) H시 M분" 형태로 변경
            promise = if (time == 12) {
                if (minute != 0) "오후 ${time}시 ${minute}분"
                else "오후 ${time}시"
            } else {
                if (time == 0) {
                    if (minute != 0) "오전 ${time+12}시 ${minute}분"
                    else "오전 ${time+12}시"
                } else {
                    if (time < 12) {
                    if (minute != 0) "오전 ${time}시 ${minute}분"
                    else "오전 ${time}시"
                } else {
                    if (minute != 0) "오후 ${time-12}시 ${minute}분"
                    else "오후 ${time-12}시"
                }
                }
            }



            diffHours.text = diff
            title.text = item.title
            planTime.text = promise
            location.text = item.location
            participantCount.text = "${item.participantCount}명"

            // 참여자 프로필 사진 리사이클러뷰 데이터 설정
            participantAdapter.participantList = item.participantList
            participantAdapter.notifyDataSetChanged()
        }

    }

    inner class ActiveViewHolder(view: View): RecyclerView.ViewHolder(view) {

        // 카카오맵 띄워줄 mapView 변수 설정
        // private var mapView: MapView? = null

        private val diffHours: TextView = itemView.findViewById(R.id.tv_active_diff_hours)
        private val title: TextView = itemView.findViewById(R.id.tv_active_title)
        private val planTime: TextView = itemView.findViewById(R.id.tv_active_planTime)
        private val location: TextView = itemView.findViewById(R.id.tv_active_location)
        private val participantCount: TextView =
            itemView.findViewById(R.id.tv_active_participantCount)
//        private val address: LinearLayout = itemView.findViewById(R.id.ll_promise_active_map)

        private val participantRecyclerView: RecyclerView =
            itemView.findViewById(R.id.rv_home_people_list)
        private val participantAdapter: HomeparticipantAdapter = HomeparticipantAdapter(ArrayList())

        // 유저 현재위치 관련 변수 설정
        private var locationManager : LocationManager? = null
        private var userNowLocation: Location? = null
        private var userY: Double? = null
        private var userX: Double? = null

        // 게임 입장 버튼
        val btn: AppCompatButton = view.findViewById(R.id.btn_promise_game_in)

        // 웹소켓 메시지 전송 시 json화
        val gson = Gson()


//        // 웹소켓 통신을 위한 변수 설정
//        val request = Request.Builder()
//            .url("ws://k8d209.p.ssafy.io:8080/ws/position")
//            .build()
//
//        val listener = object: WebSocketListener() {
//            override fun onOpen(webSocket: WebSocket, response: Response) {
//                super.onOpen(webSocket, response)
//
//                Log.i("Home에서 WebSocket", "열렸습니다.")
//
//                val gson = Gson()
//
//                viewModel.viewModelScope.launch {
////                    delay(500)
////                    while (true) {
////                        delay(1000)
////
////                        // 1초마다 위치정보 새로 업데이트해주기 -> 함수 호출
////                        permissionCheck()
////
////                        val message = socketMessage(
////                            type = "MOVE",
////                            roomId = viewModel.roomData.value?.roomId.toString(),
////                            sender = viewModel.userInfo.value?.id.toString(),
////                            profile = viewModel.userInfo.value?.profile.toString(),
////                            message = "위치 정보를 보냅니다.",
////                            latitude = viewModel.userY.value.toString(),
////                            longitude = viewModel.userX.value.toString()
////                        )
////
////                        val sendMsg = gson.toJson(message)
////                        webSocket.send(sendMsg)
////                    }
//
//                }
//            }
//
//            override fun onMessage(webSocket: WebSocket, text: String) {
//                // 웹소켓으로부터 문자열 메시지를 받음
//                super.onMessage(webSocket, text)
//
//                Log.i("HOme에서 WebSocket", "Message 수신")
//                val gson = Gson()
//                val message = gson.fromJson(text, socketMessage::class.java)
//
//
//                // ENTER & MOVE -> 좌표 데이터로 마커
//                when (message.type) {
//                    "ENTER" -> {Log.i("ENTER메시지를 받았습니다", message.toString())}
//                    "MOVE" -> {Log.i("MOVE메시지를 받았습니다", "열라 뽕 따이다 새끼야" + message.toString())}
//                    "ARRIVE" -> {Log.i("ARRIVE메시지를 받았습니다", message.toString())}
//                }
//            }
//
//        }
//
//        val client = OkHttpClient()
//        val websocket = client.newWebSocket(request, listener)


        init {
            itemView.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = itemList[position]
                    onItemClickListener?.onItemClick(item)
                }
            }
            // 내부 리사이클러뷰 설정
            participantRecyclerView.layoutManager =
                LinearLayoutManager(itemView.context, LinearLayoutManager.HORIZONTAL, false)
            participantRecyclerView.adapter = participantAdapter

        }

        fun bind(item: HomeData) {

            // state가 1일때는 btn 보이지 않게 해야함
            btn.visibility = when (item.state) {
                1 -> View.GONE
                else -> View.VISIBLE
            }

            // diffHours 처리
            var diff = ""
            val todayYear = CalendarDay.today().year.toString()
            val todayMonth = if (CalendarDay.today().month < 10) "0${CalendarDay.today().month}" else CalendarDay.today().month.toString()
            val todayDay = if (CalendarDay.today().day < 10) "0${CalendarDay.today().day}" else CalendarDay.today().day.toString()
            val today = "${todayYear}-${todayMonth}-${todayDay}"
            val selectedYear = item.planDate.substring(0,4)
            val selectedMonth = item.planDate.substring(5,7)
            val selectedDay = item.planDate.substring(8,10)

            // diffHours, diffMinutes 양수면 지난 약속
            if (item.diffHours > 0) {
                // 약속날짜가 오늘날짜와 다르고, 오늘 날짜와 달이 다르면 달 단위로 표시
                diff = if (item.planDate != today && selectedMonth != todayMonth) "${todayMonth.toInt()-selectedMonth.toInt()}달 전"
                // 약속날짜가 오늘날짜가 아니면 일 단위로 표시
                else if (item.planDate != today) "${todayDay.toInt()-selectedDay.toInt()}일 전"
                // 24시간 미만이면 시간으로 표시
                else "${item.diffHours}시간 전"
//                else if (item.diffHours < 24) "${item.diffHours}시간 전"
            }

            // diffHours가 0, diffMinutes <= 60이면 분 단위로 표시
            if (item.diffHours == 0 && item.diffMinutes <= 60) diff = "${item.diffMinutes}분 전"

            // state가 2이면 게임 중 표시
            if (item.state == 2) {
                diff = "게임 중"
            }

            // diffHours 혹은 diffMinutes가 음수면 미래의 약속
            if (item.diffHours < 0 || item.diffMinutes < 0) {
                diff = if (item.diffHours == 0 && abs(item.diffMinutes) > 0) "${abs(item.diffMinutes)+1}분 후"
                else {
                    // 약속 날짜가 오늘 날짜와 다르고, 오늘 날짜와 달이 다르면 달 단위로 표시
                    if (item.planDate != today && selectedMonth != todayMonth) "${selectedMonth.toInt()-todayMonth.toInt()}달 후"
                    // 약속 날짜가 오늘 날짜가 아니면 일 단위로 표시
                    else if (item.planDate != today) "${selectedDay.toInt()-todayDay.toInt()}일 후"
                    else "${abs(item.diffHours)}시간 후"
//                    else if (abs(item.diffHours) < 24) "${abs(item.diffHours)}시간 후"
                }
            }

            val time = item.planTime.split(":")[0].toInt()
            val minute = item.planTime.split(":")[1].toInt()
            var promise = ""

            // planTime "(오전/오후) H시 M분" 형태로 변경
            promise = if (time == 12) {
                if (minute != 0) "오후 ${time}시 ${minute}분"
                else "오후 ${time}시"
            } else {
                if (time == 0) {
                    if (minute != 0) "오전 ${time + 12}시 ${minute}분"
                    else "오전 ${time + 12}시"
                } else {
                    if (time < 12) {
                        if (minute != 0) "오전 ${time}시 ${minute}분"
                        else "오전 ${time}시"
                    } else {
                        if (minute != 0) "오후 ${time - 12}시 ${minute}분"
                        else "오후 ${time - 12}시"
                    }
                }
            }

            diffHours.text = "$diff"
            title.text = item.title
            planTime.text = promise
            location.text = item.location
            participantCount.text = "${item.participantCount}명"

            // 참여자 프로필 사진 리사이클러뷰 데이터 설정
            participantAdapter.participantList = item.participantList
            participantAdapter.notifyDataSetChanged()

            // 홈화면에 Active된 어뎁터 있을 때 1초에 한번 씩 웹소켓에 메시지 전송
//            viewModel.viewModelScope.launch {
//                delay(500)
//                while (true) {
//                    delay(1000)
//
//                    // 1초마다 위치정보 새로 업데이트해주기 -> 함수 호출
//                    permissionCheck()
//
//                    // 웹소켓에 보낼 메시지 설정
//                    val message = socketMessage(
//                        type = "MOVE",
//                        roomId = viewModel.roomData.value?.roomId.toString(),
//                        sender = viewModel.userInfo.value?.id.toString(),
//                        nickname = viewModel.userInfo.value?.nickname.toString(),
//                        profile = viewModel.userInfo.value?.profile.toString(),
//                        message = "위치 정보를 보냅니다.",
//                        latitude = viewModel.userY.value.toString(),
//                        longitude = viewModel.userX.value.toString()
//                    )
//
//                    // json으로 변경해서 웹소켓에 메시지 전송해주기
//                    val sendMsg = gson.toJson(message)
//                    globalWebSocket.sendMessage(sendMsg)
//
//                }
//
//            }

            permissionCheck()

            // 웹소켓에 메시지 보내보기
            val gson = Gson()

            val message = socketMessage(
                type = "ENTER",
                roomId = item.planId.toString(),
                sender = viewModel.userInfo.value?.id.toString(),
                profile = viewModel.userInfo.value?.profile.toString(),
                message = "위치 정보를 보냅니다.",
                nickname = viewModel.userInfo.value?.nickname.toString(),
                latitude = viewModel.userY.value.toString(),
                longitude = viewModel.userX.value.toString()
            )

            val sendMsg = gson.toJson(message)

            Log.i("홈화면 sendMsg", sendMsg.toString())
            globalWebSocket.sendMessage(sendMsg)



            btn.setOnClickListener{
                Log.i("ACCESSTOKEN", "${accessToken}")

                val intent = Intent(mContext, UnityPlayerActivity::class.java)
                intent.putExtra("unity", "unity")
                intent.putExtra("scene", "0")
                intent.putExtra("accessToken", accessToken)
                Log.i("home에서 itemId", "" + item.planId)
                intent.putExtra("roomNumber", item.planId)

                mContext?.startActivity(intent)
            }
        }
//        fun onMapRecycled() {
//            // MapView 제거
//            if (mapView != null) {
//                address.removeView(mapView)
//                mapView?.onSurfaceDestroyed()
//                mapView = null
//            }
//        }

//        fun onMapAttached() {
//            // MapView 추가
//            if (mapView?.parent == null) {
//                address.addView(mapView)
//            }
//        }

        // 권한확인 후 현재위치 받아오는 함수
        private fun permissionCheck() {
            val context = itemView.context
            // 권한이 있을 때
            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // 현재위치 받아오기
                locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                userNowLocation = locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                // 현재위치 위도 경도 HomeViewModel에 저장해주기
                userY = userNowLocation?.latitude
                viewModel.userY.value = userY
                userX = userNowLocation?.longitude
                viewModel.userX.value = userX

                // 나중에 주석처리 하거나 삭제하기
//                Log.i("userY", viewModel.userY.value.toString())
//                Log.i("userX", viewModel.userX.value.toString())

            } else {
                // 권한이 없을 때 Toast 띄워주기
                Toast.makeText(context, "위치 권한을 허용해주세요.", Toast.LENGTH_SHORT).show()
//                ActivityCompat.requestPermissions(
//                    context as Activity,
//                    arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION,
//                        android.Manifest.permission.ACCESS_FINE_LOCATION,
//                        android.Manifest.permission.ACCESS_BACKGROUND_LOCATION),
//                    1)
            }
        }
    }
}
