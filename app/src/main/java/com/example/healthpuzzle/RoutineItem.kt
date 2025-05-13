package com.example.healthpuzzle

data class RoutineItem(
    val title: String,
    val detail: String,
    val time: String,
    val days: List<String>,
    var isCompleted: Boolean = false,
    val addedDate: String? = null
)