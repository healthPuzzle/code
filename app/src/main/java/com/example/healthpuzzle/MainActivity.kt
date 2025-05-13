package com.example.healthpuzzle

import PuzzleManager
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var routineAdapter: RoutineAdapter
    private val routineList = mutableListOf<RoutineItem>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.routine_recycler)
        val progressText: TextView = findViewById(R.id.progress_text)
        val addButton: FloatingActionButton = findViewById(R.id.add_routine_button)
        val dateText: TextView = findViewById(R.id.date_text)

        // 오늘 날짜 표시
        val today = Calendar.getInstance().time
        val formatter = SimpleDateFormat("M월 d일 E요일", Locale.KOREAN)
        dateText.text = formatter.format(today)

        // 어댑터 설정
        routineAdapter = RoutineAdapter(routineList) { position ->
            val item = routineList[position]
            routineList[position].isCompleted = !routineList[position].isCompleted
            routineAdapter.notifyItemChanged(position)

            // 일반 루틴 완료 여부 저장 (routine2_ 인덱스 기반)
            val prefs = getSharedPreferences("routine_prefs", MODE_PRIVATE)
            prefs.edit().putBoolean("completed_routine2_$position", item.isCompleted).apply()

            updateProgress(progressText)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = routineAdapter

        addButton.setOnClickListener {
            val intent = Intent(this, RoutineSettingActivity::class.java)
            startActivity(intent)
        }

        routineList.clear()
        loadRegularRoutines()
        loadDailyRoutines()
        updateProgress(progressText)

        // 하단 네비게이션
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    true
                }
                R.id.nav_routine -> {
                    val intent = Intent(this, RoutineSettingActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_puzzle -> {
                    val intent = Intent(this, PuzzleActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_mypage -> {
                    val intent = Intent(this, MyPageActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
    }

    private fun updateProgress(progressView: TextView) {
        val total = routineList.size
        val done = routineList.count { it.isCompleted }
        progressView.text = "$done/$total 완료"

        updateCompletedRoutineCount(done)
        checkAndShowPuzzleDialog()
    }

    private fun updateCompletedRoutineCount(count: Int) {
        val prefs = getSharedPreferences("routine_data", MODE_PRIVATE)
        val current = prefs.getInt("completed_routine_count", 0)
        if (count > current) {
            prefs.edit().putInt("completed_routine_count", count).apply()
        }
    }

    private fun checkAndShowPuzzleDialog() {
        if (routineList.isNotEmpty() && routineList.all { it.isCompleted }) {
            val puzzle = PuzzleManager.collectPuzzle()

            if (puzzle != null) {
                val prefs = getSharedPreferences("puzzle_data", MODE_PRIVATE)
                val currentSet = prefs.getStringSet("collected_puzzles", emptySet())?.toMutableSet() ?: mutableSetOf()
                currentSet.addAll(PuzzleManager.collectedPuzzles)

                val masterCount = prefs.getInt("puzzle_master_count", 0) + 1

                prefs.edit().apply {
                    putStringSet("collected_puzzles", currentSet)
                    putInt("puzzle_master_count", masterCount)
                    apply()
                }
            }

            showPuzzleDialog()
        }
    }

    private fun showPuzzleDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_puzzle_reward, null)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialogView.findViewById<Button>(R.id.btn_close).setOnClickListener {
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btn_go_puzzle).setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(this, PuzzleActivity::class.java))
        }

        dialog.show()
    }

    // ✅ 기본 루틴 로드 (요일 기준 필터링)
    private fun loadRegularRoutines() {
        // 정기 루틴 불러오기
        val regularRoutines = RoutineManager.getRegularRoutines()

        // 오늘 요일 가져오기
        val today = SimpleDateFormat("E", Locale.KOREAN).format(Date())

        // 해당 요일에 맞는 루틴만 필터링하여 표시
        val filteredRoutines = regularRoutines.filter { routine ->
            routine.days.contains(today)
        }

        // 루틴 리스트에 추가하여 화면에 표시
        routineList.addAll(filteredRoutines)
        routineAdapter.notifyDataSetChanged()
    }

    // ✅ 일반 루틴 로드 (오늘 날짜 기준 필터링)
    private fun loadDailyRoutines() {
        val todayDate = SimpleDateFormat("yyyyMMdd", Locale.KOREAN).format(Date())

        // RoutineManager에서 오늘 날짜의 루틴을 가져옴
        val dailyRoutines = RoutineManager.getTodayRoutines(todayDate)

        routineList.clear() // 기존 루틴을 클리어하고
        routineList.addAll(dailyRoutines) // 새로운 루틴 추가
        routineAdapter.notifyDataSetChanged()
    }

}