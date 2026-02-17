package com.example.mealzy.ui.mealplan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mealzy.R
import com.example.mealzy.data.model.MealPlan
import com.example.mealzy.data.model.MealType
import com.example.mealzy.databinding.ItemCalendarDayBinding

class CalendarDayAdapter(
    private val onAddMealClick: (CalendarDay, MealType) -> Unit,
    private val onMealClick: (MealPlan) -> Unit
) : ListAdapter<CalendarDay, CalendarDayAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCalendarDayBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemCalendarDayBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(day: CalendarDay) {
            binding.apply {
                // Set day header
                textDayName.text = day.dayName
                textDayNumber.text = day.dayNumber.toString()

                // Highlight today
                if (day.isToday) {
                    cardDayHeader.setCardBackgroundColor(
                        ContextCompat.getColor(root.context, R.color.md_theme_light_primary)
                    )
                    textDayName.setTextColor(
                        ContextCompat.getColor(root.context, R.color.md_theme_light_onPrimary)
                    )
                    textDayNumber.setTextColor(
                        ContextCompat.getColor(root.context, R.color.md_theme_light_onPrimary)
                    )
                } else {
                    cardDayHeader.setCardBackgroundColor(
                        ContextCompat.getColor(root.context, R.color.md_theme_light_surfaceVariant)
                    )
                    textDayName.setTextColor(
                        ContextCompat.getColor(root.context, R.color.md_theme_light_onSurfaceVariant)
                    )
                    textDayNumber.setTextColor(
                        ContextCompat.getColor(root.context, R.color.md_theme_light_onSurface)
                    )
                }

                // Setup meal slots
                setupMealSlot(containerBreakfast, day, MealType.BREAKFAST, "Breakfast")
                setupMealSlot(containerLunch, day, MealType.LUNCH, "Lunch")
                setupMealSlot(containerDinner, day, MealType.DINNER, "Dinner")
                setupMealSlot(containerSnack, day, MealType.SNACK, "Snack")
            }
        }

        private fun setupMealSlot(
            container: ViewGroup,
            day: CalendarDay,
            mealType: MealType,
            mealTypeName: String
        ) {
            container.removeAllViews()
            val meal = day.meals[mealType]

            if (meal != null) {
                // Show meal card
                val mealView = LayoutInflater.from(container.context)
                    .inflate(R.layout.item_meal_plan_entry, container, false)

                // TODO: Set meal details (recipe name, time, etc.)
                // For now, just show a placeholder
                mealView.setOnClickListener {
                    onMealClick(meal)
                }

                container.addView(mealView)
            } else {
                // Show empty slot with add button
                val emptyView = LayoutInflater.from(container.context)
                    .inflate(R.layout.item_meal_slot_empty, container, false)

                emptyView.setOnClickListener {
                    onAddMealClick(day, mealType)
                }

                container.addView(emptyView)
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<CalendarDay>() {
        override fun areItemsTheSame(oldItem: CalendarDay, newItem: CalendarDay): Boolean {
            return oldItem.date == newItem.date
        }

        override fun areContentsTheSame(oldItem: CalendarDay, newItem: CalendarDay): Boolean {
            return oldItem == newItem
        }
    }
}
