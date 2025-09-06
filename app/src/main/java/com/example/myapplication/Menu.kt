package com.example.myapplication

// Data classes for menu items
data class MenuItem(
    val name: String,
    val category: String,
    val price: Double = 0.00,
    val imageResId: Int = R.drawable.placeholder
)


// Main menu manager class
class MilkTeaMenuManager {
    // Categories
    val categories = listOf(
        "Fresh Ice Cream with Tea",
        "Milk Tea",
        "Fruit Tea",
        "Coffee"
    )

    // Menu items organized by category
    private val menuItems = listOf(
        // Fresh Ice Cream with Tea sub-items
        MenuItem("Signature King Cone", "Fresh Ice Cream with Tea", 2.00, R.drawable.signature_king_cone),
        MenuItem("Chocolate Lucky Sundae", "Fresh Ice Cream with Tea", 6.00, R.drawable.chocolate_lucky_sundae),
        MenuItem("Strawberry Crispy Sundae", "Fresh Ice Cream with Tea", 6.00, R.drawable.strawberry_crispy_sundae),
        MenuItem("Early Grey Ice cream Tea", "Fresh Ice Cream with Tea", 5.00, R.drawable.early_grey_ice_cream),
        MenuItem("Super Boba Sundae", "Fresh Ice Cream with Tea",  6.00, R.drawable.super_boba_sundae),
        MenuItem("Boba Mi-Shake", "Fresh Ice Cream with Tea",  8.00, R.drawable.boba_mi_shake),
        MenuItem("Super Mango Sundae", "Fresh Ice Cream with Tea",  5.00, R.drawable.super_mango_sundae),
        MenuItem("O-Crispy Sundae", "Fresh Ice Cream with Tea",  6.00, R.drawable.o_crispy_sundae),
        MenuItem("Peach Mi-Shake", "Fresh Ice Cream with Tea", 5.00, R.drawable.peach_mi_shake),

        // Milk Tea category items
        MenuItem("Brown Sugar Milk Tea", "Milk Tea",  5.5, R.drawable.brown_sugar_milk_tea),
        MenuItem("Pearl Milk Tea", "Milk Tea",  5.00, R.drawable.pearl_milk_tea),
        MenuItem("Supreme Mixed Milk Tea", "Milk Tea",  7.0, R.drawable.supereme_milk_tea),
        MenuItem("Coconut Jelly Milk Tea", "Milk Tea",  5.5, R.drawable.coconut_jelly_milk_tea),
        MenuItem("O-Coco Milk Tea", "Milk Tea",  6.00, R.drawable.o_coco_milk_tea),
        MenuItem("Strawberry Creamy Drink", "Milk Tea",  4.5, R.drawable.strawberry_creamy_drink),
        MenuItem("Toffee Hazelnut Milk Tea", "Milk Tea",  5.5, R.drawable.toffee_hazelnut_milk_tea),
        MenuItem("Twin Topping Milk Tea", "Milk Tea",  6.5, R.drawable.twin_topping_milk_tea),
        MenuItem("Super Triple Milk Tea", "Milk Tea",  8.00, R.drawable.super_triple_milk_tea),

        // Fruit Tea items
        MenuItem("Fresh Lemonade", "Fruit Tea",  4.0, R.drawable.fresh_lemonade),
        MenuItem("Lemon Jasmine Tea", "Fruit Tea",  5.0, R.drawable.lemon_jasmine_tea),
        MenuItem("Passion Fruit Bubble Tea", "Fruit Tea",  7.0, R.drawable.passion_fruit_bubble_tea),
        MenuItem("Peach Jasmine Tea", "Fruit Tea",  7.0, R.drawable.peach_jasmine_tea),
        MenuItem("Peach Black Tea", "Fruit Tea",  7.0, R.drawable.peach_black_tea),
        MenuItem("Lemon Black Tea", "Fruit Tea",  4.0, R.drawable.lemon_black_tea),
        MenuItem("Kiwi Jasmine Tea", "Fruit Tea",  5.0, R.drawable.kiwi__jasmine_tea),

        // Coffee items
        MenuItem("Ice Cream Latte", "Coffee",  5.0, R.drawable.ice_cream_latte),
        MenuItem("Ice Cream Mocha", "Coffee",  5.0, R.drawable.ice_cream_mocha),
        MenuItem("Ice Cream Toffee Hazelnut Latte", "Coffee",  5.0, R.drawable.ice_cream_toffee_hazelnut_latte),
    )

    // Get all menu items
    fun getAllMenuItems(): List<MenuItem> {
        return menuItems
    }

    // Get items by specific category
    fun getItemsByCategory(category: String): List<MenuItem> {
        return menuItems.filter { it.category == category }
    }

    // Search menu items
    fun searchMenu(query: String): List<MenuItem> {
        return menuItems.filter {
            it.name.contains(query, ignoreCase = true) ||
                    it.category.contains(query, ignoreCase = true)
        }
    }
    fun getItemByName(name: String): MenuItem? {
        return menuItems.find { it.name == name }
    }
}