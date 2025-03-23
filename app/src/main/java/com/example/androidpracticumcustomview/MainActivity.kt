package com.example.androidpracticumcustomview

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BakeryApp()
        }
    }
}


@Serializable
data class BakeryItem(val name: String, val price: Double, val imageRes: Int)
@Serializable
data class Cart(val items: Map<String, Int>)


fun Map<BakeryItem, Int>.toJson(): String {
    val items = this.mapKeys { it.key.name }
    val cart = Cart(items)
    return Json.encodeToString(cart)
}


class BakeryViewModel : ViewModel() {
    var cart by mutableStateOf<Map<BakeryItem, Int>>(emptyMap())
        private set

    var totalPrice by mutableStateOf(0.0)
        private set


    fun addToCart(item: BakeryItem) {
        val updatedCart = cart.toMutableMap()
        updatedCart[item] = (updatedCart[item] ?: 0) + 1
        cart = updatedCart
        totalPrice += item.price
    }


}

@Composable
fun BakeryApp() {
    val navController = rememberNavController()
    val bakeryViewModel: BakeryViewModel = viewModel()

    NavHost(navController = navController, startDestination = "start_page") {
        composable("start_page") {
            StartPage(navController = navController)
        }
        composable("bakery_order") {
            ComposeScreen(navController = navController, bakeryViewModel = bakeryViewModel)
        }


        composable(
            "item_description/{itemName}/{itemPrice}/{imageRes}",
            arguments = listOf(
                navArgument("itemName") { type = NavType.StringType },
                navArgument("itemPrice") { type = NavType.StringType },
                navArgument("imageRes") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val itemName = backStackEntry.arguments?.getString("itemName") ?: ""
            val itemPriceString = backStackEntry.arguments?.getString("itemPrice") ?: "0.0"
            val imageRes = backStackEntry.arguments?.getInt("imageRes") ?: 0


            val itemPrice = itemPriceString.toDouble()

            val bakeryItem = BakeryItem(itemName, itemPrice, imageRes)
            ItemDescriptionScreen(navController = navController, bakeryItem = bakeryItem)
        }
    }
}



@Composable
fun BakeryItemRow(item: BakeryItem, onAddToCart: () -> Unit, navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        val image: Painter = painterResource(id = item.imageRes)
        Image(
            painter = image,
            contentDescription = item.name,
            modifier = Modifier
                .size(40.dp)
                .padding(end = 16.dp)
                .clickable {
                    navController.navigate("item_description/${item.name}/${item.price}/${item.imageRes}")
                }
        )

        Text(
            text = item.name,
            color = Color.White,
            modifier = Modifier
                .clickable {
                    navController.navigate("item_description/${item.name}/${item.price}/${item.imageRes}")
                }
                .padding(end = 8.dp)
        )

        Text(
            text = "${item.price} rub.",
            color = Color.White
        )

        Spacer(modifier = Modifier.weight(1f))


        Button(
            onClick = onAddToCart,
            modifier = Modifier.height(40.dp)
        ) {
            Text("add", color = Color.White)
        }
    }
}



@Composable
fun ItemDescriptionScreen(navController: NavController, bakeryItem: BakeryItem) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
        }

        Text(
            text = bakeryItem.name,
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Image(
            painter = painterResource(id = bakeryItem.imageRes),
            contentDescription = bakeryItem.name,
            modifier = Modifier.size(200.dp).padding(bottom = 16.dp),
            contentScale = ContentScale.Crop
        )

        Text(
            text = "Price: ${"%.2f".format(bakeryItem.price)} rub.",
            style = MaterialTheme.typography.bodyLarge,
           modifier = Modifier.padding(bottom = 16.dp)
        )

        Button(
            onClick = { },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add to Cart")
        }
    }
}


@Composable
fun StartPage(navController: NavController) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val rotationAngle = if (isLandscape) 90f else 0f
    val backgroundImage: Painter = painterResource(id = R.drawable.start_page)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectHorizontalDragGestures { _, dragAmount ->
                    if (dragAmount < 0) {
                        navController.navigate("backery_order")
                    } else if (dragAmount > 0) {
                        navController.navigate("val intent = Intent(context, BakeryOrderActivity::class.java)\n" +
                                "                    context.startActivity(intent)")
                    }
                }
            }
    ) {

        Image(
            painter = backgroundImage,
            contentDescription = "Start Page Background",
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    rotationZ = rotationAngle,
                    transformOrigin = TransformOrigin.Center
                ),
            contentScale = ContentScale.FillBounds
        )


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "greetings my friend",
                style = MaterialTheme.typography.headlineLarge.copy(color = Color.Yellow),
                modifier = Modifier.padding(bottom = 100.dp)
            )


            Spacer(modifier = Modifier.height(300.dp))


            Button(
                onClick = { navController.navigate("bakery_order") },
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp)
            ) {
                Text("make order compose")
            }


            Spacer(modifier = Modifier.height(100.dp))

            Button(
                onClick = {
                    val intent = Intent(context, XmlActivity::class.java)
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp)
            ) {
                Text("make order xml")
            }

        }
    }

}

