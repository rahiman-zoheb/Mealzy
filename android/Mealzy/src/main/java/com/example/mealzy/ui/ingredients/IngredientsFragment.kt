package com.example.mealzy.ui.ingredients

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mealzy.R
import com.example.mealzy.databinding.FragmentIngredientsBinding

class IngredientsFragment : Fragment() {

    private var _binding: FragmentIngredientsBinding? = null
    private val binding get() = _binding!!

    private lateinit var ingredientsViewModel: IngredientsViewModel
    private lateinit var ingredientsAdapter: IngredientsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        ingredientsViewModel = ViewModelProvider(this)[IngredientsViewModel::class.java]

        _binding = FragmentIngredientsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupUI()
        observeViewModel()

        // Check if we should show the add dialog immediately
        val showAddDialog = arguments?.getBoolean("showAddDialog", false) ?: false
        if (showAddDialog && savedInstanceState == null) {
            // Post the dialog show to ensure the fragment is fully created
            binding.root.post {
                showAddIngredientDialog()
            }
            // Clear the argument to prevent showing again on configuration changes
            arguments?.putBoolean("showAddDialog", false)
        }

        return root
    }

    private fun setupUI() {
        // Setup RecyclerView
        ingredientsAdapter = IngredientsAdapter(
            onIngredientClick = { ingredient ->
                showEditIngredientDialog(ingredient)
            },
            onAvailabilityToggle = { ingredient ->
                ingredientsViewModel.toggleIngredientAvailability(ingredient)
            }
        )

        binding.recyclerViewIngredients.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = ingredientsAdapter
        }

        // Setup SearchView
        binding.searchViewIngredients.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                ingredientsViewModel.setSearchQuery(newText ?: "")
                return true
            }
        })

        // Setup filter chips
        binding.chipGroupFilter.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                val selectedMode = when (checkedIds[0]) {
                    R.id.chip_all -> FilterMode.ALL
                    R.id.chip_available -> FilterMode.AVAILABLE_ONLY
                    R.id.chip_out_of_stock -> FilterMode.OUT_OF_STOCK
                    else -> FilterMode.ALL
                }
                ingredientsViewModel.setFilterMode(selectedMode)
            }
        }

        // Setup FAB
        binding.fabAddIngredient.setOnClickListener {
            // Show add ingredient dialog
            showAddIngredientDialog()
        }
    }

    private fun observeViewModel() {
        ingredientsViewModel.ingredients.observe(viewLifecycleOwner) { ingredients ->
            ingredientsAdapter.submitList(ingredients)
            
            if (ingredients.isEmpty()) {
                binding.textNoIngredients.visibility = View.VISIBLE
                binding.recyclerViewIngredients.visibility = View.GONE
            } else {
                binding.textNoIngredients.visibility = View.GONE
                binding.recyclerViewIngredients.visibility = View.VISIBLE
            }
        }

    }

    private fun showAddIngredientDialog() {
        AddIngredientDialog(onIngredientSaved = { ingredient ->
            ingredientsViewModel.addIngredient(ingredient)
        }).show(parentFragmentManager, "AddIngredientDialog")
    }

    private fun showEditIngredientDialog(ingredient: com.example.mealzy.data.model.Ingredient) {
        AddIngredientDialog(existingIngredient = ingredient, onIngredientSaved = { updated ->
            ingredientsViewModel.updateIngredient(updated)
        }).show(parentFragmentManager, "EditIngredientDialog")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}