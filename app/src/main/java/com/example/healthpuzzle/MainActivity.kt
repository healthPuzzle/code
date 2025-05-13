package com.example.healthpuzzle

import PuzzleManager
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var routineAdapter: RoutineAdapter
    private val routineList = mutableListOf<RoutineItem>()
    private lateinit var addRoutineResultLauncher: ActivityResultLauncher<Intent>

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
            routineAdapter.notifyItemChanged(position)
            recyclerView.post {
                updateProgress(progressText)
            }
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = routineAdapter

        // 루틴 추가 ActivityResult
        addRoutineResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val title = result.data?.getStringExtra("title") ?: return@registerForActivityResult
                val detail = result.data?.getStringExtra("detail") ?: return@registerForActivityResult
                val time = result.data?.getStringExtra("time") ?: return@registerForActivityResult
                val days = result.data?.getStringArrayListExtra("days") ?: return@registerForActivityResult

                routineList.add(RoutineItem(title, detail, time, days, false))
                routineAdapter.notifyItemInserted(routineList.size - 1)
                updateProgress(progressText)
            }
        }

        addButton.setOnClickListener {
            val intent = Intent(this, RoutineSettingActivity::class.java)
            addRoutineResultLauncher.launch(intent)
        }

        updateProgress(progressText)

        // 하단 네비게이션
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true
                R.id.nav_routine -> {
                    startActivity(Intent(this, RoutineSettingActivity::class.java))
                    true
                }
                R.id.nav_puzzle -> {
                    startActivity(Intent(this, PuzzleActivity::class.java))
                    true
                }
                R.id.nav_mypage -> {
                    startActivity(Intent(this, MyPageActivity::class.java))
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
}
