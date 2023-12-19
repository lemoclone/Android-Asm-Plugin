package com.example.gradletransformactiondemo

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.gradletransformactiondemo.ui.theme.GradleTransformActionDemoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GradleTransformActionDemoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
        val b = 3 / 0
    }

    fun runCatch() {
        try {
           val c = 4
           val d = c * 2 - 8
           val e = c / d
        } catch (e: Exception) {
            println(e.toString())
        }
    }
}

fun showGreeting(ctx: Context) {
    println("Hello World")
    val t = 1/0
    Toast.makeText(ctx, "Hello world", Toast.LENGTH_SHORT).show()
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    val mContext = LocalContext.current
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
    Button(onClick = { showGreeting(mContext) }) {
        Text(text = "Show")
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GradleTransformActionDemoTheme {
        Greeting("Android")
    }
}