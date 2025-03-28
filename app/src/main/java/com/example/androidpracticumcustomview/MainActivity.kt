package com.example.androidpracticumcustomview

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BakeryApp()
        }
    }
}

@Composable
fun BakeryApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "start_page") {
        composable("start_page") {
            StartPage(navController = navController)
        }
        composable("bakery_order") {
            ComposeScreen()
        }

        composable(
            "item_description/{itemName}/{itemPrice}/{imageRes}",


        ) {

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

