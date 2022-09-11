package ru.ermolnik.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import ru.mts.data.news.repository.NewsRepository
import ru.mts.data.utils.Result

class NewsViewModel(private val repository: NewsRepository) : ViewModel() {
    private val _state: MutableStateFlow<NewsState> = MutableStateFlow(NewsState.Loading)
    val state = _state.asStateFlow()

    init {
        subscribeToData()
        getNews(false)
    }

    private fun subscribeToData() {
        repository
            .dataFlow
            .onEach {
                when (it) {
                    is Result.Error -> {
                        _state.emit(NewsState.Error(it.error))
                    }
                    is Result.Success -> {
                        _state.emit(NewsState.Content(it.data))
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    fun updateNews() {
        getNews(true)
    }

    private fun getNews(forceUpdate: Boolean) {
        viewModelScope.launch {
            _state.tryEmit(NewsState.Loading)
            repository.getNews(forceUpdate)
        }
    }
}
