package com.example.mealzy.ui.mealplan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mealzy.data.model.MealPlan
import com.example.mealzy.data.model.MealType
import com.example.mealzy.data.model.Recipe
import com.example.mealzy.databinding.FragmentMealPlanBinding
import com.example.mealzy.ui.recipes.RecipeDetailBottomSheet

class MealPlanFragment : Fragment() {

    private var _binding: FragmentMealPlanBinding? = null
    private val binding get() = _binding!!

    private lateinit var mealPlanViewModel: MealPlanViewModel
    private lateinit var calendarDayAdapter: CalendarDayAdapter
    private var availableRecipes: List<Recipe> = emptyList()

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
        calendarDayAdapter = CalendarDayAdapter(
            onAddMealClick = { day, mealType ->
                showAddMealDialog(day.date, mealType)
            },
            onMealClick = { meal ->
                showMealOptionsDialog(meal)
            }
        )

        binding.recyclerViewMealPlan.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = calendarDayAdapter
        }

        binding.buttonPrevWeek.setOnClickListener {
            mealPlanViewModel.navigateWeek(-1)
        }

        binding.buttonNextWeek.setOnClickListener {
            mealPlanViewModel.navigateWeek(1)
        }

        binding.buttonToday.setOnClickListener {
            mealPlanViewModel.goToToday()
            binding.recyclerViewMealPlan.scrollToPosition(0)
        }

        binding.fabAddMealPlan.setOnClickListener {
            val today = java.util.Calendar.getInstance().time
            showAddMealDialog(today, MealType.DINNER)
        }
    }

    private fun observeViewModel() {
        mealPlanViewModel.weekRangeText.observe(viewLifecycleOwner) { weekRange ->
            binding.textWeekRange.text = weekRange
        }

        mealPlanViewModel.calendarDays.observe(viewLifecycleOwner) { days ->
            calendarDayAdapter.submitList(days)
        }

        mealPlanViewModel.allRecipes.observe(viewLifecycleOwner) { recipes ->
            availableRecipes = recipes
        }
    }

    private fun showAddMealDialog(date: java.util.Date, mealType: MealType) {
        if (availableRecipes.isEmpty()) {
            Toast.makeText(context, "No recipes available. Add recipes first.", Toast.LENGTH_SHORT).show()
            return
        }
        AddMealPlanDialog(date, mealType, availableRecipes) { mealPlan ->
            mealPlanViewModel.addMealPlan(mealPlan)
        }.show(parentFragmentManager, "AddMealPlanDialog")
    }

    private fun showMealOptionsDialog(meal: MealPlan) {
        val recipe = availableRecipes.find { it.id == meal.recipeId } ?: return
        MealOptionsBottomSheet(
            meal = meal,
            recipe = recipe,
            onViewDetails = { r ->
                RecipeDetailBottomSheet.newInstance(r)
                    .show(parentFragmentManager, RecipeDetailBottomSheet.TAG)
            },
            onDelete = { mealPlanViewModel.deleteMealPlan(it) }
        ).show(parentFragmentManager, MealOptionsBottomSheet.TAG)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
