package com.example.mealzy.ui.ingredients

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mealzy.R
import com.example.mealzy.databinding.FragmentIngredientsBinding
import com.google.android.material.snackbar.Snackbar

class IngredientsFragment : Fragment() {

    private var _binding: FragmentIngredientsBinding? = null
    private val binding get() = _binding!!

    private lateinit var ingredientsViewModel: IngredientsViewModel
    private lateinit var ingredientsAdapter: IngredientsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        ingredientsViewModel = ViewModelProvider(this)[IngredientsViewModel::class.java]

        _binding = FragmentIngredientsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupUI()
        observeViewModel()

        val showAddDialog = arguments?.getBoolean("showAddDialog", false) ?: false
        if (showAddDialog && savedInstanceState == null) {
            binding.root.post { showAddIngredientDialog() }
            arguments?.putBoolean("showAddDialog", false)
        }

        return root
    }

    private fun setupUI() {
        ingredientsAdapter = IngredientsAdapter(
            onIngredientClick = { ingredient -> showEditIngredientDialog(ingredient) },
            onAvailabilityToggle = { ingredient ->
                ingredientsViewModel.toggleIngredientAvailability(ingredient)
            }
        )

        binding.recyclerViewIngredients.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = ingredientsAdapter
        }

        binding.searchViewIngredients.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false
            override fun onQueryTextChange(newText: String?): Boolean {
                ingredientsViewModel.setSearchQuery(newText ?: "")
                return true
            }
        })

        binding.chipGroupFilter.setOnCheckedStateChangeListener { _, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                val selectedMode = when (checkedIds[0]) {
                    R.id.chip_all -> FilterMode.ALL
                    R.id.chip_available -> FilterMode.AVAILABLE_ONLY
                    R.id.chip_out_of_stock -> FilterMode.OUT_OF_STOCK
                    else -> FilterMode.ALL
                }
                ingredientsViewModel.setFilterMode(selectedMode)
            }
        }

        binding.fabAddIngredient.setOnClickListener { showAddIngredientDialog() }

        setupSwipeActions()
    }

    private fun setupSwipeActions() {
        val deleteColor = ContextCompat.getColor(requireContext(), R.color.color_ingredient_unavailable)
        val markAvailableColor = ContextCompat.getColor(requireContext(), R.color.success_color)
        val markUnavailableColor = ContextCompat.getColor(requireContext(), R.color.warning_color)
        val deleteIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_delete_24)!!
        val toggleIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_check_24)!!
        val density = resources.displayMetrics.density
        val iconSize = (24 * density).toInt()
        val iconMargin = (20 * density).toInt()

        val swipeCallback = object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            private val paint = Paint().apply { isAntiAlias = true }

            override fun onMove(
                rv: RecyclerView,
                vh: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.absoluteAdapterPosition
                if (position == RecyclerView.NO_POSITION) return
                val ingredient = ingredientsAdapter.currentList[position]

                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        ingredientsViewModel.deleteIngredient(ingredient)
                        Snackbar.make(
                            binding.root,
                            getString(R.string.ingredient_deleted, ingredient.name),
                            Snackbar.LENGTH_LONG
                        )
                            .setAction(R.string.undo) {
                                ingredientsViewModel.addIngredient(ingredient)
                            }
                            .setAnchorView(binding.fabAddIngredient)
                            .show()
                    }
                    ItemTouchHelper.RIGHT -> {
                        ingredientsViewModel.toggleIngredientAvailability(ingredient)
                        ingredientsAdapter.notifyItemChanged(position)
                    }
                }
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float, dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView
                val itemHeight = itemView.bottom - itemView.top
                val iconTop = itemView.top + (itemHeight - iconSize) / 2
                val iconBottom = iconTop + iconSize

                when {
                    dX < 0 -> { // Swiping left → delete
                        paint.color = deleteColor
                        c.drawRect(
                            itemView.right + dX, itemView.top.toFloat(),
                            itemView.right.toFloat(), itemView.bottom.toFloat(),
                            paint
                        )
                        val iconRight = itemView.right - iconMargin
                        val iconLeft = iconRight - iconSize
                        deleteIcon.setTint(Color.WHITE)
                        deleteIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                        deleteIcon.draw(c)
                    }
                    dX > 0 -> { // Swiping right → toggle availability
                        val pos = viewHolder.absoluteAdapterPosition
                        val isAvailable = if (pos != RecyclerView.NO_POSITION &&
                            pos < ingredientsAdapter.currentList.size
                        ) {
                            ingredientsAdapter.currentList[pos].isAvailable
                        } else true

                        paint.color = if (isAvailable) markUnavailableColor else markAvailableColor
                        c.drawRect(
                            itemView.left.toFloat(), itemView.top.toFloat(),
                            itemView.left + dX, itemView.bottom.toFloat(),
                            paint
                        )
                        val iconLeft = itemView.left + iconMargin
                        val iconRight = iconLeft + iconSize
                        toggleIcon.setTint(Color.WHITE)
                        toggleIcon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
                        toggleIcon.draw(c)
                    }
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }

        ItemTouchHelper(swipeCallback).attachToRecyclerView(binding.recyclerViewIngredients)
    }

    private fun observeViewModel() {
        ingredientsViewModel.ingredients.observe(viewLifecycleOwner) { ingredients ->
            ingredientsAdapter.submitList(ingredients)
            binding.textNoIngredients.visibility =
                if (ingredients.isEmpty()) View.VISIBLE else View.GONE
            binding.recyclerViewIngredients.visibility =
                if (ingredients.isEmpty()) View.GONE else View.VISIBLE
        }
    }

    private fun showAddIngredientDialog() {
        AddIngredientDialog(onIngredientSaved = { ingredient ->
            ingredientsViewModel.addIngredient(ingredient)
        }).show(parentFragmentManager, "AddIngredientDialog")
    }

    private fun showEditIngredientDialog(ingredient: com.example.mealzy.data.model.Ingredient) {
        AddIngredientDialog(existingIngredient = ingredient, onIngredientSaved = { updated ->
            ingredientsViewModel.updateIngredient(updated)
        }).show(parentFragmentManager, "EditIngredientDialog")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
