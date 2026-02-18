package com.example.mealzy.ui.mealplan

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mealzy.R
import com.example.mealzy.data.model.MealPlan
import com.example.mealzy.data.model.MealType
import com.example.mealzy.data.model.Recipe
import com.example.mealzy.databinding.DialogAddMealPlanBinding
import com.example.mealzy.databinding.ItemMealPickerRecipeBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddMealPlanDialog(
    private val date: Date,
    private val mealType: MealType,
    private val recipes: List<Recipe>,
    private val onMealAdded: (MealPlan) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: DialogAddMealPlanBinding? = null
    private val binding get() = _binding!!

    private lateinit var pickerAdapter: MealPickerAdapter
    private var selectedRecipe: Recipe? = null
    private var servingsCount = 2

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddMealPlanBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBanner()
        setupRecipeList()
        setupServingsStepper()
        setupAddButton()
    }

    private fun setupBanner() {
        val dateFormat = SimpleDateFormat("EEEE, MMM d", Locale.getDefault())
        binding.textMealSlotInfo.text = dateFormat.format(date)

        val mealTypeName = mealType.name.lowercase().replaceFirstChar { it.uppercase() }
        binding.textDialogTitle.text = getString(R.string.add_meal_title, mealTypeName)

        binding.viewGradientBanner.setBackgroundResource(
            when (mealType) {
                MealType.BREAKFAST -> R.drawable.gradient_breakfast
                MealType.LUNCH -> R.drawable.gradient_lunch
                MealType.DINNER -> R.drawable.gradient_dinner
                MealType.SNACK -> R.drawable.gradient_snack
            }
        )
        binding.iconMealType.setImageResource(
            when (mealType) {
                MealType.BREAKFAST -> R.drawable.ic_breakfast_24
                MealType.LUNCH -> R.drawable.ic_lunch_24
                MealType.DINNER -> R.drawable.ic_dinner_24
                MealType.SNACK -> R.drawable.ic_snack_24
            }
        )
    }

    private fun setupRecipeList() {
        pickerAdapter = MealPickerAdapter(mealType) { recipe ->
            selectedRecipe = recipe
        }
        binding.recyclerRecipes.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = pickerAdapter
        }
        pickerAdapter.submitList(recipes)
        // Pre-select first recipe
        if (recipes.isNotEmpty()) {
            selectedRecipe = recipes.first()
            pickerAdapter.setSelected(recipes.first())
        }
    }

    private fun setupServingsStepper() {
        binding.textServingsCount.text = servingsCount.toString()
        binding.btnServingsMinus.setOnClickListener {
            if (servingsCount > 1) {
                servingsCount--
                binding.textServingsCount.text = servingsCount.toString()
            }
        }
        binding.btnServingsPlus.setOnClickListener {
            servingsCount++
            binding.textServingsCount.text = servingsCount.toString()
        }
    }

    private fun setupAddButton() {
        binding.btnAddToPlan.setOnClickListener {
            val recipe = selectedRecipe
            if (recipe == null) {
                Toast.makeText(context, getString(R.string.select_recipe), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            onMealAdded(
                MealPlan(
                    recipeId = recipe.id,
                    date = date,
                    mealType = mealType,
                    servings = servingsCount
                )
            )
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // ── Inner adapter ──────────────────────────────────────────────────────────

    private class MealPickerAdapter(
        private val mealType: MealType,
        private val onSelected: (Recipe) -> Unit
    ) : ListAdapter<Recipe, MealPickerAdapter.ViewHolder>(DiffCallback()) {

        private var selectedId: Long = -1L

        fun setSelected(recipe: Recipe) {
            val old = currentList.indexOfFirst { it.id == selectedId }
            selectedId = recipe.id
            val new = currentList.indexOfFirst { it.id == selectedId }
            if (old >= 0) notifyItemChanged(old)
            if (new >= 0) notifyItemChanged(new)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ItemMealPickerRecipeBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(getItem(position), getItem(position).id == selectedId)
        }

        inner class ViewHolder(
            private val binding: ItemMealPickerRecipeBinding
        ) : RecyclerView.ViewHolder(binding.root) {

            fun bind(recipe: Recipe, isSelected: Boolean) {
                binding.textRecipeName.text = recipe.name
                val totalTime = recipe.prepTimeMinutes + recipe.cookTimeMinutes
                binding.textRecipeInfo.text = "${recipe.difficulty} · ${totalTime} min"

                // Color bar matches meal type
                val colorRes = when (mealType) {
                    MealType.BREAKFAST -> R.color.breakfast_color
                    MealType.LUNCH -> R.color.lunch_color
                    MealType.DINNER -> R.color.dinner_color
                    MealType.SNACK -> R.color.snack_color
                }
                binding.viewTypeBar.backgroundTintList = ColorStateList.valueOf(
                    ContextCompat.getColor(binding.root.context, colorRes)
                )

                binding.iconSelected.visibility = if (isSelected) View.VISIBLE else View.GONE

                binding.root.setOnClickListener {
                    onSelected(recipe)
                    setSelected(recipe)
                }
            }
        }

        private class DiffCallback : DiffUtil.ItemCallback<Recipe>() {
            override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe) =
                oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe) =
                oldItem == newItem
        }
    }
}
