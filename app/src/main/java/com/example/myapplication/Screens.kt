package com.example.myapplication

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuMainScreen(navController: NavHostController, menuManager: MilkTeaMenuManager) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Home Page", color = Color.White) },
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Red
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(
                text = "Welcome to MIXUE! 🍵",
                fontSize = 24.sp,
                color = Color.Red,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            MenuOptionButton("View Full Menu", Icons.Default.ArrowForward) {
                navController.navigate("menu_full")
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
            ) {
                Text("Logout")
            }
        }
    }
}

@Composable
fun MenuOptionButton(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                fontSize = 18.sp,
                color = Color.Red,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = icon,
                contentDescription = "Navigate",
                tint = Color.Red
            )
        }
    }
}


@Composable
fun MenuItemCard(item: MenuItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = item.imageResId),
                contentDescription = item.name,
                modifier = Modifier
                    .size(80.dp)
                    .padding(end = 16.dp),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Red
                )
                Text(
                    text = "RM${"%.2f".format(item.price)}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuFullScreen(navController: NavHostController, menuManager: MilkTeaMenuManager) {
    var searchQuery by remember { mutableStateOf("") }
    val categories = menuManager.categories
    val searchResults = remember(searchQuery) {
        if (searchQuery.isNotEmpty()) {
            menuManager.searchMenu(searchQuery)
        } else {
            emptyList()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search menu...") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.White,
                            unfocusedIndicatorColor = Color.White,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedPlaceholderColor = Color.White.copy(alpha = 0.7f),
                            unfocusedPlaceholderColor = Color.White.copy(alpha = 0.7f)
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Red
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Category Navigation Row (no current category highlighted in full menu)
            CategoryNavigationRow(categories, navController)

            // Menu Content
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                if (searchQuery.isNotEmpty()) {
                    if (searchResults.isEmpty()) {
                        item {
                            Text(
                                text = "No results found for '$searchQuery'",
                                fontSize = 16.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    } else {
                        items(searchResults) { item ->
                            MenuItemCard(item = item)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                } else {
                    categories.forEach { category ->
                        item {
                            Text(
                                text = category,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Red,
                                modifier = Modifier.padding(vertical = 16.dp)
                            )

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(Color.LightGray)
                                    .padding(bottom = 8.dp)
                            )
                        }

                        items(menuManager.getItemsByCategory(category)) { item ->
                            MenuItemCard(item = item)
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryNavigationRow(categories: List<String>, navController: NavHostController, currentCategory: String? = null) {
    val scrollState = rememberScrollState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray.copy(alpha = 0.2f))
            .horizontalScroll(scrollState)
            .padding(vertical = 8.dp, horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        categories.forEach { category ->
            CategoryChip(
                category = category,
                isSelected = category == currentCategory,
                onClick = {
                    navController.navigate("menu_category/$category") {
                        // This prevents building up a large stack of category screens
                        popUpTo("menu_category/{category}") {
                            inclusive = false
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun CategoryChip(category: String, isSelected: Boolean = false, onClick: () -> Unit) {
    val backgroundColor = if (isSelected) Color.Red else Color.LightGray
    val textColor = if (isSelected) Color.White else Color.Black

    Card(
        modifier = Modifier
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Text(
            text = category,
            color = textColor,
            fontSize = 14.sp,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDetailScreen(navController: NavHostController, menuManager: MilkTeaMenuManager, category: String) {
    val items = menuManager.getItemsByCategory(category)
    val categories = menuManager.categories

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(category, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Red
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Add Category Navigation Row at the top
            CategoryNavigationRow(categories, navController, currentCategory = category)

            // Menu items for the selected category
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                items(items) { item ->
                    MenuItemCard(item = item)
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}
@Composable
fun BottomNavigationBar(navController: NavHostController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Red)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomNavItem(icon = Icons.Default.Home, label = "Home", onClick = {
            navController.navigate("menu_main") {
                popUpTo("menu_main") { inclusive = true }
            }
        })
        BottomNavItem(icon = Icons.Default.Menu, label = "Menu", onClick = {
            navController.navigate("menu_full") {
                popUpTo("menu_full") { inclusive = true }
            }
        })
        BottomNavItem(icon = Icons.Default.ShoppingCart, label = "Cart", onClick = {
            navController.popBackStack()
        })
        BottomNavItem(icon = Icons.Default.Create, label = "Order", onClick = {
            // Navigate to current order
        })
        BottomNavItem(icon = Icons.Default.Person, label = "Profile", onClick = {
            // Navigate to profile
        })
    }
}

@Composable
fun BottomNavItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = label,
            color = Color.White,
            fontSize = 12.sp
        )
    }
}