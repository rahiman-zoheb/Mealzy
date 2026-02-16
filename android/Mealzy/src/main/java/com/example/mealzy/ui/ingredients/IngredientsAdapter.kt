package com.example.mealzy.ui.ingredients

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mealzy.R
import com.example.mealzy.data.model.Ingredient
import com.example.mealzy.databinding.ItemIngredientBinding

class IngredientsAdapter(
    private val onIngredientClick: (Ingredient) -> Unit,
    private val onAvailabilityToggle: (Ingredient) -> Unit
) : ListAdapter<Ingredient, IngredientsAdapter.IngredientViewHolder>(IngredientDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val binding = ItemIngredientBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return IngredientViewHolder(binding)
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class IngredientViewHolder(
        private val binding: ItemIngredientBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(ingredient: Ingredient) {
            binding.apply {
                textIngredientName.text = ingredient.name
                textQuantity.text = "${ingredient.quantity} ${ingredient.unit}"
                chipCategory.text = ingredient.category
                
                // Set availability status
                switchAvailable.isChecked = ingredient.isAvailable
                textAvailabilityStatus.text = if (ingredient.isAvailable) {
                    root.context.getString(R.string.available)
                } else {
                    root.context.getString(R.string.out_of_stock)
                }

                // Set colors based on availability
                val textColor = if (ingredient.isAvailable) {
                    R.color.black
                } else {
                    android.R.color.darker_gray
                }
                textIngredientName.setTextColor(root.context.getColor(textColor))

                // Set click listeners
                root.setOnClickListener {
                    onIngredientClick(ingredient)
                }

                switchAvailable.setOnCheckedChangeListener { _, _ ->
                    onAvailabilityToggle(ingredient)
                }

                // Set category chip color based on category
                val categoryColor = when (ingredient.category.lowercase()) {
                    "protein" -> R.color.breakfast_color
                    "vegetables" -> R.color.lunch_color
                    "grains" -> R.color.dinner_color
                    "condiments" -> R.color.snack_color
                    else -> android.R.color.darker_gray
                }
                chipCategory.setChipBackgroundColorResource(categoryColor)
                chipCategory.text = ingredient.category
            }
        }
    }
}

private class IngredientDiffCallback : DiffUtil.ItemCallback<Ingredient>() {
    override fun areItemsTheSame(oldItem: Ingredient, newItem: Ingredient): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Ingredient, newItem: Ingredient): Boolean {
        return oldItem == newItem
    }
}