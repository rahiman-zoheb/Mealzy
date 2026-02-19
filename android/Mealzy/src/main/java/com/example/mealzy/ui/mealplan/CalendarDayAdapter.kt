package com.example.mealzy.ui.mealplan

import android.graphics.drawable.GradientDrawable
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mealzy.R
import com.example.mealzy.data.model.MealType
import com.example.mealzy.databinding.ItemCalendarDayStripBinding
import java.util.Calendar
import java.util.Date

class CalendarDayAdapter(
    private val onDayClick: (CalendarDay) -> Unit
) : ListAdapter<CalendarDay, CalendarDayAdapter.ViewHolder>(DiffCallback()) {

    private var selectedDate: Date? = null

    fun setSelectedDate(date: Date) {
        val oldPos = currentList.indexOfFirst { selectedDate != null && isSameDay(it.date, selectedDate!!) }
        val newPos = currentList.indexOfFirst { isSameDay(it.date, date) }
        selectedDate = date
        if (oldPos >= 0) notifyItemChanged(oldPos)
        if (newPos >= 0) notifyItemChanged(newPos)
    }

    private fun isSameDay(d1: Date, d2: Date): Boolean {
        val c1 = Calendar.getInstance().apply { time = d1 }
        val c2 = Calendar.getInstance().apply { time = d2 }
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) &&
                c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Each item occupies exactly 1/7 of the RecyclerView width
        val dp16 = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 16f, parent.context.resources.displayMetrics
        ).toInt()
        val itemWidth = (parent.context.resources.displayMetrics.widthPixels - 2 * dp16) / 7

        val binding = ItemCalendarDayStripBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        binding.root.layoutParams = RecyclerView.LayoutParams(
            itemWidth, RecyclerView.LayoutParams.WRAP_CONTENT
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val day = getItem(position)
        val isSelected = selectedDate?.let { isSameDay(day.date, it) } ?: false
        holder.bind(day, isSelected)
    }

    inner class ViewHolder(
        private val binding: ItemCalendarDayStripBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(day: CalendarDay, isSelected: Boolean) {
            binding.textDayName.text = day.dayName
            binding.textDayNumber.text = day.dayNumber.toString()

            // Circle highlight for today / selected
            when {
                day.isToday -> {
                    binding.textDayNumber.background =
                        ContextCompat.getDrawable(itemView.context, R.drawable.bg_day_circle_today)
                    binding.textDayNumber.setTextColor(
                        ContextCompat.getColor(itemView.context, R.color.md_theme_light_onPrimary)
                    )
                }
                isSelected -> {
                    binding.textDayNumber.background =
                        ContextCompat.getDrawable(itemView.context, R.drawable.bg_day_circle_selected)
                    binding.textDayNumber.setTextColor(
                        ContextCompat.getColor(itemView.context, R.color.md_theme_light_primary)
                    )
                }
                else -> {
                    binding.textDayNumber.background = null
                    binding.textDayNumber.setTextColor(
                        ContextCompat.getColor(itemView.context, R.color.md_theme_light_onSurface)
                    )
                }
            }

            // Meal-type dots: coloured if a meal is planned, grey otherwise
            setDot(binding.dotBreakfast, day.meals[MealType.BREAKFAST] != null, R.color.breakfast_color)
            setDot(binding.dotLunch,     day.meals[MealType.LUNCH]     != null, R.color.lunch_color)
            setDot(binding.dotDinner,    day.meals[MealType.DINNER]    != null, R.color.dinner_color)
            setDot(binding.dotSnack,     day.meals[MealType.SNACK]     != null, R.color.snack_color)

            binding.root.setOnClickListener { onDayClick(day) }
        }

        private fun setDot(dot: android.view.View, hasMeal: Boolean, activeColorRes: Int) {
            val color = if (hasMeal) {
                ContextCompat.getColor(itemView.context, activeColorRes)
            } else {
                ContextCompat.getColor(itemView.context, R.color.md_theme_light_outlineVariant)
            }
            dot.background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(color)
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<CalendarDay>() {
        override fun areItemsTheSame(oldItem: CalendarDay, newItem: CalendarDay) =
            oldItem.date == newItem.date

        override fun areContentsTheSame(oldItem: CalendarDay, newItem: CalendarDay) =
            oldItem == newItem
    }
}
