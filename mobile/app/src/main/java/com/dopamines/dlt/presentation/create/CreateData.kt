package com.dopamines.dlt.presentation.create

import android.os.Parcel
import android.os.Parcelable
import android.text.Editable
import com.dopamines.dlt.presentation.detail.Participant
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class PromiseCreate (
    @SerializedName("title")
    val title: String,
    @SerializedName("planDate")
    val planDate: String,
    @SerializedName("planTime")
    val planTime: String,
    @SerializedName("location")
    val location: String,
    @SerializedName("cost")
    val cost: Int,
    @SerializedName("participantIds")
    val participantIds: List<Participant>,
)


// 카카오맵 장소 검색 결과 data class
data class ResultSearchPlace(
    var meta: PlaceMeta, // 장소 메타데이터
    var documents: List<Place> // 검색 결과
)

data class PlaceMeta(
    var total_count: Int, // 검색어에 검색된 문서 수
    var pageable_count: Int, // total_count 중 노출 가능 문서 수, 최대 45 (API에서 최대 45개 정보만 제공)
    var is_end: Boolean, // 현재 페이지가 마지막 페이지인지 여부, 값이 false면 page를 증가시켜 다음 페이지를 요청할 수 있음
    var same_name: RegionInfo // 질의어의 지역 및 키워드 분석 정보
)

data class RegionInfo(
    var region: List<String>, // 질의어에서 인식된 지역의 리스트, ex) '중앙로 맛집' 에서 중앙로에 해당하는 지역 리스트
    var keyword: String, // 질의어에서 지역 정보를 제외한 키워드, ex) '중앙로 맛집' 에서 '맛집'
    var selected_region: String // 인식된 지역 리스트 중, 현재 검색에 사용된 지역 정보
)

data class Place(
    var id: String, // 장소 ID
    var place_name: String, // 장소명, 업체명
    var category_name: String, // 카테고리 이름
    var category_group_code: String, // 중요 카테고리만 그룹핑한 카테고리 그룹 코드
    var category_group_name: String, // 중요 카테고리만 그룹핑한 카테고리 그룹명
    var phone: String, // 전화번호
    var address_name: String, // 전체 지번 주소
    var road_address_name: String, // 전체 도로명 주소
    var x: String, // X 좌표값 혹은 longitude
    var y: String, // Y 좌표값 혹은 latitude
    var place_url: String, // 장소 상세페이지 URL
    var distanc: String // 중심좌표까지의 거리. 단, x,y 파라미터를 준 경우에만 존재. 단위는 meter
)

// 장소 검색 리사이클러뷰 아이템 data class
data class ListLayout(
    val name: String,
    val x: Double,
    val y: Double,
    val road_address_name: String
)

// 참가자 목록 리사이클러뷰 아이템 data class
data class PeopleLayout(
    val accountId: Int,
    val nickname: String,
    val profile: String
)

// 참가자 검색 리사이클러뷰 아이템 data class
data class PeopleSearchLayout(
    val accountId: Int,
    val nickname: String,
    val profile: String,
)

// 참가자 검색 Response
data class PeopleSearchResponse(
    val accountId: Int,
    val nickname: String,
    val profile: String,
    val profileMessage: String
)
