package com.example.mealzy.ui.ingredients

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import com.example.mealzy.R
import com.example.mealzy.data.model.Ingredient
import com.example.mealzy.databinding.BottomSheetAddIngredientBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddIngredientDialog(
    private val existingIngredient: Ingredient? = null,
    private val onIngredientSaved: (Ingredient) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: BottomSheetAddIngredientBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetAddIngredientBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        binding.btnSave.setOnClickListener { saveIngredient() }
        binding.btnCancel.setOnClickListener { dismiss() }
    }

    private fun setupUI() {
        binding.textSheetTitle.setText(
            if (existingIngredient != null) R.string.edit else R.string.add_new_ingredient
        )

        val units = arrayOf(
            "pieces", "cups", "lbs", "kg", "grams", "oz",
            "liters", "ml", "tbsp", "tsp", "bottles", "cans"
        )
        val unitAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, units)
        binding.actvUnit.setAdapter(unitAdapter)
        binding.actvUnit.setText(units[0], false)

        val categories = arrayOf(
            "Protein", "Vegetables", "Fruits", "Grains",
            "Dairy", "Condiments", "Spices", "Other"
        )
        val categoryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, categories)
        binding.actvCategory.setAdapter(categoryAdapter)
        binding.actvCategory.setText(categories[0], false)

        // Pre-fill fields when editing
        existingIngredient?.let { ingredient ->
            binding.etName.setText(ingredient.name)
            binding.etQuantity.setText(ingredient.quantity)
            binding.actvUnit.setText(ingredient.unit, false)
            binding.actvCategory.setText(ingredient.category, false)
        }

        // Auto-categorization on name entry (new ingredients only)
        binding.etName.addTextChangedListener { text ->
            val suggestion = CategorySuggestions.suggestCategory(text.toString())
            if (suggestion != null && existingIngredient == null) {
                binding.actvCategory.setText(suggestion, false)
            }
        }
    }

    private fun saveIngredient() {
        val name = binding.etName.text.toString().trim()
        val quantity = binding.etQuantity.text.toString().trim()
        val unit = binding.actvUnit.text.toString()
        val category = binding.actvCategory.text.toString()

        if (name.isEmpty()) {
            binding.tilName.error = getString(R.string.error_name_required)
            return
        }
        binding.tilName.error = null

        if (quantity.isEmpty()) {
            binding.tilQuantity.error = getString(R.string.error_quantity_required)
            return
        }
        val quantityDouble = quantity.toDoubleOrNull()
        if (quantityDouble == null || quantityDouble <= 0.0) {
            binding.tilQuantity.error = getString(R.string.error_quantity_positive)
            return
        }
        binding.tilQuantity.error = null

        val ingredient = if (existingIngredient != null) {
            existingIngredient.copy(name = name, quantity = quantity, unit = unit, category = category)
        } else {
            Ingredient(name = name, quantity = quantity, unit = unit, category = category, isAvailable = true)
        }

        onIngredientSaved(ingredient)
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
