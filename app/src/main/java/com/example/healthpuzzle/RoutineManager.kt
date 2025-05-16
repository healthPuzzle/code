package com.example.healthpuzzle

import android.content.Context
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object RoutineManager {
    val routinesByDay = mutableMapOf<String, MutableList<RoutineItem>>()
    val generalRoutines = mutableListOf<RoutineItem>() // 일반 루틴: 하루짜리

    fun addRoutine(routine: RoutineItem) {
        if (routine.addedDate != null) {
            // 일반 루틴 (하루만 노출)
            generalRoutines.add(routine)
        } else {
            // 정기 루틴
            for (day in routine.days) {
                if (!routinesByDay.containsKey(day)) {
                    routinesByDay[day] = mutableListOf()
                }
                routinesByDay[day]?.add(routine)
            }
        }
    }

    fun getTodayRoutines(today: String): List<RoutineItem> {
        val dayOfWeek = getTodayDayOfWeek()
        val regularRoutines = routinesByDay[dayOfWeek] ?: emptyList()
        val todayDate = getTodayDate()
        val validGeneralRoutines = generalRoutines.filter { it.addedDate == todayDate }
        return regularRoutines + validGeneralRoutines
    }

    fun getRegularRoutines(): List<RoutineItem> {
        return routinesByDay.values.flatten().distinctBy { it.title }
    }

    fun saveToPreferences(context: Context) {
        val prefs = context.getSharedPreferences("routine_prefs", Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.clear() // 기존 데이터 초기화

        // 정기 루틴 저장
        routinesByDay.forEach { (day, routines) ->
            val routineStrings = routines.joinToString(separator = ";") { routine ->
                "${routine.title},${routine.time},${routine.days.joinToString("|")},${routine.isCompleted},"
            }
            editor.putString("regular_$day", routineStrings)
        }

        // 일반 루틴 저장
        val generalRoutinesString = generalRoutines.joinToString(separator = ";") { routine ->
            "${routine.title},${routine.time},${routine.days.joinToString("|")},${routine.isCompleted},${routine.addedDate}"
        }
        editor.putString("general_routines", generalRoutinesString)

        editor.apply()
    }

    fun loadFromPreferences(context: Context) {
        val prefs = context.getSharedPreferences("routine_prefs", Context.MODE_PRIVATE)
        routinesByDay.clear()
        generalRoutines.clear()

        prefs.all.forEach { (key, value) ->
            if (value !is String) return@forEach
            val routineStrings = value.split(";")

            routineStrings.forEach { routineString ->
                val parts = routineString.split(",")
                if (parts.size < 4) return@forEach

                val title = parts[0]
                val time = parts[1]
                val days = parts[2].split("|")
                val isCompleted = parts[3].toBoolean()
                val addedDate = if (parts.size >= 5) parts[4] else null

                val routine = RoutineItem(title, "", time, days, isCompleted, addedDate)

                if (key.startsWith("regular_")) {
                    for (day in days) {
                        if (!routinesByDay.containsKey(day)) {
                            routinesByDay[day] = mutableListOf()
                        }
                        routinesByDay[day]?.add(routine)
                    }
                } else if (key == "general_routines") {
                    generalRoutines.add(routine)
                }
            }
        }
    }

    private fun getTodayDayOfWeek(): String {
        val calendar = Calendar.getInstance()
        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.SUNDAY -> "일"
            Calendar.MONDAY -> "월"
            Calendar.TUESDAY -> "화"
            Calendar.WEDNESDAY -> "수"
            Calendar.THURSDAY -> "목"
            Calendar.FRIDAY -> "금"
            Calendar.SATURDAY -> "토"
            else -> ""
        }
    }

    private fun getTodayDate(): String {
        return SimpleDateFormat("yyyyMMdd", Locale.KOREAN).format(Date())
    }
}