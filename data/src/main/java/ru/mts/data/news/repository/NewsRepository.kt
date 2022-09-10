package ru.mts.data.news.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import ru.mts.data.news.db.NewsLocalDataSource
import ru.mts.data.news.remote.NewsRemoteDataSource
import ru.mts.data.news.remote.toDomain
import ru.mts.data.utils.Result
import ru.mts.data.utils.doOnError
import ru.mts.data.utils.doOnSuccess

class NewsRepository(
    private val newsLocalDataSource: NewsLocalDataSource,
    private val newsRemoteDataSource: NewsRemoteDataSource
) {
    suspend fun getNews(): Flow<Result<List<News>, Throwable>> {
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
