package com.example.mealzy.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mealzy.R
import com.example.mealzy.data.model.MealType
import com.example.mealzy.databinding.ItemSuggestedRecipeHorizontalBinding

class SuggestedRecipesAdapter(
    private val onRecipeClick: (RecipeWithIngredientMatch) -> Unit
) : ListAdapter<RecipeWithIngredientMatch, SuggestedRecipesAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSuggestedRecipeHorizontalBinding.inflate(
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
        private val binding: ItemSuggestedRecipeHorizontalBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: RecipeWithIngredientMatch) {
            binding.apply {
                textRecipeName.text = item.recipe.name
                textIngredientMatch.text = root.context.getString(
                    R.string.ingredient_match,
                    item.matchingIngredientCount,
                    item.totalIngredientCount
                )

                // Set gradient background and icon based on meal type
                val (gradientDrawable, iconDrawable) = when (item.recipe.mealType) {
                    MealType.BREAKFAST ->
                        Pair(R.drawable.gradient_breakfast, R.drawable.ic_breakfast_24)
                    MealType.LUNCH ->
                        Pair(R.drawable.gradient_lunch, R.drawable.ic_lunch_24)
                    MealType.DINNER ->
                        Pair(R.drawable.gradient_dinner, R.drawable.ic_dinner_24)
                    MealType.SNACK ->
                        Pair(R.drawable.gradient_snack, R.drawable.ic_snack_24)
                }

                viewGradientBackground.setBackgroundResource(gradientDrawable)
                iconMealType.setImageResource(iconDrawable)

                root.setOnClickListener {
                    onRecipeClick(item)
                }
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<RecipeWithIngredientMatch>() {
        override fun areItemsTheSame(
            oldItem: RecipeWithIngredientMatch,
            newItem: RecipeWithIngredientMatch
        ): Boolean {
            return oldItem.recipe.id == newItem.recipe.id
        }

        override fun areContentsTheSame(
            oldItem: RecipeWithIngredientMatch,
            newItem: RecipeWithIngredientMatch
        ): Boolean {
            return oldItem == newItem
        }
    }
}
