package ru.mts.coroutines

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import ru.ermolnik.news.NewsScreen
import ru.ermolnik.news.NewsViewModel
import ru.mts.coroutines.ui.theme.CoroutinesTheme
import ru.mts.data.main.AppDatabase
import ru.mts.data.news.db.NewsLocalDataSource
import ru.mts.data.news.remote.NewsRemoteDataSource
import ru.mts.data.news.repository.NewsRepository

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CoroutinesTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    NewsScreen(
                        NewsViewModel(
                            NewsRepository(
                                NewsLocalDataSource(applicationContext),
                                NewsRemoteDataSource(),
                                AppDatabase.getDatabase(applicationContext).newsDao()
                            )
                        )
                    )
                }
            }
        }
    }
}

//@Composable
//fun Greeting(name: String) {
//    Text(text = "Hello $name!")
//}
//
//@Preview(showBackground = true)
//@Composable
//fun DefaultPreview() {
//    CoroutinesTheme {
//        Greeting("Android")
//    }
//}