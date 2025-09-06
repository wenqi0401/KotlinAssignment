package com.example.myapplication

// 购物车商品数据类
data class CartItem(
    val item: MenuItem,
    val quantity: Int,
    val size: String,
    val ice: String,
    val sugar: String
)

// 购物车管理器
object CartManager {
    private val _cartItems = mutableListOf<CartItem>()

    // 获取购物车商品列表
    fun getItems(): List<CartItem> {
        return _cartItems.toList()
    }

    // 添加商品到购物车
    fun addToCart(cartItem: CartItem) {
        // 查找是否已存在相同配置的商品
        val existingIndex = _cartItems.indexOfFirst {
            it.item.name == cartItem.item.name &&
                    it.size == cartItem.size &&
                    it.ice == cartItem.ice &&
                    it.sugar == cartItem.sugar
        }

        if (existingIndex >= 0) {
            // 如果存在，增加数量
            val existing = _cartItems[existingIndex]
            _cartItems[existingIndex] = existing.copy(quantity = existing.quantity + cartItem.quantity)
        } else {
            // 如果不存在，添加新商品
            _cartItems.add(cartItem)
        }
    }

    // 从购物车移除商品
    fun removeItem(cartItem: CartItem) {
        _cartItems.remove(cartItem)
    }

    // 更新商品数量
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

    // 清空购物车
    fun clearAll() {
        _cartItems.clear()
    }

    // 计算总价
    fun calculateTotal(): Double {
        return _cartItems.sumOf { it.item.price * it.quantity }
    }

    // 获取商品总数量
    fun getTotalItemCount(): Int {
        return _cartItems.sumOf { it.quantity }
    }
}