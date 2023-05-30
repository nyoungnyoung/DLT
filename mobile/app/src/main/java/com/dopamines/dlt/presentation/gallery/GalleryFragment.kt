package com.dopamines.dlt.presentation.home

import android.app.AlertDialog
import android.content.res.Resources
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Layout
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.style.AbsoluteSizeSpan
import android.text.style.AlignmentSpan
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView

import androidx.core.content.ContextCompat

import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment

import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavOptions

import androidx.navigation.findNavController

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.dopamines.dlt.R
import com.dopamines.dlt.databinding.FragmentGalleryBinding

import com.dopamines.dlt.presentation.gallery.GalleryData
import com.dopamines.dlt.presentation.gallery.GalleryRepository
import com.dopamines.dlt.presentation.gallery.GalleryViewModel
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat

import java.util.Calendar
import java.util.Locale

class GalleryFragment : Fragment() {

    private lateinit var binding: FragmentGalleryBinding
    private lateinit var galleryViewModel: GalleryViewModel

    private fun isPreviousAppointmentExist(date: String, time: String): Boolean {
        val currentTime = Calendar.getInstance()
        currentTime.add(Calendar.HOUR_OF_DAY, 1)

        val dateTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse("$date $time")
        return dateTime?.before(currentTime.time) == true

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_gallery, container, false
        )

        // 네비게이션바 보여주기
        val HomeActivity = activity as HomeActivity
        HomeActivity.HideBottomNavi(false)

        // RecyclerView 설정
        val recyclerView = binding.rvPhotoList
        recyclerView.setHasFixedSize(true)

        val layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager


        val galleryRepository = GalleryRepository(requireContext())
        galleryViewModel = GalleryViewModel(galleryRepository)


        val calendarView = binding.cvCalendarGallery
        // Topbar 지우기
        calendarView.topbarVisible = false


        // 현재 월의 첫째 날짜를 yyyy-MM-01의 형태로 구함
        val currentDate = Calendar.getInstance().apply { set(Calendar.DAY_OF_MONTH, 1) }
        val currentDateString =
            SimpleDateFormat("yyyy-MM-dd", Locale.KOREA).format(currentDate.time)
        val year = currentDate.get(Calendar.YEAR)
        val month = currentDate.get(Calendar.MONTH) + 1

        binding.tvMonth.text = String.format("%d.%02d", year, month)
        galleryViewModel.getGalleryData(currentDateString)


        calendarView.setOnMonthChangedListener { _, date ->

            val year = date.year
            val month = date.month
            val dateString = "$year-${String.format("%02d", month)}-01"
            Log.i("gk", currentDateString.toString())
            Log.i("gk", dateString)
            binding.tvMonth.text = String.format("%d.%02d", year, month)
            galleryViewModel.getGalleryData(dateString)
        }

        val requestOptions = RequestOptions()
            .override(500, 500)
            .centerCrop()
        val handler = Handler(Looper.getMainLooper())

        // 현재 시간 가져오기
        galleryViewModel.galleryData.observe(viewLifecycleOwner) { galleryData ->
            val data = mutableMapOf<String, MutableList<GalleryData>>()

            galleryData?.forEach { gallery ->
                val planTime = gallery.key
                if (!data.containsKey(planTime)) {
                    data[planTime] = mutableListOf()
                }
                data[planTime]?.add(gallery.data)
            }


            for (dateStr in data.keys) {

                // 현재날짜
                val currentTime = Calendar.getInstance()
                val dateList = data[dateStr] ?: emptyList()
                val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                val sortedDateList = dateList.sortedBy { timeFormat.parse(it.planTime) }
                val firstData = sortedDateList.firstOrNull()
                val firstDataWithPhoto = sortedDateList.firstOrNull { it.photoUrl != null }


                if (firstData != null && isPreviousAppointmentExist(dateStr, firstData.planTime)) {
                    val eventTime = Calendar.getInstance()
                    eventTime.set(
                        dateStr.substring(0, 4).toInt(),
                        dateStr.substring(5, 7).toInt() - 1,
                        dateStr.substring(8, 10).toInt() - 1
                    )
                    // 현재날짜과 비교하기
                    if (eventTime.before(currentTime)) {
                        val firstPhotoUrl = firstDataWithPhoto?.photoUrl

                        if (firstPhotoUrl != null) {
                            lifecycleScope.launch {
                                val drawable = withContext(Dispatchers.IO) {
                                    Glide.with(requireContext())
                                        .load(firstPhotoUrl)
                                        .apply(requestOptions)
                                        .submit()
                                        .get()
                                }

                                handler.post {
                                    val year = dateStr.substring(0, 4).toInt()
                                    val month = dateStr.substring(5, 7).toInt()
                                    val day = dateStr.substring(8, 10).toInt()
                                    val date = CalendarDay.from(year, month, day)
                                    val decorator = drawable?.let { EventDecorator(it, date) }

                                    calendarView.addDecorator(decorator)
                                }
                            }
                        } else {
                            val defaultDrawable =
                                ContextCompat.getDrawable(requireContext(), R.drawable.backgournd_default)
                            val year = dateStr.substring(0, 4).toInt()
                            val month = dateStr.substring(5, 7).toInt()
                            val day = dateStr.substring(8, 10).toInt()
                            val date = CalendarDay.from(year, month, day)

                            if (isPreviousAppointmentExist(dateStr, firstData.planTime)) {
                                Log.i("planTime", firstData.planTime)
                                Log.i("planTime result", isPreviousAppointmentExist(dateStr, firstData.planTime).toString())
                                val decorator = defaultDrawable?.let { EventDecorator(it, date) }
                                calendarView.addDecorator(decorator)
                            }

                        }

                    }

                }
            }


            calendarView.setOnDateChangedListener { widget, date, selected ->

                val year = date.year.toString()
                val month = String.format("%02d", date.month)
                val day = String.format("%02d", date.day)
                val dateStr = "$year-$month-$day"
                val selectedDateList = data[dateStr] ?: emptyList()

                val adapter = GalleryPhotoAdapter(selectedDateList, dateStr)
                recyclerView.adapter = adapter


            }
        }


        val saturdayDecorator = SaturdayDecorator()
        val sundayDecorator = SundayDecorator()
        val allDayDecorator = AllDayDecorator()
        val disabledDayDecorator = DisabledDayDecorator()
        calendarView.addDecorator(allDayDecorator)
        calendarView.addDecorator(saturdayDecorator)
        calendarView.addDecorator(sundayDecorator)
        calendarView.addDecorator(disabledDayDecorator)


        return binding.root
    }


    class GalleryPhotoAdapter(
        private var itemList: List<GalleryData>,
        private val dateStr: String
    ) :
        RecyclerView.Adapter<GalleryPhotoAdapter.ViewHolder>() {


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_card_gallery, parent, false)


            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = itemList[position]

            holder.bind(item)
        }

        override fun getItemCount(): Int {
            return itemList.size
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val imageView: ImageView = itemView.findViewById(R.id.iv_photo)
            private val textView:TextView = itemView.findViewById(R.id.tv_item_text)

            fun bind(item: GalleryData) {
                val requestOptions = RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.backgournd_default)
                    .error(R.drawable.backgournd_default) // 에러 이미지 설정

                Glide.with(itemView.context)
                    .load(item.photoUrl)
                    .apply(requestOptions)
                    .into(imageView)

                textView.text = item.planTime

                val currentTime = Calendar.getInstance().timeInMillis

                val planDateTime = Calendar.getInstance().apply {
                    val dateParts = dateStr.split("-")
                    val year = dateParts[0].toInt()
                    val month = dateParts[1].toInt() - 1
                    val day = dateParts[2].toInt()

                    val timeParts = item.planTime.split(":")
                    val hour = timeParts[0].toInt()
                    val minute = timeParts[1].toInt()

                    set(year, month, day, hour, minute)
                }.timeInMillis

                val oneHourBeforeCurrentTime = currentTime - (60 * 60 * 1000) // 1시간을 밀리초로 변환

                if (planDateTime <= oneHourBeforeCurrentTime) {
                    itemView.visibility = View.VISIBLE
                    // 애니메이션
                    val animation = AnimationUtils.loadAnimation(itemView.context, R.anim.slide_up)
                    itemView.startAnimation(animation)

                    val delay = (bindingAdapterPosition + 1) * 25L // 딜레이 계산
                    animation.startOffset = delay // 애니메이션에 딜레이 설정

                } else {
                    itemView.visibility = View.GONE
                }

                // 각 아이템 클릭 시 GalleryDetailFragment로 이동
                itemView.setOnClickListener {
                    item.planId?.let { planId ->
                        val bundle = Bundle().apply {
                            putInt("planId", planId)
                        }
//                        val navOptions = NavOptions.Builder()
//                            .setEnterAnim(R.anim.fragment_in)
//                            .setExitAnim(R.anim.fragment_out)
//                            .build()

                        itemView.findNavController().navigate(
                            R.id.galleryDetailFragment,
                            bundle,
//                            navOptions
                        )
                    }
                }
            }
        }

        init {
            val sortedList = itemList.sortedBy { it.planTime }
            itemList = sortedList
        }


    }

    class DisabledDayDecorator : DayViewDecorator {

        override fun shouldDecorate(day: CalendarDay): Boolean {
            val today = CalendarDay.today()
            return day.isAfter(today) // 오늘 이후의 날짜만 비활성화 처리
        }

        override fun decorate(view: DayViewFacade) {
            view.setDaysDisabled(true) // 날짜를 비활성화 처리하는 스타일 적용
        }
    }

    class EventDecorator(private val drawable: Drawable, private val date: CalendarDay) :
        DayViewDecorator {
        override fun shouldDecorate(day: CalendarDay?): Boolean {
            return date == day
        }

        override fun decorate(view: DayViewFacade?) {
            view?.addSpan(ForegroundColorSpan(Color.WHITE))
            view?.setBackgroundDrawable(drawable)

        }
    }

    class AllDayDecorator : DayViewDecorator {
        override fun shouldDecorate(day: CalendarDay?): Boolean {
            return true
        }

        override fun decorate(view: DayViewFacade?) {

            // Set the text size
            view?.addSpan(AbsoluteSizeSpan(15, true))
        }
    }

    class SaturdayDecorator : DayViewDecorator {

        private val calendar = Calendar.getInstance()

        override fun shouldDecorate(day: CalendarDay): Boolean {
            val year = day.year
            val month = day.month - 1
            val dayOfMonth = day.day
            calendar.set(year, month, dayOfMonth)
            val weekDay: Int = calendar.get(Calendar.DAY_OF_WEEK)
            return weekDay == Calendar.SATURDAY
        }

        override fun decorate(view: DayViewFacade) {
            view.addSpan(ForegroundColorSpan(Color.parseColor("#5B47D5")))
        }
    }

    class SundayDecorator : DayViewDecorator {

        private val calendar = Calendar.getInstance()

        override fun shouldDecorate(day: CalendarDay): Boolean {
            val year = day.year
            val month = day.month - 1
            val dayOfMonth = day.day
            calendar.set(year, month, dayOfMonth)
            val weekDay: Int = calendar.get(Calendar.DAY_OF_WEEK)
            return weekDay == Calendar.SUNDAY
        }

        override fun decorate(view: DayViewFacade) {
            view.addSpan(ForegroundColorSpan(Color.parseColor("#E25E6D")))
        }
    }


}


