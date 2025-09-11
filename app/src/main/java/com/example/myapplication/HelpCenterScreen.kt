package com.example.myapplication

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavHostController
import androidx.compose.material3.Button

// Sample data with questions and answers
data class FaqItem(val question: String, val answer: String)

val faqItems = listOf(
    FaqItem(
        "How do I manage my notifications?",
        "You can manage notifications in the Settings menu. Go to Settings > Notifications to customize which alerts you receive."
    ),
    FaqItem(
        "How do I start to order?",
        "To place an order, browse our products, add items to your cart, and proceed to checkout. You'll need to create an account or sign in first."
    ),
    FaqItem(
        "How do I join a support group?",
        "Visit our Community section and click on 'Join Support Group'. You can browse available groups or create your own."
    ),
    FaqItem(
        "Is my data safe and private?",
        "Yes, we take data security seriously. All your personal information is encrypted and we never share your data with third parties without your consent."
    )
)

// Contact option data class with intent information
data class ContactOption(
    val name: String,
    val intent: Intent,
    val icon: Int? = null // You could add icon resources here if needed
)

// Function to create contact options with intents
fun getContactOptions(context: android.content.Context): List<ContactOption> {
    return listOf(
        ContactOption(
            "Call Support",
            Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:+601130082067") // Replace with your support number
            }
        ),
        ContactOption(
            "WhatsApp",
            Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://wa.me/11234567890") // Replace with your WhatsApp number
                setPackage("com.whatsapp")
            }
        ),
        ContactOption(
            "Email",
            Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:support@example.com") // Replace with your support email
                putExtra(Intent.EXTRA_SUBJECT, "Support Request")
            }
        ),
        ContactOption(
            "Website",
            Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://www.mixuemalaysia.com") // Replace with your website
            }
        ),
        ContactOption(
            "Facebook",
            Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://www.facebook.com/yourpage") // Replace with your Facebook page
                setPackage("com.facebook.katana")
            }
        ),
        ContactOption(
            "Twitter",
            Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://twitter.com/yourhandle") // Replace with your Twitter handle
                setPackage("com.twitter.android")
            }
        ),
        ContactOption(
            "Instagram",
            Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://www.instagram.com/yourprofile") // Replace with your Instagram profile
                setPackage("com.instagram.android")
            }
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpCenterScreen(navController: NavHostController) {
    var selectedTab by remember { mutableStateOf("FAQ") }
    var searchText by remember { mutableStateOf("") }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Help Center", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Red
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.Transparent)
                .verticalScroll(rememberScrollState())
        ) {
            // Search Bar
            TextField(
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = { Text("Search for help") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp)
            )

            // FAQ and Contact Us tabs
            HelpCenterTabs(selectedTab = selectedTab, onTabSelected = { selectedTab = it })

            Spacer(modifier = Modifier.height(16.dp))

            // Content based on selected tab
            when (selectedTab) {
                "FAQ" -> FaqContent()
                "Contact Us" -> ContactUsContent(context = context)
            }
        }
    }
}

@Composable
fun HelpCenterTabs(selectedTab: String, onTabSelected: (String) -> Unit) {
    val tabs = listOf("FAQ", "Contact Us")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        tabs.forEach { tab ->
            HelpCenterTab(
                title = tab,
                isSelected = tab == selectedTab,
                onClick = { onTabSelected(tab) }
            )
        }
    }
}

@Composable
fun HelpCenterTab(title: String, isSelected: Boolean, onClick: () -> Unit) {
    val backgroundColor = if (isSelected) Color.Red else Color.White
    val textColor = if (isSelected) Color.White else Color.Black

    Card(
        modifier = Modifier
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Text(
            text = title,
            color = textColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun FaqContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Frequently Asked Questions",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Display FAQ items with questions and answers
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            items(faqItems) { faqItem ->
                // White box containing both question and answer
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    // Question
                    Text(
                        text = faqItem.question,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Answer in gray box with smaller text
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.LightGray, RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = faqItem.answer,
                            fontSize = 12.sp,
                            color = Color.DarkGray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ContactUsContent(context: android.content.Context) {
    val contactOptions = remember { getContactOptions(context) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Contact Us",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        // Contact options with clickable intents
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            items(contactOptions) { option ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .height(60.dp)
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .clickable {
                            try {
                                startActivity(context, option.intent, null)
                            } catch (e: Exception) {
                                // Fallback to browser if app not installed
                                if (option.intent.`package` != null) {
                                    val browserIntent = Intent(
                                        Intent.ACTION_VIEW,
                                        option.intent.data
                                    )
                                    startActivity(context, browserIntent, null)
                                }
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = option.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}