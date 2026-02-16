package com.example.mealzy.ui.recipes

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mealzy.R
import com.example.mealzy.data.model.Recipe
import com.example.mealzy.databinding.ItemRecipeBinding

class RecipesAdapter(
    private val onRecipeClick: (Recipe) -> Unit
) : ListAdapter<Recipe, RecipesAdapter.RecipeViewHolder>(RecipeDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val binding = ItemRecipeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RecipeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class RecipeViewHolder(
        private val binding: ItemRecipeBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(recipe: Recipe) {
            binding.apply {
                textRecipeName.text = recipe.name
                textRecipeDescription.text = recipe.description
                textPrepTime.text = root.context.getString(R.string.prep_time, recipe.prepTimeMinutes)
                textCookTime.text = root.context.getString(R.string.cook_time, recipe.cookTimeMinutes)
                textServings.text = root.context.getString(R.string.servings, recipe.servings)
                textDifficulty.text = root.context.getString(R.string.difficulty, recipe.difficulty)
                
                chipMealType.text = recipe.mealType.name.lowercase().replaceFirstChar { it.uppercase() }
                
                // Set meal type chip color based on meal type
                val chipColor = when (recipe.mealType) {
                    com.example.mealzy.data.model.MealType.BREAKFAST -> R.color.breakfast_color
                    com.example.mealzy.data.model.MealType.LUNCH -> R.color.lunch_color
                    com.example.mealzy.data.model.MealType.DINNER -> R.color.dinner_color
                    com.example.mealzy.data.model.MealType.SNACK -> R.color.snack_color
                }
                chipMealType.setChipBackgroundColorResource(chipColor)

                root.setOnClickListener {
                    onRecipeClick(recipe)
                }
            }
        }
    }
}

private class RecipeDiffCallback : DiffUtil.ItemCallback<Recipe>() {
    override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
        return oldItem == newItem
    }
}