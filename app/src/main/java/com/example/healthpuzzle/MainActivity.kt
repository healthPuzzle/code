package com.example.healthpuzzle

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
import java.util.Date
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

        // 오늘 날짜 세팅
        val today = Calendar.getInstance().time
        val formatter = SimpleDateFormat("M월 d일 E요일", Locale.KOREAN)
        val formattedDate = formatter.format(today)
        dateText.text = formattedDate

        // 루틴 어댑터 설정
        routineAdapter = RoutineAdapter(routineList) { position ->
            routineList[position].isCompleted = !routineList[position].isCompleted
            routineAdapter.notifyItemChanged(position)
            updateProgress(progressText)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = routineAdapter

        // Activity Result API 초기화
        addRoutineResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val title = result.data?.getStringExtra("title") ?: return@registerForActivityResult
                val detail = result.data?.getStringExtra("detail") ?: return@registerForActivityResult
                val time = result.data?.getStringExtra("time") ?: return@registerForActivityResult
                val days = result.data?.getStringArrayListExtra("days") ?: return@registerForActivityResult
                routineList.add(RoutineItem(
                    title, detail, time, days, isCompleted = false
                ))
                routineAdapter.notifyItemInserted(routineList.size - 1)
                updateProgress(progressText)
            }
        }

        addButton.setOnClickListener {
            val intent = Intent(this, RoutineSettingActivity::class.java)
            addRoutineResultLauncher.launch(intent)
        }

        updateProgress(progressText)

        // BottomNavigationView 설정
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottom_navigation)

        // 메뉴 클릭 리스너 설정
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

        checkAndShowPuzzleDialog()
    }

    private fun checkAndShowPuzzleDialog() {
        val prefs = getSharedPreferences("puzzle_prefs", MODE_PRIVATE)
        val today = SimpleDateFormat("yyyyMMdd", Locale.KOREAN).format(Date())

        val alreadyReceived = prefs.getBoolean(today, false)
        if (alreadyReceived) return

        if (routineList.isNotEmpty() && routineList.all { it.isCompleted }) {
            // 오늘 퍼즐 획득 처리
            prefs.edit().putBoolean(today, true).apply()
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