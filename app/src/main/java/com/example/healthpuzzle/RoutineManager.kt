package com.example.healthpuzzle

object RoutineManager {
    val routinesByDay = mutableMapOf<String, MutableList<RoutineItem>>()
    val completedRoutines = mutableSetOf<String>()

    fun addRoutine(routine: RoutineItem) {
        for (day in routine.days) {
            if (!routinesByDay.containsKey(day)) {
                routinesByDay[day] = mutableListOf()
            }
            routinesByDay[day]?.add(routine)
        }
    }

    fun markRoutineCompleted(title: String) {
        completedRoutines.add(title)
    }

    fun resetDailyCompletion() {
        completedRoutines.clear()
    }

    fun getTodayRoutines(today: String): List<RoutineItem> {
        return routinesByDay[today] ?: emptyList()
    }

    fun getRegularRoutines(): List<RoutineItem> {
        return routinesByDay.values.flatten().distinctBy { it.title }
    }
}