package com.example.mealzy.ui.recipes

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mealzy.R
import com.example.mealzy.data.model.RecipeIngredientDetail
import com.example.mealzy.databinding.ItemRecipeIngredientBinding

class RecipeIngredientDetailAdapter :
    ListAdapter<RecipeIngredientDetail, RecipeIngredientDetailAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemRecipeIngredientBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
        private val binding: ItemRecipeIngredientBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(detail: RecipeIngredientDetail) {
            binding.textIngredientName.text = detail.ingredientName
            binding.textIngredientQuantity.text = "${detail.quantity} ${detail.unit}"

            val colorRes = if (detail.isAvailable) {
                R.color.success_color
            } else {
                R.color.color_ingredient_unavailable
            }
            binding.viewAvailabilityDot.backgroundTintList =
                ColorStateList.valueOf(ContextCompat.getColor(binding.root.context, colorRes))
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<RecipeIngredientDetail>() {
        override fun areItemsTheSame(
            oldItem: RecipeIngredientDetail,
            newItem: RecipeIngredientDetail
        ) = oldItem.ingredientId == newItem.ingredientId

        override fun areContentsTheSame(
            oldItem: RecipeIngredientDetail,
            newItem: RecipeIngredientDetail
        ) = oldItem == newItem
    }
}
