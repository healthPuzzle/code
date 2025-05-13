package com.example.healthpuzzle

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MyPageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypage)

        // 퍼즐 수 카운트 표시
        val puzzlePrefs = getSharedPreferences("puzzle_data", MODE_PRIVATE)
        val puzzleCount = puzzlePrefs.getStringSet("collected_puzzles", emptySet())?.size ?: 0

        val puzzleCountView = findViewById<TextView>(R.id.text_puzzle_count)
        puzzleCountView.text = "$puzzleCount"

        // 퍼즐 마스터 업적 카운터
        val puzzleMasterCount = puzzlePrefs.getInt("puzzle_master_count", 0)
        val masterProgress = findViewById<ProgressBar>(R.id.progress_puzzle_master)
        val masterText = findViewById<TextView>(R.id.text_puzzle_master_count)

        masterProgress.max = 15
        masterProgress.progress = puzzleMasterCount
        masterText.text = "$puzzleMasterCount/15"

        // 완료한 루틴 수 카운트 표시
        val routinePrefs = getSharedPreferences("routine_data", MODE_PRIVATE)
        val completedCount = routinePrefs.getInt("completed_routine_count", 0)

        val completedTextView = findViewById<TextView>(R.id.text_completed_count)
        completedTextView.text = "$completedCount"

        // 정기 루틴 표시
        val regularRoutines = RoutineManager.getRegularRoutines()

        if (regularRoutines.isNotEmpty()) {
            val routineSection = findViewById<LinearLayout>(R.id.layout_regular_routine)
            routineSection.visibility = View.VISIBLE

            val titleView = routineSection.findViewById<TextView>(R.id.text_regular_title)
            val timeView = routineSection.findViewById<TextView>(R.id.text_regular_time)

            val routine = regularRoutines.first()
            titleView.text = "💪 ${routine.title}"
            timeView.text = routine.time
        }

        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigation.selectedItemId = R.id.nav_mypage

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.nav_routine -> {
                    startActivity(Intent(this, RoutineSettingActivity::class.java))
                    true
                }
                R.id.nav_puzzle -> {
                    startActivity(Intent(this, PuzzleActivity::class.java))
                    true
                }
                R.id.nav_mypage -> true
                else -> false
            }
        }
    }

    fun showNameEditDialog(view: View) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_name_edit, null)
        val nameEditText: EditText = dialogView.findViewById(R.id.editTextName)

        val builder = AlertDialog.Builder(this)
            .setTitle("이름 변경")
            .setView(dialogView)
            .setPositiveButton("확인") { dialog, _ ->
                val newName = nameEditText.text.toString()
                dialog.dismiss()
            }
            .setNegativeButton("취소") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    fun showHeightWeightEditDialog(view: View) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_height_weight_edit, null)
        val heightEditText: EditText = dialogView.findViewById(R.id.editTextHeight)
        val weightEditText: EditText = dialogView.findViewById(R.id.editTextWeight)

        val builder = AlertDialog.Builder(this)
            .setTitle("키/몸무게 변경")
            .setView(dialogView)
            .setPositiveButton("확인") { dialog, _ ->
                val newHeight = heightEditText.text.toString()
                val newWeight = weightEditText.text.toString()
                dialog.dismiss()
            }
            .setNegativeButton("취소") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }
}
