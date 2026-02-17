package com.example.mealzy.ui.recipes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.example.mealzy.R
import com.example.mealzy.data.model.MealType
import com.example.mealzy.data.model.Recipe
import com.example.mealzy.databinding.DialogRecipeDetailBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class RecipeDetailBottomSheet : BottomSheetDialogFragment() {

    private var _binding: DialogRecipeDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogRecipeDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args = requireArguments()
        val name = args.getString("name") ?: ""
        val description = args.getString("description") ?: ""
        val instructions = args.getString("instructions") ?: ""
        val prepTime = args.getInt("prepTime")
        val cookTime = args.getInt("cookTime")
        val servings = args.getInt("servings")
        val difficulty = args.getString("difficulty") ?: ""
        val mealTypeName = args.getString("mealType") ?: "BREAKFAST"
        val mealType = MealType.valueOf(mealTypeName)

        binding.apply {
            textRecipeName.text = name
            textDescription.text = description
            textInstructions.text = instructions
            textPrepTime.text = getString(R.string.prep_time, prepTime)
            textCookTime.text = getString(R.string.cook_time, cookTime)
            textServings.text = getString(R.string.servings, servings)
            textDifficulty.text = getString(R.string.difficulty, difficulty)

            val mealTypeLabel = when (mealType) {
                MealType.BREAKFAST -> getString(R.string.breakfast)
                MealType.LUNCH -> getString(R.string.lunch)
                MealType.DINNER -> getString(R.string.dinner)
                MealType.SNACK -> getString(R.string.snack)
            }
            chipMealType.text = mealTypeLabel

            val gradientDrawable = when (mealType) {
                MealType.BREAKFAST -> R.drawable.gradient_breakfast
                MealType.LUNCH -> R.drawable.gradient_lunch
                MealType.DINNER -> R.drawable.gradient_dinner
                MealType.SNACK -> R.drawable.gradient_snack
            }
            val iconDrawable = when (mealType) {
                MealType.BREAKFAST -> R.drawable.ic_breakfast_24
                MealType.LUNCH -> R.drawable.ic_lunch_24
                MealType.DINNER -> R.drawable.ic_dinner_24
                MealType.SNACK -> R.drawable.ic_snack_24
            }
            viewGradientBanner.setBackgroundResource(gradientDrawable)
            iconMealTypeBanner.setImageResource(iconDrawable)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "RecipeDetailBottomSheet"

        fun newInstance(recipe: Recipe): RecipeDetailBottomSheet {
            return RecipeDetailBottomSheet().apply {
                arguments = bundleOf(
                    "id" to recipe.id,
                    "name" to recipe.name,
                    "description" to recipe.description,
                    "instructions" to recipe.instructions,
                    "prepTime" to recipe.prepTimeMinutes,
                    "cookTime" to recipe.cookTimeMinutes,
                    "servings" to recipe.servings,
                    "difficulty" to recipe.difficulty,
                    "mealType" to recipe.mealType.name
                )
            }
        }
    }
}
