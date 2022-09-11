package ru.mts.data.news.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.withContext
import ru.mts.data.news.db.NewsDao
import ru.mts.data.news.db.NewsLocalDataSource
import ru.mts.data.news.db.toDomain
import ru.mts.data.news.db.toEntity
import ru.mts.data.news.remote.NewsRemoteDataSource
import ru.mts.data.news.remote.toDomain
import ru.mts.data.utils.Result
import ru.mts.data.utils.doOnError
import ru.mts.data.utils.doOnSuccess

class NewsRepository(
    private val newsLocalDataSource: NewsLocalDataSource,
    private val newsRemoteDataSource: NewsRemoteDataSource,
    private val newsDao: NewsDao
) {
    private val _dataFlow: MutableSharedFlow<Result<List<News>, Throwable>> =
        MutableSharedFlow(1)
    val dataFlow = _dataFlow.asSharedFlow()

    suspend fun getNews(forceUpdate: Boolean) {
        if (forceUpdate) {
            getNewsFromSource()
        } else {
            getNewsFromDB()
        }
    }

    private suspend fun getNewsFromDB() {
        withContext(Dispatchers.IO) {
            newsLocalDataSource.getNews()
        }.doOnSuccess {
            if (it.isNotEmpty()) {
                _dataFlow.emit(Result.Success(it.toDomain()))
            } else {
                getNewsFromSource()
            }
        }.doOnError {
            _dataFlow.emit(Result.Error(it))
        }
    }

    private suspend fun getNewsFromSource() {
        withContext(Dispatchers.IO) {
            newsRemoteDataSource.getNews()
        }.doOnSuccess {
            val newsList = it.toDomain()
            updateDB(newsList)
            _dataFlow.emit(Result.Success(newsList))
        }.doOnError {
            _dataFlow.emit(Result.Error(it))
        }
    }

    private suspend fun updateDB(news: List<News>) {
        withContext(Dispatchers.IO) {
            newsDao.getAll()?.forEach { news ->
                newsDao.delete(news)
            }

            news.forEach {
                newsDao.update(it.toEntity())
            }
        }
    }
}
