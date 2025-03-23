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
            contentDescription = "AboutUsScreen Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        val bakeryItems = listOf(
            BakeryItem("baget", 40.0, R.drawable.baget),
            BakeryItem("cruassan", 50.0, R.drawable.kruassan),
           // BakeryItem("cherry pie", 150.0, R.drawable.vishnya_pirog),
           // BakeryItem("cheeze cake", 80.0, R.drawable.syrniki),
            // BakeryItem("napoleon", 350.0, R.drawable.napoleon_tort)
        )

        if (bakeryItems.size > 2) {
      throw IllegalStateException("Количество дочерних элементов не должно превышать двух")
      }


        val visibleItems =
            remember { mutableStateListOf<Boolean>().apply { addAll(List(bakeryItems.size) { false }) } }
        val visibleTotalPrice = remember { mutableStateOf(false) }

        val itemOffsetY =
            remember { mutableStateListOf<Float>().apply { addAll(List(bakeryItems.size) { 0f }) } }

        val totalPriceOffsetY = remember { mutableStateOf(0f) }

        LaunchedEffect(Unit) {
            bakeryItems.indices.forEach { index ->
                delay(500)
                visibleItems[index] = true
                itemOffsetY[index] = if (index % 2 == 0) -240f else 240f // вверх для первого, вниз для второго
            }
            delay(500)
            visibleTotalPrice.value = true
            totalPriceOffsetY.value = 300f
        }

        val animatedPrice by animateFloatAsState(
            targetValue = bakeryViewModel.totalPrice.toFloat(),
            animationSpec = tween(durationMillis = 5000)
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

                Text(
                    text = "Bakery Order",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                items(bakeryItems.withIndex().toList()) { (index, item) ->
                    val offsetY by animateDpAsState(
                        targetValue = itemOffsetY[index].dp,
                        animationSpec = tween(durationMillis = 5000)
                    )

                    AnimatedVisibility(
                        visible = visibleItems[index],
                        enter = fadeIn(tween(2000)) + slideInVertically(
                            initialOffsetY = { if (index % 2 == 0) -it / 2 else it / 2 } // вверх для четных, вниз для нечетных
                        )
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
                    val totalPriceYOffset by animateDpAsState(
                        targetValue = totalPriceOffsetY.value.dp,
                        animationSpec = tween(durationMillis = 2000)
                    )

                    AnimatedVisibility(
                        visible = visibleTotalPrice.value,
                        enter = fadeIn(tween(2000)) + slideInVertically(initialOffsetY = { it })
                    ) {
                        Box(
                            modifier = Modifier
                                .offset(y = totalPriceYOffset)
                                .fillMaxWidth()
                                .padding(1.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Total: ${"%.2f".format(animatedPrice)} rub.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.Black
                            )
                        }
                    }
                }
            }
        }
    }
}
