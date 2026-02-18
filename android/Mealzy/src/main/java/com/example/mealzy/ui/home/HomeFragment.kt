package com.example.mealzy.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mealzy.R
import com.example.mealzy.databinding.FragmentHomeBinding
import com.google.android.material.transition.MaterialFadeThrough

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        enterTransition = MaterialFadeThrough()
        exitTransition = MaterialFadeThrough()
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupUI()
        observeViewModel()

        return root
    }

    private lateinit var suggestedRecipesAdapter: SuggestedRecipesAdapter
    private lateinit var upcomingMealsAdapter: UpcomingMealsAdapter

    private fun setupUI() {
        binding.apply {
            // Setup stat card icons
            statIngredients.iconStat.setImageResource(R.drawable.ic_local_grocery_store_24)
            statRecipes.iconStat.setImageResource(R.drawable.ic_restaurant_24)
            statMeals.iconStat.setImageResource(R.drawable.ic_event_24)

            // Setup quick actions
            cardAddIngredient.setOnClickListener {
                val bundle = bundleOf("showAddDialog" to true)
                findNavController().navigate(R.id.action_home_to_ingredients, bundle)
            }

            cardBrowseRecipes.setOnClickListener {
                findNavController().navigate(R.id.action_home_to_recipes)
            }

            cardPlanMeals.setOnClickListener {
                findNavController().navigate(R.id.action_home_to_meal_plan)
            }

            // Setup stat card click listeners
            statIngredients.root.setOnClickListener {
                findNavController().navigate(R.id.action_home_to_ingredients)
            }

            statRecipes.root.setOnClickListener {
                findNavController().navigate(R.id.action_home_to_recipes)
            }

            statMeals.root.setOnClickListener {
                findNavController().navigate(R.id.action_home_to_meal_plan)
            }

            // Setup RecyclerView for upcoming meals
            upcomingMealsAdapter = UpcomingMealsAdapter()
            recyclerViewUpcomingMeals.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = upcomingMealsAdapter
            }

            // Setup RecyclerView for suggested recipes (horizontal)
            suggestedRecipesAdapter = SuggestedRecipesAdapter { recipeMatch ->
                com.example.mealzy.ui.recipes.RecipeDetailBottomSheet.newInstance(recipeMatch.recipe)
                    .show(parentFragmentManager, com.example.mealzy.ui.recipes.RecipeDetailBottomSheet.TAG)
            }
            recyclerViewSuggestedRecipes.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = suggestedRecipesAdapter
            }
        }
    }

    private fun observeViewModel() {
        // Observe welcome message
        homeViewModel.welcomeMessage.observe(viewLifecycleOwner) { message ->
            binding.textWelcome.text = message
        }

        // Observe stats
        homeViewModel.stats.observe(viewLifecycleOwner) { stats ->
            binding.apply {
                // Update stat card values
                statIngredients.apply {
                    textStatNumber.text = stats.ingredientCount.toString()
                    textStatLabel.text = getString(R.string.title_ingredients)
                }

                statRecipes.apply {
                    textStatNumber.text = stats.recipeCount.toString()
                    textStatLabel.text = getString(R.string.title_recipes)
                }

                statMeals.apply {
                    textStatNumber.text = stats.weeklyMealCount.toString()
                    textStatLabel.text = "Meals This Week"
                }
            }
        }

        // Observe upcoming meals with recipe names
        homeViewModel.upcomingMealItems.observe(viewLifecycleOwner) { items ->
            if (items.isEmpty()) {
                binding.textNoUpcomingMeals.visibility = View.VISIBLE
                binding.recyclerViewUpcomingMeals.visibility = View.GONE
            } else {
                binding.textNoUpcomingMeals.visibility = View.GONE
                binding.recyclerViewUpcomingMeals.visibility = View.VISIBLE
                upcomingMealsAdapter.submitList(items)
            }
        }

        // Observe suggested recipes
        homeViewModel.suggestedRecipes.observe(viewLifecycleOwner) { recipes ->
            if (recipes.isEmpty()) {
                binding.textSuggestedRecipes.visibility = View.GONE
                binding.recyclerViewSuggestedRecipes.visibility = View.GONE
            } else {
                binding.textSuggestedRecipes.visibility = View.VISIBLE
                binding.recyclerViewSuggestedRecipes.visibility = View.VISIBLE
                suggestedRecipesAdapter.submitList(recipes)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}