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
import com.example.mealzy.databinding.FragmentMealPlanBinding

class MealPlanFragment : Fragment() {

    private var _binding: FragmentMealPlanBinding? = null
    private val binding get() = _binding!!

    private lateinit var mealPlanViewModel: MealPlanViewModel
    private lateinit var calendarDayAdapter: CalendarDayAdapter

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
        // Setup calendar RecyclerView
        calendarDayAdapter = CalendarDayAdapter(
            onAddMealClick = { day, mealType ->
                // TODO: Show recipe picker dialog
                Toast.makeText(
                    context,
                    "Add ${mealType.name.lowercase()} for ${day.dayName}",
                    Toast.LENGTH_SHORT
                ).show()
            },
            onMealClick = { meal ->
                // TODO: Show meal details/options dialog
                Toast.makeText(
                    context,
                    "Meal clicked: Recipe ID ${meal.recipeId}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )

        binding.recyclerViewMealPlan.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = calendarDayAdapter
        }

        // Week navigation buttons
        binding.buttonPrevWeek.setOnClickListener {
            mealPlanViewModel.navigateWeek(-1)
        }

        binding.buttonNextWeek.setOnClickListener {
            mealPlanViewModel.navigateWeek(1)
        }

        binding.buttonToday.setOnClickListener {
            mealPlanViewModel.goToToday()
            // Scroll to first position (which should include today)
            binding.recyclerViewMealPlan.scrollToPosition(0)
        }

        binding.fabAddMealPlan.setOnClickListener {
            // TODO: Show add meal plan dialog with date picker
            Toast.makeText(context, "Add meal plan", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeViewModel() {
        // Observe week range text
        mealPlanViewModel.weekRangeText.observe(viewLifecycleOwner) { weekRange ->
            binding.textWeekRange.text = weekRange
        }

        // Observe calendar days
        mealPlanViewModel.calendarDays.observe(viewLifecycleOwner) { days ->
            calendarDayAdapter.submitList(days)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}