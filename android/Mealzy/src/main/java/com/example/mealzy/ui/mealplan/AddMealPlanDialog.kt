package com.example.mealzy.ui.mealplan

import android.app.Dialog
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.mealzy.R
import com.example.mealzy.data.model.MealPlan
import com.example.mealzy.data.model.MealType
import com.example.mealzy.data.model.Recipe
import com.example.mealzy.databinding.DialogAddMealPlanBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddMealPlanDialog(
    private val date: Date,
    private val mealType: MealType,
    private val recipes: List<Recipe>,
    private val onMealAdded: (MealPlan) -> Unit
) : DialogFragment() {

    private var _binding: DialogAddMealPlanBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogAddMealPlanBinding.inflate(layoutInflater)

        val dateFormat = SimpleDateFormat("EEEE, MMM d", Locale.getDefault())
        val mealTypeName = mealType.name.lowercase()
            .replaceFirstChar { it.uppercase() }
        binding.textMealSlotInfo.text = "Adding $mealTypeName for ${dateFormat.format(date)}"

        val recipeNames = recipes.map { it.name }
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            recipeNames
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerRecipe.adapter = adapter

        val title = getString(R.string.add_meal_to_plan)

        return AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setView(binding.root)
            .setPositiveButton(R.string.save) { _, _ -> saveMealPlan() }
            .setNegativeButton(R.string.cancel, null)
            .create()
    }

    private fun saveMealPlan() {
        val selectedIndex = binding.spinnerRecipe.selectedItemPosition
        if (selectedIndex < 0 || selectedIndex >= recipes.size) return

        val recipe = recipes[selectedIndex]
        val servingsText = binding.editTextServings.text.toString().trim()
        val servings = servingsText.toIntOrNull()?.coerceAtLeast(1) ?: 1

        val mealPlan = MealPlan(
            recipeId = recipe.id,
            date = date,
            mealType = mealType,
            servings = servings
        )
        onMealAdded(mealPlan)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
