package com.example.mealzy.model;

public class Ingredient {
    private String name;
    private String quantity;

    public Ingredient(String name, String quantity) {
        this.name = name;
        this.quantity = quantity;
    }

    // Getters and setters
}

package com.example.mealzy.model;

public enum MealType {
    BREAKFAST,
    LUNCH,
    DINNER
}

package com.example.mealzy.model;

import java.util.List;

public class Recipe {
    private int id;
    private String name;
    private List<Ingredient> ingredients;
    private String instructions;

    public Recipe(int id, String name, List<Ingredient> ingredients, String instructions) {
        this.id = id;
        this.name = name;
        this.ingredients = ingredients;
        this.instructions = instructions;
    }

    // Getters and setters
}

package com.example.mealzy;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}