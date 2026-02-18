package com.example.mealzy.ui.mealplan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mealzy.R
import com.example.mealzy.data.model.MealPlan
import com.example.mealzy.data.model.MealType
import com.example.mealzy.data.model.Recipe
import com.example.mealzy.databinding.DialogMealOptionsBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MealOptionsBottomSheet(
    private val meal: MealPlan,
    private val recipe: Recipe,
    private val onViewDetails: (Recipe) -> Unit,
    private val onDelete: (MealPlan) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: DialogMealOptionsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogMealOptionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.textRecipeName.text = recipe.name

        binding.chipMealType.text = when (meal.mealType) {
            MealType.BREAKFAST -> getString(R.string.breakfast)
            MealType.LUNCH -> getString(R.string.lunch)
            MealType.DINNER -> getString(R.string.dinner)
            MealType.SNACK -> getString(R.string.snack)
        }

        binding.textServingsInfo.text = getString(R.string.servings_count, meal.servings)

        binding.optionViewDetails.setOnClickListener {
            dismiss()
            onViewDetails(recipe)
        }

        binding.optionDelete.setOnClickListener {
            onDelete(meal)
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "MealOptionsBottomSheet"
    }
}
