package com.dopamines.dlt.presentation.gallery

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


class GalleryViewModel(private val repository: GalleryRepository): ViewModel()  {
    private val _galleryData = MutableLiveData<List<GalleryDataWithKey>?>()
    val galleryData: MutableLiveData<List<GalleryDataWithKey>?> = _galleryData

    fun getGalleryData(date: String) {
        viewModelScope.launch {
            try {
                val response = repository.getGalleryData(date)
                Log.i("RESPONSE", "${response.body()}")
                if (response.isSuccessful) {
                    val galleryDataMap = response.body()
                    val galleryData = mutableListOf<GalleryDataWithKey>()

                    galleryDataMap?.forEach { (key, value) ->
                        value.forEach { data ->
                            galleryData.add(GalleryDataWithKey(key, data))
                        }
                    }
                    _galleryData.value = galleryData
                    Log.i("TRY IN GALLERY", _galleryData.value.toString())
                } else {
                    Log.e("ERROR IN GALLERY", response.message())
                }
            } catch (e: Exception) {

            }
        }
    }

    data class GalleryDataWithKey(val key: String, val data: GalleryData)


}
