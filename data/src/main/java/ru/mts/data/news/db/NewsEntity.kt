package ru.mts.data.news.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.mts.data.news.repository.News

@Entity(tableName = "news")
data class NewsEntity(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "desc") val description: String
)

internal fun List<NewsEntity?>.toDomain(): List<News> {
    return this.map { it.toDomain() }
}

fun NewsEntity?.toDomain() =
    News(
        id = this?.id ?: 0,
        title = this?.title ?: "",
        description = this?.description ?: ""
    )

fun News.toEntity() = NewsEntity(
    id = this.id, title = this.title, description = this.description
)