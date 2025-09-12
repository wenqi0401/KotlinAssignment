package com.example.myapplication

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import kotlinx.coroutines.delay
import kotlin.math.min


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
        }
    ) { padding ->
        // Add vertical scroll here
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()) // Add this line for scrolling
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text(
                    text = "Welcome to MIXUE! 🍵",
                    fontSize = 24.sp,
                    color = Color.Red,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Main image carousel - UPDATED to include navController
                AutoScrollingImageCarousel(navController)

                // Top Sales section
                Text(
                    text = "Top Sales",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Red,
                    modifier = Modifier.padding(vertical = 16.dp)
                )

                TopSalesCarousel(menuManager) { item ->
                    // Navigate to item detail when a top sales item is clicked
                    navController.navigate("item_detail/${item.name}")
                }

                // voucher
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .clickable { navController.navigate("voucher_center") },
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🎫", fontSize = 28.sp, modifier = Modifier.padding(end = 12.dp))
                            Column {
                                Text(
                                    text = "My Vouchers",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Red
                                )
                                Text(
                                    text = "Check and redeem your discounts",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "Go to vouchers",
                            tint = Color.Red
                        )
                    }
                }

            }
        }
    }
}

@Composable
fun AutoScrollingImageCarousel(navController: NavHostController) {
    val images = remember {
        listOf(
            R.drawable.homepage,
            R.drawable.hotday,
            R.drawable.newcoffee,
            R.drawable.coffee,
            R.drawable.voucher
        )
    }

    val pagerState = rememberPagerState(pageCount = { images.size })

    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            val nextPage = (pagerState.currentPage + 1) % images.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(bottom = 16.dp)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { page ->
            Image(
                painter = painterResource(id = images[page]),
                contentDescription = "Promotional image ${page + 1}",
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        try {
                            when (page) {
                                0, 1 -> navController.navigate("menu_full")
                                2, 3 -> navController.navigate("menu_category/Coffee")
                                4 -> navController.navigate("voucher_center")
                            }
                        } catch (e: Exception) {
                            println("Navigation failed: ${e.message}")
                        }
                    }
            )
        }

        // Page indicators
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(images.size) { index ->
                val color = if (pagerState.currentPage == index) Color.Red else Color.LightGray
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(color)
                )
            }
        }
    }
}

@Composable
fun TopSalesCarousel(menuManager: MilkTeaMenuManager, onItemClick: (MenuItem) -> Unit) {
    // Define your top sales items directly by their names or other identifiers
    val topSalesItemNames = remember {
        listOf(
            "Fresh Lemonade",
            "Signature King Cone",
            "Chocolate Lucky Sundae",
            "Strawberry Creamy Drink",
            "Brown Sugar Milk Tea",
            "Passion Fruit Bubble Tea",
            "Lemon Jasmine Tea",
            "Pearl Milk Tea"
        )
    }

    val topSalesItems = remember {
        menuManager.getAllMenuItems().filter { item ->
            topSalesItemNames.contains(item.name)
        }
    }

    val pagerState = rememberPagerState(
        pageCount = { (topSalesItems.size + 1) / 2 } // Show 2 items per page
    )

    // Auto-scroll effect
    LaunchedEffect(Unit) {
        while (true) {
            delay(3000) // Scroll every 4 seconds
            val nextPage = (pagerState.currentPage + 1) % pagerState.pageCount
            pagerState.animateScrollToPage(nextPage)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .padding(bottom = 16.dp)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) { page ->
            // Display 2 items per page
            val startIndex = page * 2
            val endIndex = min(startIndex + 2, topSalesItems.size)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (i in startIndex until endIndex) {
                    TopSalesItem(item = topSalesItems[i], onItemClick = onItemClick)
                }
            }
        }

        // Page indicators
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pagerState.pageCount) { index ->
                val color = if (pagerState.currentPage == index) Color.Red else Color.LightGray
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(color)
                )
            }
        }
    }
}

@Composable
fun TopSalesItem(item: MenuItem, onItemClick: (MenuItem) -> Unit) { // Add onItemClick parameter
    Card(
        modifier = Modifier
            .width(160.dp)
            .height(200.dp)
            .padding(8.dp)
            .clickable { onItemClick(item) }, // Use the passed onItemClick
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = item.imageResId),
                contentDescription = item.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                modifier = Modifier.padding(horizontal = 6.dp)
            )
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
