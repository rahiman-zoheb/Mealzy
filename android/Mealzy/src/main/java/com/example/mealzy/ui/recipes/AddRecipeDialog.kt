package com.example.mealzy.ui.recipes

import android.app.Dialog
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.mealzy.R
import com.example.mealzy.data.model.MealType
import com.example.mealzy.data.model.Recipe
import com.example.mealzy.databinding.DialogAddRecipeBinding

class AddRecipeDialog(
    private val onRecipeSaved: (Recipe) -> Unit
) : DialogFragment() {

    private var _binding: DialogAddRecipeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogAddRecipeBinding.inflate(layoutInflater)

        setupUI()

        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.add_recipe)
            .setView(binding.root)
            .setPositiveButton(R.string.save) { _, _ ->
                saveRecipe()
            }
            .setNegativeButton(R.string.cancel, null)
            .create()
    }

    private fun setupUI() {
        val difficulties = arrayOf("Easy", "Medium", "Hard")
        binding.spinnerDifficulty.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            difficulties
        ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        val mealTypes = arrayOf("Breakfast", "Lunch", "Dinner", "Snack")
        binding.spinnerMealType.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            mealTypes
        ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }
    }

    private fun saveRecipe() {
        val name = binding.editTextRecipeName.text.toString().trim()
        if (name.isEmpty()) {
            binding.editTextRecipeName.error = getString(R.string.recipe_name_required)
            return
        }

        val mealType = when (binding.spinnerMealType.selectedItemPosition) {
            0 -> MealType.BREAKFAST
            1 -> MealType.LUNCH
            2 -> MealType.DINNER
            else -> MealType.SNACK
        }

        onRecipeSaved(
            Recipe(
                name = name,
                description = binding.editTextDescription.text.toString().trim(),
                instructions = binding.editTextInstructions.text.toString().trim(),
                prepTimeMinutes = binding.editTextPrepTime.text.toString().toIntOrNull() ?: 0,
                cookTimeMinutes = binding.editTextCookTime.text.toString().toIntOrNull() ?: 0,
                servings = binding.editTextServings.text.toString().toIntOrNull() ?: 1,
                difficulty = binding.spinnerDifficulty.selectedItem.toString(),
                mealType = mealType
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
