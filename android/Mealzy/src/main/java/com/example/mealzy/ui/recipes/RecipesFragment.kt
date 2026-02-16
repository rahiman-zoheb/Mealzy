package com.example.mealzy.ui.recipes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
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

        // Setup meal type filter spinner
        val mealTypes = arrayOf("All Meals", "Breakfast", "Lunch", "Dinner", "Snack")
        val spinnerAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, mealTypes)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerMealType.adapter = spinnerAdapter

        binding.spinnerMealType.setOnItemSelectedListener(object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: android.widget.AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedMealType = when (position) {
                    0 -> null // All meals
                    1 -> MealType.BREAKFAST
                    2 -> MealType.LUNCH
                    3 -> MealType.DINNER
                    4 -> MealType.SNACK
                    else -> null
                }
                recipesViewModel.filterByMealType(selectedMealType)
            }

            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {}
        })
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