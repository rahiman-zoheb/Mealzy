package com.example.mealzy.ui.recipes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mealzy.R
import com.example.mealzy.data.model.MealType
import com.example.mealzy.databinding.FragmentRecipesBinding

class RecipesFragment : Fragment() {

    private var _binding: FragmentRecipesBinding? = null
    private val binding get() = _binding!!

    private lateinit var recipesViewModel: RecipesViewModel
    private lateinit var recipesAdapter: RecipesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        recipesViewModel = ViewModelProvider(this)[RecipesViewModel::class.java]

        _binding = FragmentRecipesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupUI()
        observeViewModel()

        return root
    }

    private fun setupUI() {
        // Setup RecyclerView
        recipesAdapter = RecipesAdapter { recipe ->
            // Handle recipe click
            // TODO: Navigate to recipe detail
        }

        binding.recyclerViewRecipes.apply {
            layoutManager = GridLayoutManager(context, 2)
            adapter = recipesAdapter
        }

        // Setup search
        binding.searchViewRecipes.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { recipesViewModel.searchRecipes(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    recipesViewModel.clearSearch()
                }
                return true
            }
        })

        // Setup meal type filter chips
        binding.chipGroupMealType.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                val selectedMealType = when (checkedIds[0]) {
                    R.id.chip_all -> null // All meals
                    R.id.chip_breakfast -> MealType.BREAKFAST
                    R.id.chip_lunch -> MealType.LUNCH
                    R.id.chip_dinner -> MealType.DINNER
                    R.id.chip_snack -> MealType.SNACK
                    else -> null
                }
                recipesViewModel.filterByMealType(selectedMealType)
            }
        }
    }

    private fun observeViewModel() {
        recipesViewModel.recipes.observe(viewLifecycleOwner) { recipes ->
            recipesAdapter.submitList(recipes)
            
            if (recipes.isEmpty()) {
                binding.textNoRecipes.visibility = View.VISIBLE
                binding.recyclerViewRecipes.visibility = View.GONE
            } else {
                binding.textNoRecipes.visibility = View.GONE
                binding.recyclerViewRecipes.visibility = View.VISIBLE
            }
        }

        recipesViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}