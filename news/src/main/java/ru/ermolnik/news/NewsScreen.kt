package ru.ermolnik.news

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun NewsScreen(viewModel: NewsViewModel) {
    val state = viewModel.state.collectAsState()
    Box(modifier = Modifier.fillMaxSize()) {
        when (state.value) {
            is NewsState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(50.dp)
                        .align(Alignment.Center)
                )
            }
            is NewsState.Error -> {
                val error = (state.value as NewsState.Error).throwable
                ShowDialog(
                    retryConnectAction = { viewModel.updateNews() },
                    description = error.message ?: ""
                )
            }
            is NewsState.Content -> {
                val data = state.value as NewsState.Content
                SwipeRefresh(state = rememberSwipeRefreshState(isRefreshing = false),
                    onRefresh = { viewModel.updateNews() }) {
                    Spacer(Modifier.width(8.dp))
                    Column(Modifier.verticalScroll(rememberScrollState())) {
                        data.news.forEach {
                            Text(text = it.title)
                            Text(text = it.description)
                        }
                    }
                }
            }
        }
    }
}
