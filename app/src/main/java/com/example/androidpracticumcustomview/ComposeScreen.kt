package com.example.androidpracticumcustomview

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

@Composable
fun ComposeScreen(navController: NavController, bakeryViewModel: BakeryViewModel) {
    val backgroundImage = painterResource(id = R.drawable.about_us_background)
    val bakeryItems = remember {
        listOf(
            BakeryItem("baget", 40.0, R.drawable.baget),
            // Add more items if needed
        )
    }

    if (bakeryItems.size > 2) {
        throw IllegalStateException("Количество дочерних элементов не должно превышать двух")
    }

    val visibleItems = remember { mutableStateListOf<Boolean>().apply { addAll(List(bakeryItems.size) { false }) } }
    val visibleTotalPrice = remember { mutableStateOf(false) }
    val animatedPrice by animateFloatAsState(
        targetValue = bakeryViewModel.totalPrice.toFloat(),
        animationSpec = tween(durationMillis = 2000)
    )

    val itemOffsetY = remember { mutableStateListOf<Float>().apply { addAll(List(bakeryItems.size) { 0f }) } }
    val totalPriceOffsetY = remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        bakeryItems.indices.forEach { index ->
            delay(0)
            visibleItems[index] = true
            itemOffsetY[index] = -250f
        }
        delay(0)
        visibleTotalPrice.value = true
        totalPriceOffsetY.floatValue = -210f
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = backgroundImage,
            contentDescription = "AboutUsScreen Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                IconButton(onClick = { navController.navigate("start_page") { popUpTo("start_page") } }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        tint = Color.White,
                        contentDescription = "Back"
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                items(bakeryItems.withIndex().toList()) { (index, item) ->
                    AnimatedVisibility(
                        visible = visibleItems[index],
                        enter = fadeIn(animationSpec = tween(durationMillis = 2000)) +
                                slideInVertically(animationSpec = tween(durationMillis = 1000)) { itemOffsetY[index].toInt() } +
                                scaleIn(animationSpec = tween(1000))
                    ) {
                        BakeryItemRow(
                            item = item,
                            onAddToCart = { bakeryViewModel.addToCart(item) },
                            navController = navController
                        )
                    }
                }

                item {
                    AnimatedVisibility(
                        visible = visibleTotalPrice.value,
                        enter = fadeIn(animationSpec = tween(durationMillis = 2000)) +
                                slideInVertically(animationSpec = tween(durationMillis = 1000)) { totalPriceOffsetY.floatValue.toInt() } +
                                scaleIn(animationSpec = tween(1000))
                    ) {
                        Text(
                            text = "Total: ${"%.2f".format(animatedPrice)} rub.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Black,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}