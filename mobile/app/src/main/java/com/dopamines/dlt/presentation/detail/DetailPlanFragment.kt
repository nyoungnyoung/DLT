package com.dopamines.dlt.presentation.detail

import android.app.Activity
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.location.Location
import android.location.LocationManager

import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider

import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.dopamines.dlt.GlobalApplication
import com.dopamines.dlt.R

import com.dopamines.dlt.databinding.FragmentDetailPlanBinding
import com.dopamines.dlt.databinding.ItemCardFriendBinding
import com.dopamines.dlt.presentation.home.HomeViewModel
import com.dopamines.dlt.presentation.home.socketMessage
import com.dopamines.dlt.util.CustomWebSocketListener
import com.dopamines.dlt.util.GlobalWebSocket
import com.google.gson.Gson
import com.dopamines.dlt.presentation.auth.SharedPreferences
import com.dopamines.dlt.presentation.home.HomeActivity
import com.dopamines.dlt.presentation.notification.NotificationRepository
import com.dopamines.dlt.presentation.notification.NotificationViewModel
import com.unity3d.player.UnityPlayerActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.daum.mf.map.api.MapCircle
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import java.io.File
import java.io.FileOutputStream

import java.time.LocalDateTime

import java.time.format.DateTimeFormatter


class DetailPlanFragment : Fragment(), CustomWebSocketListener {

    private lateinit var binding: FragmentDetailPlanBinding
    private lateinit var detailAdapter: DetailPlanAdapter

    private lateinit var detailViewModel: DetailViewModel
    private lateinit var homeViewModel: HomeViewModel

    private lateinit var globalWebSocket: GlobalWebSocket
    private lateinit var notificationViewModel: NotificationViewModel

    // 카메라
    private lateinit var photoFile: File
    private lateinit var photoURI: Uri

    private var planId: Int? = null

    // 지도
    private var mapView: MapView? = null

    // 웹소켓 메시지 전송 시 json화
    val gson = Gson()

    // 유저 현재위치 관련 변수 설정
    private var locationManager: LocationManager? = null
    private var userNowLocation: Location? = null
    private var userY: Double? = null
    private var userX: Double? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // GlobalWebSocket 인스턴스 가져오기
        globalWebSocket = (requireActivity().application as GlobalApplication).globalWebSocket

        // 네비게이션바 숨겨주기
        val HomeActivity = activity as HomeActivity
        HomeActivity.HideBottomNavi(true)
    }

    override fun onResume() {
        super.onResume()
        // CustomWebSocketListener 등록
        globalWebSocket.addMessageListener(this)

        // 네비게이션바 숨겨주기
        val HomeActivity = activity as HomeActivity
        HomeActivity.HideBottomNavi(true)
    }

    override fun onPause() {
        super.onPause()
        // CustomWebSocketListener 제거
        globalWebSocket.removeMessageListener(this)

        // BottomNavBar 제거
        val HomeActivity = activity as HomeActivity
        HomeActivity.HideBottomNavi(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail_plan, container, false)

        val detailRepository = DetailRepository(requireContext())
        detailViewModel = DetailViewModel(detailRepository)
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        val notificationRepository = NotificationRepository(requireContext())
        notificationViewModel = NotificationViewModel(notificationRepository)

        // 네비게이션바 숨겨주기
        val HomeActivity = activity as HomeActivity
        HomeActivity.HideBottomNavi(true)


        // 디테일 페이지 들어오자마자 디테일 정보 detailViewModel에 저장해주기
        arguments?.let { bundle ->

            planId = bundle.getInt("planId", -1)
            Log.i("planId확인", planId.toString())
        }

        lifecycleScope.launch {
            planId?.let { detailViewModel.loadDetailPlan(it) }
        }

        // 디테일 페이지 들어오자마자 user정보 detailViewModel에 저장해주기
        lifecycleScope.launch {
            detailViewModel.getUserId()
        }

        // 뷰모델의 plan 변할 때
        detailViewModel.plan.observe(viewLifecycleOwner) { planDetail ->
            Log.i("planDetail 확인해보기", planDetail.toString())
            binding.planDetail = planDetail

            val planDateTime = LocalDateTime.parse("${planDetail.planDate}T${planDetail.planTime}")
            val formattedDateTime = planDateTime.format(DateTimeFormatter.ofPattern("yyyy.MM.dd(EEE) HH:mm"))


            binding.tvDatetimeCard?.text = formattedDateTime

            binding.tvLateCost.text = planDetail.cost.toString() + "원"

            val diffDay = planDetail.diffDay
            val dayText = if (diffDay != 0) "D$diffDay" else "D-DAY"

            binding.tvLastDay.text = dayText

            binding.detailCountFriend.tvContentCountPerson.text =
                planDetail.participantCount.toString()

            // 버튼 숨김 & 표시 처리
            binding.btnDetail.visibility = when {
                planDetail.state == 0 || planDetail.state == 1 || planDetail.isPhoto -> View.GONE
                else -> View.VISIBLE
            }

            binding.btnDetail.text = when {
                planDetail.state == 2 -> "게임 입장"
                planDetail.state == 3 || !planDetail.isPhoto -> "사진 업로드"
                else -> ""
            }

            binding.tvTitlePlanLocation.text = planDetail.location
            binding.tvContentPlanLocation.text = planDetail.address


            detailAdapter = DetailPlanAdapter(planDetail.participantList)
            binding.rvParticipateFriendList.adapter = detailAdapter
            detailAdapter.notifyDataSetChanged()

            // 카카오맵에 약속 위치 표시 & 줌 위치 변경 & 약속 위치 중심 반경 10m 원 그리기
            var zoomPoint = MapPoint.mapPointWithGeoCoord(planDetail.latitude, planDetail.longitude)
            mapView?.setMapCenterPointAndZoomLevel(zoomPoint, 0, true)
            var circle =
                MapCircle(zoomPoint, 10, Color.rgb(226, 94, 109), Color.argb(80, 254, 202, 206))
            mapView?.addCircle(circle)
            // 마커 추가
            val point = MapPOIItem()
            point.apply {
                itemName = planDetail.location
                mapPoint = MapPoint.mapPointWithGeoCoord(planDetail.latitude, planDetail.longitude)
                markerType = MapPOIItem.MarkerType.BluePin
                selectedMarkerType = MapPOIItem.MarkerType.RedPin
            }
            mapView?.addPOIItem(point)

            // 약속의 state가 1, 2일 때
            when (planDetail.state) {
                1, 2 ->
                    // 웹소켓 생성 API 요청 함수 호출
                    GlobalScope.launch {
                        detailViewModel.getPosition(planDetail.planId.toString())
                    }
            }
        }

        // GlobalApplication에서 GlobalWebSocket 인스턴스 가져오기
        val myApplication = activity?.application as GlobalApplication
        globalWebSocket = myApplication.globalWebSocket

        lifecycleScope.launch {
            delay(500)
            while (true) {
                delay(1000)

                // 1초마다 위치정보 새로 업데이트해주기 -> 함수 호출
                permissionCheck()
                Log.i("디테일에서 웹소켓 발신", "MOVE")

                // 웹소켓에 보낼 메시지 설정
                val message = socketMessage(
                    type = "MOVE",
                    roomId = detailViewModel.roomData.value?.roomId.toString(),
                    sender = detailViewModel.userInfo.value?.id.toString(),
                    nickname = detailViewModel.userInfo.value?.nickname.toString(),
                    profile = detailViewModel.userInfo.value?.profile.toString(),
                    message = "위치 정보를 보냅니다.",
                    latitude = detailViewModel.userY.value.toString(),
                    longitude = detailViewModel.userX.value.toString()
                )

                // json으로 변경해서 웹소켓에 메시지 전송해주기
                val sendMsg = gson.toJson(message)
                globalWebSocket.sendMessage(sendMsg)

            }

        }

        // Toobar 이름
        binding.signUpToolbar.title = "약속 상세"

        // 카카오맵 초기화
        mapView = MapView(context)
        binding.llPromiseMap.addView(mapView)

        // 카카오맵 & 스크롤뷰 터치이벤트 조정
        mapView!!.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> binding.svDetailPromise.requestDisallowInterceptTouchEvent(true)
                MotionEvent.ACTION_UP -> binding.svDetailPromise.requestDisallowInterceptTouchEvent(false)
            }
            false
        }

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signUpToolbar.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
        val clipboardManager =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        binding.ivCopy.setOnClickListener {
            detailViewModel.onCopyClicked(
                binding.tvContentPlanLocation.text.toString(),
                clipboardManager,
                requireContext()
            )
        }
        val sharedPreferences = context?.let { SharedPreferences(it) }
        val accessToken = sharedPreferences?.accessToken
        Log.i("ACCESSTOKEN", "${accessToken}")
        binding.btnDetail.setOnClickListener {
            detailViewModel.plan.observe(viewLifecycleOwner) { planDetail ->
                // 추후 사용자가 host인경우만 사진 촬영 기능
                if (planDetail.state == 3) {
                    checkCameraPermissionAndOpenCamera()

                }else if(planDetail.state == 2) {
                    Log.i("ACCESSTOKEN", "${accessToken}")

                    val intent = Intent(context, UnityPlayerActivity::class.java)
                    intent.putExtra("unity", "unity")
                    intent.putExtra("scene", "0")
                    intent.putExtra("accessToken", accessToken)
                    intent.putExtra("roomNumber", planDetail.planId)
                    Log.i("detail에서 itemId", "" + planDetail.planId)

                    startActivity(intent)
                }
            }
        }

        val picturesDirectory = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFiles = picturesDirectory?.listFiles()?.filter { it.isFile } ?: emptyList()

        // 이미지 파일 목록 출력
//        imageFiles.forEach { file ->
//            Log.d("사진확인", "Image file path: ${file.absolutePath}")
//        }

    }

    // recycler View
    class DetailPlanAdapter(private val data: List<Participant>) :
        RecyclerView.Adapter<DetailPlanAdapter.DetailPlanViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailPlanViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemCardFriendBinding.inflate(inflater, parent, false)
            return DetailPlanViewHolder(binding)
        }

        override fun onBindViewHolder(holder: DetailPlanViewHolder, position: Int) {
            val detailPlanData = data[position]
            holder.bind(detailPlanData)
        }

        override fun getItemCount(): Int {
            return data.size
        }

        inner class DetailPlanViewHolder(private val binding: ItemCardFriendBinding) :
            RecyclerView.ViewHolder(binding.root) {

            private val imageView: ImageView = binding.ivImgCardFriend

            init {
                imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            }

            fun bind(participant: Participant) {
                binding.participant = participant

                Glide.with(imageView)
                    .load(participant.profile)
                    .centerCrop()
                    .transform(CircleCrop())
                    .placeholder(R.drawable.backgournd_default) // 로딩 중에 표시할 이미지
                    .error(R.drawable.backgournd_default) // 로딩 실패 시 표시할 이미지
                    .into(imageView)
                binding.executePendingBindings()
            }
        }
    }


    // 권한 확인 및 카메라 앱 실행
    private val requestCameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openCamera()
            } else {
                Toast.makeText(requireContext(), "카메라 권한이 필요합니다", Toast.LENGTH_SHORT).show()
            }
        }

    private fun checkCameraPermissionAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            openCamera()
        } else {
            requestCameraPermission.launch(android.Manifest.permission.CAMERA)
        }
    }

    private val takePicture =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // 파일 경로를 이용해 Uri 객체 생성
                // file:///storage/emulated/0/DCIM/Camera/my_photo.jpg 형태
                // 여기서 api요청을 통해 db로 보내기
                val uri = Uri.fromFile(photoFile)

                Glide.with(this)
                    .asBitmap()
                    .load(uri)
                    .apply(
                        RequestOptions()
                            .format(DecodeFormat.PREFER_RGB_565) // 이미지 포맷 설정 (옵션)
                            .override(500, 500) // 이미지 크기 조정 (옵션)
                            .encodeFormat(Bitmap.CompressFormat.JPEG) // 이미지 포맷 변경 (옵션)
                            .encodeQuality(100) // 이미지 품질 설정 (0-100)
                    )
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                        ) {
                            // 압축된 이미지를 업로드하거나 처리합니다.
                            // resource 변수에 압축된 Bitmap 이미지가 전달됩니다.
                            val compressedUri = saveCompressedBitmap(resource)
                            detailViewModel.plan.observe(viewLifecycleOwner) { planDetail ->
                                val planId = planDetail.planId.toLong()
                                lifecycleScope.launch {
                                    Dispatchers.IO
                                    detailViewModel.uploadImage(planId, compressedUri)
                                }
                            }

                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                            // 이미지 로딩이 취소되었을 때 호출됩니다.
                        }
                    })
            }
        }

    private fun saveCompressedBitmap(bitmap: Bitmap): Uri {
        val outputDir = requireContext().cacheDir // 압축된 이미지를 저장할 디렉토리 선택 (예: 캐시 디렉토리)
        val outputFile = File.createTempFile("compressed_image", ".jpg", outputDir) // 임시 파일 생성

        // 이미지를 JPEG 형식으로 압축하여 파일에 저장
        val outputStream = FileOutputStream(outputFile)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        outputStream.close()

        return Uri.fromFile(outputFile) // 압축된 이미지의 Uri 반환
    }


    // 카메라 앱 실행
    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        // 카메라 앱으로부터 받아온 사진을 저장할 파일 생성
        photoFile = File(
            requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "${System.currentTimeMillis()}.jpg"
        )

        // 파일 경로를 URI로 변환
        photoURI = FileProvider.getUriForFile(
            requireContext(),
            "com.dopamines.dlt.fileprovider",
            photoFile
        )

        // 카메라 앱에 파일 저장 위치 알려주기
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)

        // 카메라 앱 실행
        takePicture.launch(intent)
    }

    // 권한확인 후 현재위치 받아오는 함수
    private fun permissionCheck() {
        // 권한이 있을 때
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // 현재위치 받아오기
            locationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            userNowLocation =
                locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            // 현재위치 위도 경도 HomeViewModel에 저장해주기
            userY = userNowLocation?.latitude
            detailViewModel.userY.value = userY
            userX = userNowLocation?.longitude
            detailViewModel.userX.value = userX

        } else {
            // 권한이 없을 때 Toast 띄워주기
            Toast.makeText(context, "현재 위치를 받아오려면 권한을 허용해주세요.", Toast.LENGTH_SHORT).show()
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(
                    android.Manifest.permission.ACCESS_COARSE_LOCATION,
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ),
                1
            )
        }
    }

    override fun onMessageReceived(message: String) {
        val msg = gson.fromJson(message, socketMessage::class.java)

        // ENTER & MOVE -> 좌표 데이터로 마커
        when (msg.type) {
            "ENTER" -> {
                Log.i("ENTER메시지를 받았습니다", msg.toString())
            }

            "MOVE" -> {
                Log.i("MOVE메시지를 받았습니다", msg.toString())

                val existingMarker = msg.nickname?.let { findMarkerByItemName(it) }
                if (existingMarker != null) {
                    // 기존 마커 제거
                    mapView?.removePOIItem(existingMarker)
                }


                if (msg.profile!= null) {
                    Glide.with(requireContext())
                        .asBitmap()
                        .load(msg.profile.toString())
                        .centerCrop()
                        .override(50, 50)
                        .transform(CircleCrop())
                        .into(object : CustomTarget<Bitmap>() {
                            override fun onResourceReady(
                                resource: Bitmap,
                                transition: Transition<in Bitmap>?
                            ) {
                                // 커스텀 마커 생성하기
                                val customMarker = MapPOIItem()
                                customMarker.apply {
                                    itemName = msg.nickname.toString()
                                    mapPoint = MapPoint.mapPointWithGeoCoord(
                                        msg.latitude!!.toDouble(),
                                        msg.longitude!!.toDouble()
                                    )
                                    markerType = MapPOIItem.MarkerType.CustomImage
                                    customImageBitmap = resource
                                }
                                mapView?.addPOIItem(customMarker)
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {
                                Log.i("마커추가실패", "마커추가 실패")
                            }
                        })
                } else {Log.i("마커추가실패", "프로필 이미지 URL이 null입니다.")}
             }

            "ARRIVE" -> {
                Log.i("ARRIVE메시지를 받았습니다", msg.toString())
                Toast.makeText(requireContext(), "${msg.nickname}님이 약속장소에 도착했습니다", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun findMarkerByItemName(itemName: String): MapPOIItem? {
        val poiItems = mapView?.poiItems
        if (poiItems != null) {
            for (item in poiItems) {
                if (item.itemName == itemName) {
                    return item
                }
            }
        }
        return null
    }
}






