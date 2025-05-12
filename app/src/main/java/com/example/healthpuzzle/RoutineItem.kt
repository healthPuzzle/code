package com.example.healthpuzzle

data class RoutineItem(
    val title: String,
    val time: String,
    var isCompleted: Boolean = false
)