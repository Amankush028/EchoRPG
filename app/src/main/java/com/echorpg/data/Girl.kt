package com.echorpg.data

data class Girl(
    val id: Int,
    val name: String,
    val fromStory: String,
    val status: String = "Lover",
    val relationshipLevel: Int = 0
)