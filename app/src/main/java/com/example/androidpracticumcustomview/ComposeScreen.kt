package com.example.androidpracticumcustomview

import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@Composable
fun ComposeScreen(navController: NavController, bakeryViewModel: BakeryViewModel) {
    val bakeryItems = remember {
        listOf(BakeryItem("baget", 40.0, R.drawable.baget))
    }

    if (bakeryItems.size > 2) {
        throw IllegalStateException("Условие по заданию, дочерних элементов не должно превышать двух")
    }

    AnimatedBakeryScreen(
        firstChild = {
            FirstBakeryItem(
                item = bakeryItems[0],
                bakeryViewModel = bakeryViewModel,
                navController = navController
            )
        },
        secondChild = {
            TotalPriceDisplay(bakeryViewModel = bakeryViewModel)
        }
    )
}


@Composable
fun AnimatedBakeryScreen(
    firstChild: @Composable() (() -> Unit)? = null,
    secondChild: @Composable() (() -> Unit)? = null
) {
    val backgroundImage = painterResource(id = R.drawable.about_us_background)

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = backgroundImage,
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )


                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    item {
                        firstChild?.invoke()
                    }

                    item {
                        secondChild?.invoke()
                    }
                }
            }
        }



@Composable
fun FirstBakeryItem(
    item: BakeryItem,
    bakeryViewModel: BakeryViewModel,
    navController: NavController,
    durationAlphaMillis: Int = 2000,
    durationOffsetMillis: Int = 5000
) {
    val visible = remember { mutableStateOf(false) }
    val offsetY = remember { mutableFloatStateOf(0f) }
    val alpha = remember { mutableFloatStateOf(0f) }
    val slideOffset = remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        visible.value = true
        offsetY.floatValue = -390f

        launch {
            animate(
                initialValue = 0f,
                targetValue = 1f,

                animationSpec = tween(durationAlphaMillis)
            ) { value, _ -> alpha.floatValue = value }
        }
        launch {
            animate(
                initialValue = 0f,
                targetValue = 1f,
                animationSpec = tween(durationOffsetMillis)
            ) { value, _ -> slideOffset.floatValue = value }
        }
    }

    val animatedOffset by animateDpAsState(
        targetValue = offsetY.floatValue.dp,
        animationSpec = tween(durationMillis = durationOffsetMillis)
    )

    if (visible.value) {
        Box(
            modifier = Modifier
                .alpha(alpha.floatValue)
                .offset(y = animatedOffset + slideOffset.floatValue.dp)
        ) {
            BakeryItemRow(
                item = item,
                onAddToCart = { bakeryViewModel.addToCart(item) },
                navController = navController
            )
        }
    }
}

@Composable
fun TotalPriceDisplay(
    bakeryViewModel: BakeryViewModel,
    durationAlphaMillis: Int = 2000,
    durationOffsetMillis: Int = 5000
) {
    val visible = remember { mutableStateOf(false) }
    val offsetY = remember { mutableFloatStateOf(0f) }
    val alpha = remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        visible.value = true
        offsetY.floatValue = 390f
        alpha.floatValue = 1f
    }

    val animatedPrice by animateFloatAsState(
        targetValue = bakeryViewModel.totalPrice.toFloat(),
        animationSpec = tween(durationMillis = durationOffsetMillis)
    )

    val animatedOffset by animateDpAsState(
        targetValue = offsetY.floatValue.dp,
        animationSpec = tween(durationMillis = durationOffsetMillis)
    )

    val animatedAlpha by animateFloatAsState(
        targetValue = alpha.floatValue,
        animationSpec = tween(durationMillis = durationAlphaMillis)
    )

    if (visible.value) {
        Box(
            modifier = Modifier
                .alpha(animatedAlpha)
                .offset(y = animatedOffset)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Total: ${"%.2f".format(animatedPrice)} rub.",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = animatedAlpha)
            )
        }
    }
}