package com.example.myapplication

data class CartItem(
    val item: MenuItem,
    val quantity: Int,
    val ice: String,
    val sugar: String
)

object CartManager {
    private val _cartItems = mutableListOf<CartItem>()

    fun getItems(): List<CartItem> {
        return _cartItems.toList()
    }

    fun addToCart(cartItem: CartItem) {
        val existingIndex = _cartItems.indexOfFirst {
            it.item.name == cartItem.item.name &&
                    it.ice == cartItem.ice &&
                    it.sugar == cartItem.sugar
        }

        if (existingIndex >= 0) {
            val existing = _cartItems[existingIndex]
            _cartItems[existingIndex] = existing.copy(quantity = existing.quantity + cartItem.quantity)
        } else {
            _cartItems.add(cartItem)
        }
    }

    fun removeItem(cartItem: CartItem) {
        _cartItems.remove(cartItem)
    }

    fun updateItemQuantity(cartItem: CartItem, newQuantity: Int) {
        if (newQuantity <= 0) {
            removeItem(cartItem)
            return
        }

        val index = _cartItems.indexOf(cartItem)
        if (index >= 0) {
            _cartItems[index] = cartItem.copy(quantity = newQuantity)
        }
    }

    fun clearAll() {
        _cartItems.clear()
    }

    fun calculateTotal(): Double {
        return _cartItems.sumOf { it.item.price * it.quantity }
    }

}