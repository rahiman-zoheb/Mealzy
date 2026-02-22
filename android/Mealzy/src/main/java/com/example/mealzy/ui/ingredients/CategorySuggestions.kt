package com.example.mealzy.ui.ingredients

object CategorySuggestions {
    private val keywords = mapOf(
        // Protein
        "chicken" to "Protein", "beef" to "Protein", "pork" to "Protein",
        "fish" to "Protein", "salmon" to "Protein", "tuna" to "Protein",
        "shrimp" to "Protein", "egg" to "Protein", "tofu" to "Protein",
        // Vegetables
        "carrot" to "Vegetables", "onion" to "Vegetables", "garlic" to "Vegetables",
        "tomato" to "Vegetables", "pepper" to "Vegetables", "broccoli" to "Vegetables",
        "spinach" to "Vegetables", "lettuce" to "Vegetables", "potato" to "Vegetables",
        // Fruits
        "apple" to "Fruits", "banana" to "Fruits", "orange" to "Fruits",
        "lemon" to "Fruits", "lime" to "Fruits", "strawberry" to "Fruits",
        // Grains
        "rice" to "Grains", "pasta" to "Grains", "bread" to "Grains",
        "flour" to "Grains", "oat" to "Grains", "quinoa" to "Grains",
        // Dairy
        "milk" to "Dairy", "cheese" to "Dairy", "butter" to "Dairy",
        "yogurt" to "Dairy", "cream" to "Dairy",
        // Condiments
        "oil" to "Condiments", "vinegar" to "Condiments", "sauce" to "Condiments",
        "ketchup" to "Condiments", "mayo" to "Condiments", "mustard" to "Condiments",
        // Spices
        "salt" to "Spices", "cumin" to "Spices",
        "paprika" to "Spices", "cinnamon" to "Spices", "oregano" to "Spices"
    )

    fun suggestCategory(name: String): String? =
        keywords.entries.firstOrNull { (keyword, _) ->
            name.lowercase().contains(keyword)
        }?.value
}
