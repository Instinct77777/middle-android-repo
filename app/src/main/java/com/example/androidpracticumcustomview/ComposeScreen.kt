package com.example.androidpracticumcustomview

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Image(
            painter = backgroundImage,
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        val bakeryItems = remember {
            listOf(
                BakeryItem("baget", 40.0, R.drawable.baget)
            )
        }

        if (bakeryItems.size > 2) {
            throw IllegalStateException("Условие по заданию, дочерних элементов не должно превышать двух")
        }

        val visibleItems = remember {
            mutableStateListOf<Boolean>().apply {
                addAll(List(bakeryItems.size) { false })
            }
        }
        val visibleTotalPrice = remember { mutableStateOf(false) }

        val itemOffsetY = remember {
            mutableStateListOf<Float>().apply {
                addAll(List(bakeryItems.size) { 0f })
            }
        }

        val totalPriceOffsetY = remember { mutableFloatStateOf(0f) }
        val totalPriceAlpha = remember { mutableFloatStateOf(0f) }

        LaunchedEffect(Unit) {
            bakeryItems.indices.forEach { index ->
                delay(0)
                visibleItems[index] = true
                itemOffsetY[index] = -360f
            }
            delay(0)
            visibleTotalPrice.value = true
            totalPriceOffsetY.value = 355f
            totalPriceAlpha.value = 1f
        }

        val animatedPrice by animateFloatAsState(
            targetValue = bakeryViewModel.totalPrice.toFloat(),
            animationSpec = tween(durationMillis = 5000)
        )

        val animatedTotalPriceOffset by animateDpAsState(
            targetValue = totalPriceOffsetY.value.dp,
            animationSpec = tween(durationMillis = 5000)
        )

        val animatedTotalPriceAlpha by animateFloatAsState(
            targetValue = totalPriceAlpha.value,
            animationSpec = tween(durationMillis = 2000)
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
                itemsIndexed(bakeryItems) { index, item ->
                    val offsetY by animateDpAsState(
                        targetValue = itemOffsetY[index].dp,
                        animationSpec = tween(durationMillis = 5000)
                    )

                    AnimatedVisibility(
                        visible = visibleItems[index],
                        enter = fadeIn(tween(500)) + slideInVertically(initialOffsetY = { -it / 2 })
                    ) {
                        Box(
                            modifier = Modifier
                                .offset(y = offsetY)
                        ) {
                            BakeryItemRow(
                                item = item,
                                onAddToCart = { bakeryViewModel.addToCart(item) },
                                navController = navController
                            )
                        }
                    }
                }

                item {
                    if (visibleTotalPrice.value) {
                        Box(
                            modifier = Modifier
                                .offset(y = animatedTotalPriceOffset)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Total: ${"%.2f".format(animatedPrice)} rub.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.White.copy(alpha = animatedTotalPriceAlpha)
                            )
                        }
                    }
                }
            }
        }
    }
}