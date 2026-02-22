package com.example.mealzy.ui.ingredients

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mealzy.R
import com.example.mealzy.data.model.Ingredient
import com.example.mealzy.databinding.ItemIngredientBinding

sealed class IngredientListItem {
    data class Header(val category: String, val count: Int) : IngredientListItem()
    data class Item(val ingredient: Ingredient, val isLowStock: Boolean) : IngredientListItem()
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
            is IngredientListItem.Header -> (holder as HeaderViewHolder).bind(item.category, item.count)
            is IngredientListItem.Item -> (holder as IngredientViewHolder).bind(item)
        }
    }

    class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.text_section_header)
        private val countView: TextView = itemView.findViewById(R.id.text_section_count)
        fun bind(category: String, count: Int) {
            textView.text = category
            countView.text = itemView.context.getString(R.string.section_count, count)
        }
    }

    inner class IngredientViewHolder(
        private val binding: ItemIngredientBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: IngredientListItem.Item) {
            val ingredient = item.ingredient
            binding.apply {
                textIngredientName.text = ingredient.name
                textQuantity.text = "${ingredient.quantity} ${ingredient.unit}"
                chipCategory.text = ingredient.category

                // Status chip: color-coded availability badge (replaces toggle switch)
                val (statusLabel, bgColorRes, textColorRes) = when {
                    !ingredient.isAvailable -> Triple(
                        root.context.getString(R.string.out_of_stock),
                        R.color.status_out_of_stock_bg,
                        R.color.status_out_of_stock_fg
                    )
                    item.isLowStock -> Triple(
                        root.context.getString(R.string.low_stock),
                        R.color.status_low_stock_bg,
                        R.color.status_low_stock_fg
                    )
                    else -> Triple(
                        root.context.getString(R.string.available),
                        R.color.status_available_bg,
                        R.color.status_available_fg
                    )
                }
                chipStatus.text = statusLabel
                chipStatus.chipBackgroundColor =
                    ColorStateList.valueOf(root.context.getColor(bgColorRes))
                chipStatus.setTextColor(root.context.getColor(textColorRes))

                // Category color chip
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

                // Tap card → edit; tap status chip → toggle available/out-of-stock
                root.setOnClickListener { onIngredientClick(ingredient) }
                chipStatus.setOnClickListener { onAvailabilityToggle(ingredient) }
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
