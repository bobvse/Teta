package ru.mts.data.news.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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
    suspend fun getNews(forceUpdate: Boolean): Flow<Result<List<News>, Throwable>> {
        return flow {
            if (forceUpdate) {
                getNewsFromSource().collect {
                    emit(it)
                    if (it is Result.Success) {
                        it.data.forEach { news ->
                            newsDao.update(news.toEntity())
                        }
                    }
                }
            } else {
                getNewsFromDB().collect {
                    emit(it)
                }
            }
        }
    }

    private suspend fun getNewsFromDB(): Flow<Result<List<News>, Throwable>> {
        return flow {
            withContext(Dispatchers.IO) {
                newsLocalDataSource.getNews()
            }.doOnSuccess {
                if (it.isNotEmpty()) {
                    emit(Result.Success(it.toDomain()))
                } else {
                    getNewsFromSource().collect {
                        emit(it)
                    }
                }
            }.doOnError {
                getNewsFromSource().collect {
                    emit(it)
                }
            }
        }
    }

    private suspend fun getNewsFromSource(): Flow<Result<List<News>, Throwable>> {
        return flow {
            withContext(Dispatchers.IO) {
                newsRemoteDataSource.getNews()
            }.doOnSuccess {
                emit(Result.Success(it.toDomain()))
            }.doOnError {
                emit(Result.Error(it))
            }
        }
    }
}
