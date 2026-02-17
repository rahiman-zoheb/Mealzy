package com.example.mealzy.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mealzy.R
import com.example.mealzy.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupUI()
        observeViewModel()

        return root
    }

    private lateinit var suggestedRecipesAdapter: SuggestedRecipesAdapter

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
            recyclerViewUpcomingMeals.layoutManager = LinearLayoutManager(context)

            // Setup RecyclerView for suggested recipes (horizontal)
            suggestedRecipesAdapter = SuggestedRecipesAdapter { recipeMatch ->
                // TODO: Navigate to recipe detail
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

        // Observe upcoming meals
        homeViewModel.upcomingMeals.observe(viewLifecycleOwner) { meals ->
            if (meals.isEmpty()) {
                binding.textNoUpcomingMeals.visibility = View.VISIBLE
                binding.recyclerViewUpcomingMeals.visibility = View.GONE
            } else {
                binding.textNoUpcomingMeals.visibility = View.GONE
                binding.recyclerViewUpcomingMeals.visibility = View.VISIBLE
                // TODO: Setup adapter with meals
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