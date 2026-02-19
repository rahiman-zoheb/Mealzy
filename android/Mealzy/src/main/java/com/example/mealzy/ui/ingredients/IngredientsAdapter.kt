package com.example.mealzy.ui.ingredients

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mealzy.R
import com.example.mealzy.data.model.Ingredient
import com.example.mealzy.databinding.ItemIngredientBinding

sealed class IngredientListItem {
    data class Header(val category: String) : IngredientListItem()
    data class Item(val ingredient: Ingredient) : IngredientListItem()
}

class IngredientsAdapter(
    private val onIngredientClick: (Ingredient) -> Unit,
    private val onAvailabilityToggle: (Ingredient) -> Unit
) : ListAdapter<IngredientListItem, RecyclerView.ViewHolder>(IngredientListDiffCallback()) {

    companion object {
        private const val VIEW_TYPE_HEADER = 0
        private const val VIEW_TYPE_ITEM = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is IngredientListItem.Header -> VIEW_TYPE_HEADER
            is IngredientListItem.Item -> VIEW_TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_HEADER -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_ingredient_section_header, parent, false)
                HeaderViewHolder(view)
            }
            else -> {
                val binding = ItemIngredientBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                IngredientViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is IngredientListItem.Header -> (holder as HeaderViewHolder).bind(item.category)
            is IngredientListItem.Item -> (holder as IngredientViewHolder).bind(item.ingredient)
        }
    }

    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.text_section_header)
        fun bind(category: String) {
            textView.text = category
        }
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

                // Set category color (both chip and left edge strip)
                val categoryColor = when (ingredient.category.lowercase()) {
                    "protein" -> R.color.breakfast_color
                    "vegetables" -> R.color.lunch_color
                    "fruits" -> R.color.success_color
                    "grains" -> R.color.dinner_color
                    "dairy" -> R.color.info_color
                    "condiments", "spices" -> R.color.snack_color
                    else -> R.color.md_theme_light_outline
                }
                chipCategory.setChipBackgroundColorResource(categoryColor)

                // Set left edge color strip
                viewCategoryColorStrip.setBackgroundColor(
                    ContextCompat.getColor(root.context, categoryColor)
                )

                // Show low stock warning badge if quantity is low
                val quantityValue = ingredient.quantity.toDoubleOrNull() ?: 0.0
                val isLowStock = when (ingredient.unit.lowercase()) {
                    "pieces", "pcs" -> quantityValue <= 1.0
                    "cups", "lbs", "kg", "oz", "grams" -> quantityValue <= 0.5
                    else -> quantityValue <= 1.0
                }
                iconLowStock.visibility = if (isLowStock && ingredient.isAvailable) {
                    View.VISIBLE
                } else {
                    View.GONE
                }

                // Set click listeners
                root.setOnClickListener {
                    onIngredientClick(ingredient)
                }

                switchAvailable.setOnCheckedChangeListener { _, _ ->
                    onAvailabilityToggle(ingredient)
                }
            }
        }
    }
}

private class IngredientListDiffCallback : DiffUtil.ItemCallback<IngredientListItem>() {
    override fun areItemsTheSame(oldItem: IngredientListItem, newItem: IngredientListItem): Boolean {
        return when {
            oldItem is IngredientListItem.Header && newItem is IngredientListItem.Header ->
                oldItem.category == newItem.category
            oldItem is IngredientListItem.Item && newItem is IngredientListItem.Item ->
                oldItem.ingredient.id == newItem.ingredient.id
            else -> false
        }
    }

    override fun areContentsTheSame(oldItem: IngredientListItem, newItem: IngredientListItem): Boolean {
        return oldItem == newItem
    }
}
