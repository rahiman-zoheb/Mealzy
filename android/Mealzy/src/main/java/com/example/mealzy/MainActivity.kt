package com.example.mealzy

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.mealzy.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val bottomNav = findViewById<BottomNavigationView>(R.id.nav_view)

        // Set up bottom navigation with proper back stack handling
        setupBottomNavigation(bottomNav, navController)
    }

    private fun setupBottomNavigation(bottomNav: BottomNavigationView?, navController: NavController) {
        bottomNav?.setOnItemSelectedListener { item ->
            // Build NavOptions to properly handle back stack for bottom navigation
            val navOptions = NavOptions.Builder()
                .setLaunchSingleTop(true)
                .setPopUpTo(R.id.navigation_home, inclusive = false, saveState = true)
                .setRestoreState(true)
                .build()

            try {
                navController.navigate(item.itemId, null, navOptions)
                true
            } catch (e: IllegalArgumentException) {
                false
            }
        }

        // Update selected item when navigation changes
        navController.addOnDestinationChangedListener { _, destination, _ ->
            bottomNav?.menu?.findItem(destination.id)?.isChecked = true
        }
    }
}
