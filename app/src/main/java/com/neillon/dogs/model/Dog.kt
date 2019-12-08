package com.neillon.dogs.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class Dog(
    @ColumnInfo(name = "id")
    @SerializedName("id")
    val breadId: String?,

    @ColumnInfo(name = "name")
    @SerializedName("name")
    val dogBread: String?,

    @ColumnInfo(name = "life_span")
    @SerializedName("life_span")
    val lifeSpan: String?,

    @ColumnInfo(name = "breed_group")
    @SerializedName("breed_group")
    val breadGroup: String?,

    @ColumnInfo(name = "bred_for")
    @SerializedName("bred_for")
    val breadFor: String?,

    @ColumnInfo(name = "temperament")
    @SerializedName("temperament")
    val temperament: String?,

    @ColumnInfo(name = "url")
    @SerializedName("url")
    val imageUrl: String?
) {
    @PrimaryKey(autoGenerate = true)
    var UUId: Int = 0
}

data class DogPalette(var color: Int)

data class SmsInfo(
    var to: String,
    val text: String,
    val imageUrl: String?
)