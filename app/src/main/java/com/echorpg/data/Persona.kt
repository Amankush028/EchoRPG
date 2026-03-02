package com.echorpg.data

data class Persona(
    val name: String = "You",
    val title: String = "Sir",
    val age: String = "28",
    val appearance: String = "Tall, muscular, commanding presence",
    val vibe: String = "Dominant",
    val sexualRole: String = "Dominant",     // ← NEW (used in prompt)
    val kinks: List<String> = emptyList(),
    val hardLimits: String = "No blood, no underage, no scat"
)