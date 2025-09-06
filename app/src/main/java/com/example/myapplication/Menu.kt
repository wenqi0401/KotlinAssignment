package com.example.myapplication

// Data classes for menu items
data class MenuItem(
    val name: String,
    val category: String,
    val price: Double = 0.0
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
        MenuItem("Signature King Cone", "Fresh Ice Cream with Tea", 2.00),
        MenuItem("Chocolate Lucky Sundae", "Fresh Ice Cream with Tea", 6.00),
        MenuItem("Strawberry Crispy Sundae", "Fresh Ice Cream with Tea", 6.00),
        MenuItem("Early Grey Ice cream Tea", "Fresh Ice Cream with Tea", 5.00),
        MenuItem("Super Boba Sundae", "Fresh Ice Cream with Tea",  6.00),
        MenuItem("Creamy Mango Boba", "Fresh Ice Cream with Tea",  8.00),
        MenuItem("Super Mango Sundae", "Fresh Ice Cream with Tea",  5.00),
        MenuItem("O-Crispy Sundae", "Fresh Ice Cream with Tea",  6.00),
        MenuItem("Peach mi-shake", "Fresh Ice Cream with Tea", 5.00),

        // Milk Tea category items
        MenuItem("Brown Sugar Bubble Tea", "Milk Tea",  5.5),
        MenuItem("Pearl Milk Tea", "Milk Tea",  5.00),
        MenuItem("Supreme Mixed Milk Tea", "Milk Tea",  7.0),
        MenuItem("Coconut Jelly Milk Tea", "Milk Tea",  5.5),
        MenuItem("O-Coco Milk Tea", "Milk Tea",  6.00),
        MenuItem("Strawberry Creamy Drink", "Milk Tea",  4.5),
        MenuItem("Toffee Hazelnut Milk Tea", "Milk Tea",  5.5),
        MenuItem("Twin Topping Milk Tea", "Milk Tea",  6.5),
        MenuItem("Super Triple Milk Tea", "Milk Tea",  8.00),

        // Fruit Tea items
        MenuItem("Fresh Lemonade", "Fruit Tea",  4.0),
        MenuItem("Lemon Jasmine Tea", "Fruit Tea",  5.0),
        MenuItem("Passion Fruit Bubble Tea", "Fruit Tea",  7.0),
        MenuItem("Peach Jasmine Tea", "Fruit Tea",  7.0),
        MenuItem("Peach Black Tea", "Fruit Tea",  7.0),
        MenuItem("Lemon Black Tea", "Fruit Tea",  4.0),
        MenuItem("Kiwi Jasmine Tea", "Fruit Tea",  5.0),

        // Coffee items
        MenuItem("Ice Cream Latte", "Coffee",  5.0),
        MenuItem("Ice Cream Mocha", "Coffee",  5.0),
        MenuItem("Ice Cream Toffee Hazelnut Latte", "Coffee",  5.0)
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
}
