package com.example.mealzy.ui.mealplan

import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mealzy.data.model.MealPlan
import com.example.mealzy.data.model.MealType
import com.example.mealzy.data.model.Recipe
import com.example.mealzy.databinding.FragmentMealPlanBinding
import com.example.mealzy.databinding.ItemMealPlanEntryBinding
import com.example.mealzy.databinding.ItemMealSlotEmptyBinding
import com.google.android.material.transition.MaterialFadeThrough
import com.example.mealzy.ui.recipes.RecipeDetailBottomSheet
import java.text.SimpleDateFormat
import java.util.Locale

class MealPlanFragment : Fragment() {

    private var _binding: FragmentMealPlanBinding? = null
    private val binding get() = _binding!!

    private lateinit var mealPlanViewModel: MealPlanViewModel
    private lateinit var calendarDayAdapter: CalendarDayAdapter
    private var availableRecipes: List<Recipe> = emptyList()
    private var selectedDay: CalendarDay? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        enterTransition = MaterialFadeThrough()
        exitTransition = MaterialFadeThrough()
        mealPlanViewModel = ViewModelProvider(this)[MealPlanViewModel::class.java]

        _binding = FragmentMealPlanBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupUI()
        observeViewModel()

        return root
    }

    private fun setupUI() {
        calendarDayAdapter = CalendarDayAdapter { day ->
            mealPlanViewModel.selectDate(day.date)
        }

        binding.recyclerViewMealPlan.apply {
            layoutManager = GridLayoutManager(context, 7)
            adapter = calendarDayAdapter
        }

        binding.buttonPrevWeek.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            mealPlanViewModel.navigateWeek(-1)
        }

        binding.buttonNextWeek.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            mealPlanViewModel.navigateWeek(1)
        }

        binding.buttonToday.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            mealPlanViewModel.goToToday()
        }

        binding.fabAddMealPlan.setOnClickListener {
            it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            val day = selectedDay
            if (day != null) {
                showAddMealDialog(day.date, MealType.DINNER)
            } else {
                showAddMealDialog(java.util.Calendar.getInstance().time, MealType.DINNER)
            }
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
            // Refresh detail panel if a day is already selected (recipe names may have changed)
            selectedDay?.let { updateDayDetail(it) }
        }

        mealPlanViewModel.selectedDate.observe(viewLifecycleOwner) { date ->
            calendarDayAdapter.setSelectedDate(date)
        }

        mealPlanViewModel.selectedDayDetail.observe(viewLifecycleOwner) { day ->
            if (day != null) {
                selectedDay = day
                updateDayDetail(day)
            }
        }
    }

    private fun updateDayDetail(day: CalendarDay) {
        val fmt = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
        binding.textSelectedDay.text = fmt.format(day.date)

        setupDetailSlot(binding.containerDetailBreakfast, day, MealType.BREAKFAST)
        setupDetailSlot(binding.containerDetailLunch, day, MealType.LUNCH)
        setupDetailSlot(binding.containerDetailDinner, day, MealType.DINNER)
        setupDetailSlot(binding.containerDetailSnack, day, MealType.SNACK)
    }

    private fun setupDetailSlot(container: FrameLayout, day: CalendarDay, mealType: MealType) {
        container.removeAllViews()
        val meal = day.meals[mealType]
        if (meal != null) {
            val itemBinding = ItemMealPlanEntryBinding.inflate(layoutInflater, container, true)
            val recipeName = availableRecipes.find { it.id == meal.recipeId }?.name ?: "Unknown"
            itemBinding.textRecipeName.text = recipeName
            itemBinding.textMealTime.visibility = View.GONE
            itemBinding.root.setOnClickListener { showMealOptionsDialog(meal) }
        } else {
            val emptyBinding = ItemMealSlotEmptyBinding.inflate(layoutInflater, container, true)
            emptyBinding.root.setOnClickListener {
                it.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                showAddMealDialog(day.date, mealType)
            }
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
