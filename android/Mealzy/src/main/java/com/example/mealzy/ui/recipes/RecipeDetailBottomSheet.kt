package com.example.mealzy.ui.recipes

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mealzy.R
import com.example.mealzy.data.model.MealType
import com.example.mealzy.data.model.Recipe
import com.example.mealzy.databinding.DialogRecipeDetailBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class RecipeDetailBottomSheet : BottomSheetDialogFragment() {

    private var _binding: DialogRecipeDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: RecipeDetailViewModel
    private lateinit var ingredientAdapter: RecipeIngredientDetailAdapter

    // Local mutable state for the favorite button
    private var isFavorite = false
    private lateinit var currentRecipe: Recipe

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

        viewModel = ViewModelProvider(this)[RecipeDetailViewModel::class.java]

        val args = requireArguments()
        val recipeId = args.getLong("id")
        val name = args.getString("name") ?: ""
        val description = args.getString("description") ?: ""
        val instructions = args.getString("instructions") ?: ""
        val prepTime = args.getInt("prepTime")
        val cookTime = args.getInt("cookTime")
        val servings = args.getInt("servings")
        val difficulty = args.getString("difficulty") ?: ""
        val mealType = MealType.valueOf(args.getString("mealType") ?: "BREAKFAST")
        isFavorite = args.getBoolean("isFavorite", false)

        currentRecipe = Recipe(
            id = recipeId,
            name = name,
            description = description,
            instructions = instructions,
            prepTimeMinutes = prepTime,
            cookTimeMinutes = cookTime,
            servings = servings,
            difficulty = difficulty,
            mealType = mealType,
            isFavorite = isFavorite
        )

        setupStaticContent(name, description, instructions, prepTime, cookTime, servings, difficulty, mealType)
        setupFavoriteButton()
        setupIngredientsRecyclerView()

        viewModel.loadIngredients(recipeId)
        viewModel.ingredients.observe(viewLifecycleOwner) { ingredients ->
            ingredientAdapter.submitList(ingredients)
            val availableCount = ingredients.count { it.isAvailable }
            binding.textIngredientsSummary.text =
                getString(R.string.ingredients_available, availableCount, ingredients.size)
        }
    }

    private fun setupStaticContent(
        name: String,
        description: String,
        instructions: String,
        prepTime: Int,
        cookTime: Int,
        servings: Int,
        difficulty: String,
        mealType: MealType
    ) {
        binding.apply {
            textRecipeName.text = name
            textDescription.text = description
            textInstructions.text = instructions
            textPrepTime.text = getString(R.string.time_minutes, prepTime)
            textCookTime.text = getString(R.string.time_minutes, cookTime)
            textServings.text = servings.toString()
            textDifficulty.text = difficulty

            chipMealType.text = when (mealType) {
                MealType.BREAKFAST -> getString(R.string.breakfast)
                MealType.LUNCH -> getString(R.string.lunch)
                MealType.DINNER -> getString(R.string.dinner)
                MealType.SNACK -> getString(R.string.snack)
            }

            viewGradientBanner.setBackgroundResource(
                when (mealType) {
                    MealType.BREAKFAST -> R.drawable.gradient_breakfast
                    MealType.LUNCH -> R.drawable.gradient_lunch
                    MealType.DINNER -> R.drawable.gradient_dinner
                    MealType.SNACK -> R.drawable.gradient_snack
                }
            )
            iconMealTypeBanner.setImageResource(
                when (mealType) {
                    MealType.BREAKFAST -> R.drawable.ic_breakfast_24
                    MealType.LUNCH -> R.drawable.ic_lunch_24
                    MealType.DINNER -> R.drawable.ic_dinner_24
                    MealType.SNACK -> R.drawable.ic_snack_24
                }
            )
        }
    }

    private fun setupFavoriteButton() {
        updateFavoriteIcon()
        binding.btnFavorite.setOnClickListener {
            viewModel.toggleFavorite(currentRecipe)
            isFavorite = !isFavorite
            currentRecipe = currentRecipe.copy(isFavorite = isFavorite)
            updateFavoriteIcon()
        }
    }

    private fun updateFavoriteIcon() {
        if (isFavorite) {
            binding.btnFavorite.setImageResource(R.drawable.ic_favorite_filled_24)
            binding.btnFavorite.imageTintList = ColorStateList.valueOf(
                ContextCompat.getColor(requireContext(), R.color.color_favorite_active)
            )
        } else {
            binding.btnFavorite.setImageResource(R.drawable.ic_favorite_border_24)
            binding.btnFavorite.imageTintList = ColorStateList.valueOf(
                ContextCompat.getColor(requireContext(), R.color.md_theme_light_onSurfaceVariant)
            )
        }
    }

    private fun setupIngredientsRecyclerView() {
        ingredientAdapter = RecipeIngredientDetailAdapter()
        binding.recyclerIngredients.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = ingredientAdapter
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
                    "mealType" to recipe.mealType.name,
                    "isFavorite" to recipe.isFavorite
                )
            }
        }
    }
}
