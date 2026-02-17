package com.example.mealzy.ui.ingredients

import android.app.Dialog
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.mealzy.R
import com.example.mealzy.data.model.Ingredient
import com.example.mealzy.databinding.DialogAddIngredientBinding

class AddIngredientDialog(
    private val existingIngredient: Ingredient? = null,
    private val onIngredientSaved: (Ingredient) -> Unit
) : DialogFragment() {

    private var _binding: DialogAddIngredientBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogAddIngredientBinding.inflate(layoutInflater)

        setupUI()

        val title = if (existingIngredient != null) R.string.edit else R.string.add_new_ingredient

        return AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setView(binding.root)
            .setPositiveButton(R.string.save) { _, _ ->
                saveIngredient()
            }
            .setNegativeButton(R.string.cancel, null)
            .create()
    }

    private fun setupUI() {
        val categories = arrayOf(
            "Protein", "Vegetables", "Fruits", "Grains",
            "Dairy", "Condiments", "Spices", "Other"
        )
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            categories
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = adapter

        val units = arrayOf(
            "pieces", "cups", "lbs", "kg", "grams", "oz",
            "liters", "ml", "tbsp", "tsp", "bottles", "cans"
        )
        val unitAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            units
        )
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerUnit.adapter = unitAdapter

        // Pre-fill fields when editing
        existingIngredient?.let { ingredient ->
            binding.editTextName.setText(ingredient.name)
            binding.editTextQuantity.setText(ingredient.quantity)
            val unitIndex = units.indexOf(ingredient.unit)
            if (unitIndex >= 0) binding.spinnerUnit.setSelection(unitIndex)
            val categoryIndex = categories.indexOf(ingredient.category)
            if (categoryIndex >= 0) binding.spinnerCategory.setSelection(categoryIndex)
        }
    }

    private fun saveIngredient() {
        val name = binding.editTextName.text.toString().trim()
        val quantity = binding.editTextQuantity.text.toString().trim()
        val unit = binding.spinnerUnit.selectedItem.toString()
        val category = binding.spinnerCategory.selectedItem.toString()

        if (name.isEmpty()) {
            binding.editTextName.error = "Name is required"
            return
        }

        if (quantity.isEmpty()) {
            binding.editTextQuantity.error = "Quantity is required"
            return
        }

        val ingredient = if (existingIngredient != null) {
            existingIngredient.copy(name = name, quantity = quantity, unit = unit, category = category)
        } else {
            Ingredient(name = name, quantity = quantity, unit = unit, category = category, isAvailable = true)
        }

        onIngredientSaved(ingredient)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}