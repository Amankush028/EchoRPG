package com.echorpg.data

data class Story(
    val id: Int,
    val title: String,
    val teaser: String,
    val emoji: String,           // placeholder instead of image for now
    val color: Long
)