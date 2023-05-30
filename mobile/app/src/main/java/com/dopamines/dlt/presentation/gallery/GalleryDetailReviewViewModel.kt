package com.dopamines.dlt.presentation.gallery

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dopamines.dlt.presentation.auth.SharedPreferences
import kotlinx.coroutines.launch

class GalleryDetailReviewViewModel(private val repository: GalleryRepository) : ViewModel() {

    private val _galleryDetailPhoto = MutableLiveData<GalleryDetailPhoto?>()
    val galleryDetailPhoto: MutableLiveData<GalleryDetailPhoto?> = _galleryDetailPhoto




    private val _comments = MutableLiveData<List<CommentData>>()
    val comments: MutableLiveData<List<CommentData>> = _comments


    private val _checkResponse = MutableLiveData<Boolean>()
    val checkResponse: LiveData<Boolean>
        get() = _checkResponse



    private val _nickname = MutableLiveData<String>()
    val nickname : MutableLiveData<String> = _nickname


    private val _checkDeleteResponse = MutableLiveData<Boolean>()
    val checkDeleteResponse: LiveData<Boolean>
        get() = _checkDeleteResponse



    private val _photoId = MutableLiveData<Int?>()
    val photoId: LiveData<Int?>
        get() = _photoId

    private val _planId = MutableLiveData<Int>()
    val planId: LiveData<Int>
        get() = _planId

    private val _photoUrl = MutableLiveData<String?>()
    val photoUrl: LiveData<String?>
        get() = _photoUrl

    private val _registerTime = MutableLiveData<String>()
    val registerTime: LiveData<String>
        get() = _registerTime

    fun setPhotoId(photoId: Int?) {
        _photoId.value = photoId
    }

    fun setPlanId(planId: Int) {
        _planId.value = planId
    }

    fun setPhotoUrl(photoUrl: String?) {
        _photoUrl.value = photoUrl
    }

    fun setRegisterTime(registerTime: String) {
        _registerTime.value = registerTime
    }

    fun getGalleryDetailPhoto(planId: Int) {
        viewModelScope.launch {
            try {
                val response = repository.getGalleryDetailPhoto(planId)
                if (response.isSuccessful) {
                    val photoData = response.body()
                    if (photoData != null) {
                        _galleryDetailPhoto.value = photoData
                        setPhotoId(photoData.photoId)
                        setPhotoUrl(photoData.photoUrl)
                        setRegisterTime(photoData.registerTime!!)
                        setPlanId(photoData.planId)
                    }
                }

            } catch (e: Exception) {
                Log.e("CATCH IN GALLERY REV", e.toString())
            }
        }
    }



    fun getComments(planId: Int) {
        viewModelScope.launch {
            try {
                val response = repository.getComments(planId)
                if(response.isSuccessful) {
                    Log.i("CATCH IN GALLERY COMMENT", response.body().toString())

                    val map = response.body() ?: emptyMap()
                    val commentDataList = mutableListOf<CommentData>()

                    for (key in map.keys) {
                        val comments = map[key] ?: emptyList()
                        commentDataList.add(CommentData(key, comments))
                    }
                    _comments.value = commentDataList

                }
            } catch (e:Exception) {
                Log.e("CATCH IN GALLERY COMMENT", e.toString())

            }
        }
    }

    fun createComment(planId:Int, content: String) {
        Log.i("CC", content.toString())
        viewModelScope.launch {
            val response = repository.createComment(planId, content)
            try {
                if(response.isSuccessful) {
                    Log.i("TRY IN GALLERY CRAETE_COMMENT", response.body().toString())
                    _checkResponse.value = true
                }
            } catch (e:Exception) {
                Log.e("CATCH IN GALLERY CREATE_COMMENT", e.toString())
            }
        }
    }

    fun getProfile(planId: Int) {
        viewModelScope.launch {
            try {
                val response = repository.getPlanEndDetailData(planId)
                if (response.isSuccessful) {
                    val planEndDetailData = response.body()
                    _nickname.value = planEndDetailData?.myDetail?.nickname

                }
            } catch (e: java.lang.Exception) {
                Log.e("CATCH IN GALLERY REVEIW-PROFILE", e.toString())
            }
        }
    }


    fun deleteComment(commentId:Int) {
        viewModelScope.launch {
            try{
                val response = repository.deleteComment(commentId)
                if(response.isSuccessful) {
                    _checkDeleteResponse.value = true
                }
            } catch (e: java.lang.Exception) {
                Log.e("CATCH IN GALLERY REVEIW-DELETE", e.toString())
            }
        }
    }





}
