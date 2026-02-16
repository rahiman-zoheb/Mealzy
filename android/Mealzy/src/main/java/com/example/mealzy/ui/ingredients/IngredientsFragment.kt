package com.example.mealzy.ui.ingredients

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mealzy.databinding.FragmentIngredientsBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton

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

        return root
    }

    private fun setupUI() {
        // Setup RecyclerView
        ingredientsAdapter = IngredientsAdapter(
            onIngredientClick = { ingredient ->
                // Handle ingredient click - show details or edit
                // TODO: Implement ingredient detail/edit
            },
            onAvailabilityToggle = { ingredient ->
                ingredientsViewModel.toggleIngredientAvailability(ingredient)
            }
        )

        binding.recyclerViewIngredients.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = ingredientsAdapter
        }

        // Setup FAB
        binding.fabAddIngredient.setOnClickListener {
            // Show add ingredient dialog
            showAddIngredientDialog()
        }

        // Setup category filter (if needed)
        // TODO: Implement category filter
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

        ingredientsViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun showAddIngredientDialog() {
        val dialog = AddIngredientDialog { ingredient ->
            ingredientsViewModel.addIngredient(ingredient)
        }
        dialog.show(parentFragmentManager, "AddIngredientDialog")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}