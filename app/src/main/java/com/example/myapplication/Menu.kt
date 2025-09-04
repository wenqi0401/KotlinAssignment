package com.example.myapplication

// Data classes for menu items
data class MenuItem(
    val name: String,
    val category: String,
    val description: String = "",
    val price: Double = 0.0
)

// Main menu manager class
class MilkTeaMenuManager {
    // Categories
    val categories = listOf(
        "Fresh Ice Cream with Tea",
        "Kiwi Creamy Drink",
        "Fresh Ice Cream and Tea",
        "Mixt/E Ice Cream",
        "Fruit Tea",
        "Coffee"
    )

    // Menu items organized by category
    private val menuItems = listOf(
        // Fresh Ice Cream with Tea sub-items
        MenuItem("Milk Tea", "Fresh Ice Cream with Tea", "Creamy milk tea with ice cream", 5.99),
        MenuItem("Fruit Tea", "Fresh Ice Cream with Tea", "Refreshing fruit tea with ice cream", 6.49),
        MenuItem("Coffee", "Fresh Ice Cream with Tea", "Coffee with ice cream topping", 5.79),

        // Kiwi Creamy Drink category items
        MenuItem("Kiwi Creamy Drink", "Kiwi Creamy Drink", "Refreshing kiwi creamy beverage", 6.99),
        MenuItem("Strawberry Creamy Drink", "Kiwi Creamy Drink", "Sweet strawberry creamy drink", 6.99),
        MenuItem("Brown Sugar Bubble Tea", "Kiwi Creamy Drink", "Classic brown sugar boba", 5.99),
        MenuItem("Toffee Hazelnut Milk Tea", "Kiwi Creamy Drink", "Rich toffee hazelnut flavor", 6.49),
        MenuItem("Classical Milk Tea", "Kiwi Creamy Drink", "Traditional milk tea", 4.99),
        MenuItem("O-CoCo Milk Tea", "Kiwi Creamy Drink", "Special coconut milk tea", 6.29),

        // Fresh Ice Cream and Tea items
        MenuItem("Fresh Ice Cream and Tea", "Fresh Ice Cream and Tea", "Combination of ice cream and tea", 7.49),

        // Mixt/E Ice Cream items
        MenuItem("Mixt/E Ice Cream", "Mixt/E Ice Cream", "Mixed ice cream delight", 8.99),
        MenuItem("Strawberry Mi-Shake", "Mixt/E Ice Cream", "Strawberry milkshake", 7.99),
        MenuItem("Peach Mi-Shake", "Mixt/E Ice Cream", "Peach flavored milkshake", 7.99),
        MenuItem("Boba Mi-Shake", "Mixt/E Ice Cream", "Boba pearl milkshake", 8.49),
        MenuItem("Super Boba Sundae", "Mixt/E Ice Cream", "Premium boba sundae", 9.99),

        // Fruit Tea items
        MenuItem("Peach Black Tea", "Fruit Tea", "Peach infused black tea", 5.49),
        MenuItem("Peach Jasmine Tea", "Fruit Tea", "Peach with jasmine tea", 5.79),
        MenuItem("Kiwi Jasmine Tea", "Fruit Tea", "Kiwi and jasmine blend", 5.99),
        MenuItem("Passion Fruit Bubble Tea", "Fruit Tea", "Tropical passion fruit boba", 6.29),
        MenuItem("Creamy Mango Boba", "Fruit Tea", "Creamy mango bubble tea", 6.49),

        // Coffee items
        MenuItem("Ice Cream Latte", "Coffee", "Ice cream coffee latte", 6.99),
        MenuItem("Ice Cream Mecha", "Coffee", "Special mecha coffee with ice cream", 7.49),
        MenuItem("Ice Cream Toffee Hazelnut Latte", "Coffee", "Toffee hazelnut coffee delight", 7.99)
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
                    it.description.contains(query, ignoreCase = true) ||
                    it.category.contains(query, ignoreCase = true)
        }
    }
}