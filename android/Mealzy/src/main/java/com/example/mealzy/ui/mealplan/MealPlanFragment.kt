package com.example.mealzy.ui.mealplan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mealzy.databinding.FragmentMealPlanBinding

class MealPlanFragment : Fragment() {

    private var _binding: FragmentMealPlanBinding? = null
    private val binding get() = _binding!!

    private lateinit var mealPlanViewModel: MealPlanViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mealPlanViewModel = ViewModelProvider(this)[MealPlanViewModel::class.java]

        _binding = FragmentMealPlanBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupUI()
        observeViewModel()

        return root
    }

    private fun setupUI() {
        binding.recyclerViewMealPlan.layoutManager = LinearLayoutManager(context)
        
        binding.fabAddMealPlan.setOnClickListener {
            // TODO: Show add meal plan dialog
        }
    }

    private fun observeViewModel() {
        mealPlanViewModel.weeklyMealPlan.observe(viewLifecycleOwner) { mealPlans ->
            // TODO: Update RecyclerView with meal plans
            if (mealPlans.isEmpty()) {
                binding.textNoMealPlan.visibility = View.VISIBLE
                binding.recyclerViewMealPlan.visibility = View.GONE
            } else {
                binding.textNoMealPlan.visibility = View.GONE
                binding.recyclerViewMealPlan.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}