package com.dopamines.dlt.presentation.gallery


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.net.Uri

import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment

import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat

import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

import com.dopamines.dlt.R
import com.dopamines.dlt.databinding.CommentGroupDateBinding
import com.dopamines.dlt.databinding.FragmentGalleryDetailReviewBinding
import com.dopamines.dlt.presentation.auth.Constants
import com.dopamines.dlt.presentation.auth.SharedPreferences
import com.dopamines.dlt.presentation.detail.ApiService
import com.dopamines.dlt.presentation.detail.DetailRepository
import com.dopamines.dlt.presentation.detail.DetailViewModel
import com.dopamines.dlt.presentation.home.HomeActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat

import java.util.Locale


class GalleryDetailReviewFragment : Fragment() {

    private lateinit var binding: FragmentGalleryDetailReviewBinding
    private lateinit var commentBinding: CommentGroupDateBinding

    private var planId: Int? = null
    private var currentUserNickname: String? = null

    private lateinit var galleryDetailReviewViewModel: GalleryDetailReviewViewModel

    private lateinit var detailViewModel: DetailViewModel
    private lateinit var photoFile: File
    private lateinit var photoURI: Uri


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
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_gallery_detail_review,
            container,
            false
        )
        commentBinding =
            DataBindingUtil.inflate(inflater, R.layout.comment_group_date, container, false)

        commentBinding.rvCommentList.layoutManager = LinearLayoutManager(requireContext())

        val galleryRepository = GalleryRepository(requireContext())
        galleryDetailReviewViewModel = GalleryDetailReviewViewModel(galleryRepository)
        val detailRepository = DetailRepository(requireContext())
        detailViewModel = DetailViewModel(detailRepository)

        arguments?.let { bundle ->
            planId = bundle.getInt("planId", -1)
        }


        // 사진
        planId?.let { galleryDetailReviewViewModel.getGalleryDetailPhoto(it) }


        binding.ivOverlayCamera.setOnClickListener {
            checkCameraPermissionAndOpenCamera()
        }

        // 사진 관찰하는 부분
        galleryDetailReviewViewModel.photoUrl.observe(viewLifecycleOwner) { photoUrl ->
            if (photoUrl != null) {
                binding.ivOverlayCamera.isVisible = false
                Glide.with(requireContext())
                    .load(photoUrl)
                    .into(binding.ivGalleryDetailPhoto)
            } else {
                binding.ivOverlayCamera.isVisible = true
            }
        }

        galleryDetailReviewViewModel.registerTime.observe(viewLifecycleOwner) { registerTime ->
            val planTime = registerTime

            val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.KOREA)
            val date = planTime?.let { dateFormat.parse(it) }

            val newDateFormat = SimpleDateFormat("yyyy. MM.dd (E) HH:mm", Locale.KOREA)
            val dateString = newDateFormat.format(date)

            binding.tvGalleryDetailDate.text = dateString
        }



        // 업데이트 될때마다 호출
        planId?.let {
            galleryDetailReviewViewModel.getComments(it)
            binding.btnReviewInput.setOnClickListener {
                val content = binding.etReviewInputEdit.text.toString()
                if (content.isNotEmpty()) {
                    lifecycleScope.launch {
                        galleryDetailReviewViewModel.createComment(planId!!, content)
                        galleryDetailReviewViewModel.checkResponse.observe(viewLifecycleOwner) { res ->
                            if (res == true) {
                                galleryDetailReviewViewModel.getComments(planId!!)
                                binding.etReviewInputEdit.text.clear()
                                // 댓글작성창 내리기
                                val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                                inputMethodManager.hideSoftInputFromWindow(binding.etReviewInputEdit.windowToken, 0)

                            }
                        }
                    }
                }


            }
        }


        // 닉네임
        planId?.let { galleryDetailReviewViewModel.getProfile(it) }

        galleryDetailReviewViewModel.nickname.observe(viewLifecycleOwner) { nickname ->
            currentUserNickname = nickname
        }



        planId?.let {
            galleryDetailReviewViewModel.getComments(it)
            galleryDetailReviewViewModel.comments.observe(viewLifecycleOwner) { commentDataList ->

                binding.rvTotalList.adapter =

                        GroupedCommentAdapter(
                            commentDataList,
                            this
                        )

                binding.rvTotalList.layoutManager = LinearLayoutManager(requireContext())

                // 댓글 데이터 업데이트 후 어댑터에 변경 사항 알리기
                binding.rvTotalList.adapter?.notifyDataSetChanged()
            }
        }



        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()

        val HomeActivity = activity as HomeActivity
        HomeActivity.HideBottomNavi(false)
    }

    override fun onPause() {
        super.onPause()

    }



    private fun onDeleteCommentClicked(commentId: Int) {
        galleryDetailReviewViewModel.viewModelScope.launch {
            galleryDetailReviewViewModel.deleteComment(commentId)
            galleryDetailReviewViewModel.checkDeleteResponse.observe(viewLifecycleOwner) { res ->
                if (res == true) {
                    galleryDetailReviewViewModel.getComments(planId!!)
                }
            }
        }
    }




    class CommentAdapter(
        private val commentList: List<Comment>,
        private val galleryDetailReviewFragment: GalleryDetailReviewFragment
    ) :
        RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

        private var selectedItemPosition: Int = RecyclerView.NO_POSITION



        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
            val itemView =
                LayoutInflater.from(parent.context).inflate(R.layout.comment_default, parent, false)
            return CommentViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
            val comment = commentList[position]
            holder.bind(comment)
        }

        override fun getItemCount(): Int = commentList.size

        inner class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val nicknameTextView: TextView = itemView.findViewById(R.id.tv_comment_nickname)
            val contentTextView: TextView = itemView.findViewById(R.id.tv_comment_content)
            val updateTimeTextView: TextView = itemView.findViewById(R.id.tv_comment_time)
            val profileImageView: ImageView = itemView.findViewById(R.id.iv_comment_profile)
            val deleteButton: AppCompatImageButton = itemView.findViewById(R.id.btn_review_delete)


            init {
                // 버튼 초기 상태 설정 (숨김)
                deleteButton.visibility = View.GONE
                val sharedPreferences = itemView.context.getSharedPreferences("tokenPref", Context.MODE_PRIVATE)
                val nickname = sharedPreferences.getString("nickname", null)

                // 버튼을 꾹 눌렀을 때 동작 처리
                itemView.setOnLongClickListener {
                    val position = bindingAdapterPosition
                    if (position != RecyclerView.NO_POSITION) {
                        val comment = commentList[position]
                        if (comment.nickName == nickname) {
                            toggleDeleteButtonVisibility(position)
                        }
                    }
                    true
                }

                // 아이템을 제외한 다른 곳을 터치했을 때 동작 처리
                itemView.setOnTouchListener { _, event ->
                    if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                        if (deleteButton.visibility == View.VISIBLE && !isTouchInsideDeleteButton(
                                event
                            )
                        ) {
                            hideDeleteButton()
                        }
                    }
                    false
                }


            }

            fun bind(comment: Comment) {
                val updateTime = comment.updateTime.split("T")[1].substring(0, 5)
                updateTimeTextView.text = updateTime
                nicknameTextView.text = comment.nickName
                contentTextView.text = comment.content
                comment.commentId

                if (comment.profile != null) {
                    Glide.with(profileImageView)
                        .load(comment.profile)
                        .transform(CircleCrop())
                        .into(profileImageView)
                } else {
                    Glide.with(profileImageView)
                        .load(R.drawable.test_image)
                        .transform(CircleCrop())
                        .into(profileImageView)
                }

                // 선택된 아이템의 삭제 버튼 가시성 설정
                deleteButton.visibility = if (bindingAdapterPosition == selectedItemPosition) {
                    View.VISIBLE
                } else {
                    View.GONE
                }

                // 삭제 버튼 클릭 이벤트 처리
                deleteButton.setOnClickListener {
                    Log.i("DELTEBU", "do")
                    val commentId = comment.commentId

                    commentId.let {
                        galleryDetailReviewFragment.onDeleteCommentClicked(commentId)
                    }
                }
            }

            private fun toggleDeleteButtonVisibility(position: Int) {
                if (position == selectedItemPosition) {
                    selectedItemPosition = RecyclerView.NO_POSITION
                } else {
                    selectedItemPosition = position
                }
                notifyDataSetChanged()
            }

            private fun hideDeleteButton() {
                deleteButton.visibility = View.GONE
            }

            private fun isTouchInsideDeleteButton(event: MotionEvent): Boolean {
                val x = event.x
                val y = event.y
                val buttonRect = Rect()
                deleteButton.getGlobalVisibleRect(buttonRect)
                return buttonRect.contains(x.toInt(), y.toInt())
            }

        }
    }


    class GroupedCommentAdapter(
        private val commentDataList: List<CommentData>,
        private val galleryDetailReviewFragment: GalleryDetailReviewFragment

        ) :
        RecyclerView.Adapter<GroupedCommentAdapter.GroupViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.comment_group_date, parent, false)
            return GroupViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
            val commentData = commentDataList[position]
            holder.bind(commentData)
        }

        override fun getItemCount(): Int = commentDataList.size

        inner class GroupViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val dateTextView: TextView = itemView.findViewById(R.id.tv_comment_group_date)
            private val commentRecyclerView: RecyclerView =
                itemView.findViewById(R.id.rv_comment_list)


            fun bind(commentData: CommentData) {
                val sb = StringBuilder(commentData.date.substring(5))
                sb[2] = '.'
                val formattedDate = sb.toString()
                dateTextView.text = formattedDate
                commentRecyclerView.adapter = CommentAdapter(
                    commentData.comments,
                    galleryDetailReviewFragment
                )
                commentRecyclerView.layoutManager = LinearLayoutManager(itemView.context)
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
                            .override(300, 300) // 이미지 크기 조정 (옵션)
                            .encodeFormat(Bitmap.CompressFormat.JPEG) // 이미지 포맷 변경 (옵션)
                            .encodeQuality(80) // 이미지 품질 설정 (0-100)
                    )
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                        ) {
                            // 압축된 이미지를 업로드하거나 처리합니다.
                            // resource 변수에 압축된 Bitmap 이미지가 전달됩니다.
                            val compressedUri = saveCompressedBitmap(resource)
                            val planId2 = planId?.toLong()

                            lifecycleScope.launch {
                                Dispatchers.IO
                                detailViewModel.uploadImage(planId2!!, compressedUri)
                                detailViewModel.checkPhotoResponse.observe(viewLifecycleOwner) { res->
                                    if(res == true) {
                                        galleryDetailReviewViewModel.getGalleryDetailPhoto(planId!!)
                                    }
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




}

