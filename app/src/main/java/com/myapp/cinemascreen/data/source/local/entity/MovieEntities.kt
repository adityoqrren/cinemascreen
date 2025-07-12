package com.myapp.cinemascreen.data.source.local.entity

import androidx.annotation.NonNull
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies", primaryKeys = ["id","c_id"])
data class MovieListItemEntity(
    var id: Int,
    val poster_path: String,
    var media_type: String = "",
    var time_added: Long = 0,
    @Embedded val category: CategoryEntity
)

@Entity("categories")
data class CategoryEntity(
    @PrimaryKey
    val c_id: Int,
    val name: String
)