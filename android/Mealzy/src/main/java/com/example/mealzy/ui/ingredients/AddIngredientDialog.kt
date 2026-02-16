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
    private val onIngredientAdded: (Ingredient) -> Unit
) : DialogFragment() {

    private var _binding: DialogAddIngredientBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogAddIngredientBinding.inflate(layoutInflater)

        setupUI()

        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.add_new_ingredient)
            .setView(binding.root)
            .setPositiveButton(R.string.save) { _, _ ->
                saveIngredient()
            }
            .setNegativeButton(R.string.cancel, null)
            .create()
    }

    private fun setupUI() {
        // Setup category spinner
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

        // Setup unit spinner
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

        val ingredient = Ingredient(
            name = name,
            quantity = quantity,
            unit = unit,
            category = category,
            isAvailable = true
        )

        onIngredientAdded(ingredient)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}