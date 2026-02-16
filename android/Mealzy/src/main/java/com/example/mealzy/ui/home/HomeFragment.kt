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

    private fun setupUI() {
        binding.apply {
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

            // Setup RecyclerView for upcoming meals
            recyclerViewUpcomingMeals.layoutManager = LinearLayoutManager(context)

            // Setup RecyclerView for suggested recipes (horizontal)
            recyclerViewSuggestedRecipes.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun observeViewModel() {
        homeViewModel.todaysMeals.observe(viewLifecycleOwner) { meals ->
            // Update upcoming meals RecyclerView
            if (meals.isEmpty()) {
                binding.textNoUpcomingMeals.visibility = View.VISIBLE
                binding.recyclerViewUpcomingMeals.visibility = View.GONE
            } else {
                binding.textNoUpcomingMeals.visibility = View.GONE
                binding.recyclerViewUpcomingMeals.visibility = View.VISIBLE
                // TODO: Setup adapter with meals
            }
        }

        homeViewModel.welcomeMessage.observe(viewLifecycleOwner) { message ->
            binding.textWelcome.text = message
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}