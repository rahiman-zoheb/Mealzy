package com.example.mealzy.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mealzy.R
import com.example.mealzy.data.model.MealType
import com.example.mealzy.databinding.ItemUpcomingMealBinding
import java.text.SimpleDateFormat
import java.util.Locale

data class UpcomingMealItem(
    val recipeName: String,
    val mealType: MealType,
    val dateLabel: String
)

class UpcomingMealsAdapter : ListAdapter<UpcomingMealItem, UpcomingMealsAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemUpcomingMealBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemUpcomingMealBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: UpcomingMealItem) {
            binding.apply {
                textUpcomingRecipeName.text = item.recipeName

                val mealTypeName = item.mealType.name.lowercase()
                    .replaceFirstChar { it.uppercase() }
                textUpcomingDate.text = "${item.dateLabel} · $mealTypeName"
                chipUpcomingMealType.text = mealTypeName

                val chipColor = when (item.mealType) {
                    MealType.BREAKFAST -> R.color.breakfast_color
                    MealType.LUNCH -> R.color.lunch_color
                    MealType.DINNER -> R.color.dinner_color
                    MealType.SNACK -> R.color.snack_color
                }
                val dotColor = ContextCompat.getColor(root.context, chipColor)
                viewMealTypeDot.background.setTint(dotColor)
                chipUpcomingMealType.chipBackgroundColor =
                    android.content.res.ColorStateList.valueOf(dotColor).withAlpha(40)
                chipUpcomingMealType.setTextColor(dotColor)
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<UpcomingMealItem>() {
        override fun areItemsTheSame(oldItem: UpcomingMealItem, newItem: UpcomingMealItem) =
            oldItem.recipeName == newItem.recipeName && oldItem.dateLabel == newItem.dateLabel

        override fun areContentsTheSame(oldItem: UpcomingMealItem, newItem: UpcomingMealItem) =
            oldItem == newItem
    }
}
