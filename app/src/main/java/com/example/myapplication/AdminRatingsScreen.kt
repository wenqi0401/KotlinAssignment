package com.example.myapplication

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication.orderData.OrderRepository
import com.example.myapplication.orderData.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminRatingsScreen(navController: NavController) {
    val context = LocalContext.current
    val repository = remember { OrderRepository(context) }
    var ratedOrders by remember { mutableStateOf(emptyList<Order>()) }
    var averageRating by remember { mutableStateOf(0.0) }
    var ratingCount by remember { mutableStateOf(0) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                // Load data in parallel for better performance
                val ordersDeferred = async { repository.getRatedOrders() }
                val avgRatingDeferred = async { repository.getAverageRating() }
                val countDeferred = async { repository.getRatingCount() }

                // Await all results
                ratedOrders = ordersDeferred.await()
                averageRating = avgRatingDeferred.await()
                ratingCount = countDeferred.await()

                Log.d("AdminRatings", "Loaded ${ratedOrders.size} ratings, avg: $averageRating, count: $ratingCount")
            } catch (e: Exception) {
                errorMessage = "Error loading ratings: ${e.message}"
                Log.e("AdminRatings", "Error loading ratings", e)
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Ratings & Feedback",
                        fontSize = 20.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFF9800)
                )
            )
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (errorMessage != null) {
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(errorMessage!!, color = Color.Red)
            }
        } else {
            // Use LazyColumn for the entire content to avoid scrolling conflicts
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Average Rating Card
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1))
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Average Rating",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE65100)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "%.1f/5".format(averageRating),
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFF9800)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Based on $ratingCount ratings",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }

                // Section Title
                item {
                    Text(
                        "All Ratings",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Ratings List or Empty State
                if (ratedOrders.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No ratings yet", color = Color.Gray)
                        }
                    }
                } else {
                    items(ratedOrders) { order ->
                        RatingItemCard(order)
                    }
                }
            }
        }
    }
}

@Composable
fun RatingItemCard(order: Order) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Stars
                Row {
                    for (i in 1..5) {
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint = if (i <= order.rating) Color(0xFFFFD700) else Color.LightGray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Text(
                    formatDate(order.orderDate),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (order.feedback.isNotEmpty()) {
                Text(
                    order.feedback,
                    fontSize = 14.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                "Order: ${order.orderId.take(8)}... | User: ${order.userPhoneNumber}",
                fontSize = 12.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                "Rating: ${order.rating}/5 stars",
                fontSize = 12.sp,
                color = Color(0xFFFF9800),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

