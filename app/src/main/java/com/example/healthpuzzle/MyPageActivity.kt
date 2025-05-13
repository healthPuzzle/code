package com.example.healthpuzzle

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MyPageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypage)

        // 정기 루틴 표시
        val regularRoutines = RoutineManager.getRegularRoutines()
        Log.d("MyPageActivity", "Regular routines in MyPage: $regularRoutines")

        // SharedPreferences에서 루틴 개수 가져오기
        val prefs = getSharedPreferences("routine_prefs", MODE_PRIVATE)
        val defaultRoutineCount = prefs.getInt("routine_count", 0)

        if (regularRoutines.isNotEmpty()) {
            val routineSection = findViewById<LinearLayout>(R.id.layout_regular_routine)
            routineSection.visibility = View.VISIBLE
            val routinesToShow = regularRoutines.take(defaultRoutineCount)

            // 루틴 목록을 동적으로 추가
            for (routine in routinesToShow) {
                val routineLayout = LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    setPadding(16, 16, 16, 16)
                    background = getDrawable(R.drawable.card_bg)
                    // 레이아웃 크기 설정
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        bottomMargin = 12  // 항목 간 간격 설정
                    }

                    setOnClickListener {
                        showRoutineDetailsDialog(routine)
                    }
                }

                val titleView = TextView(this).apply {
                    text = "💪 ${routine.title}"
                    textSize = 14f
                    setTypeface(null, Typeface.BOLD)
                    // 레이아웃 파라미터 설정
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        bottomMargin = 4  // 제목과 시간 사이 간격 설정
                    }
                }

                val timeView = TextView(this).apply {
                    text = "${routine.days.joinToString(", ")} - ${routine.time}"
                    textSize = 12f
                    setTextColor(Color.parseColor("#666666"))
                    // 레이아웃 파라미터 설정
                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        topMargin = 4  // 제목과 시간 사이 간격 설정
                    }
                }

                // 루틴 항목을 추가
                routineLayout.addView(titleView)
                routineLayout.addView(timeView)

                // 정기 루틴 섹션에 추가
                routineSection.addView(routineLayout)
            }
        } else {
            val routineSection = findViewById<LinearLayout>(R.id.layout_regular_routine)
            routineSection.visibility = View.GONE  // 정기 루틴이 없으면 섹션 숨기기
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
                R.id.nav_mypage -> {
                    true
                }
                else -> false
            }
        }
    }

    // 이름 수정 다이얼로그
    fun showNameEditDialog(view: View) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_name_edit, null)
        val nameEditText: EditText = dialogView.findViewById(R.id.editTextName)

        val builder = AlertDialog.Builder(this)
        builder.setTitle("이름 변경")
        builder.setView(dialogView)

        builder.setPositiveButton("확인") { dialog, _ ->
            val newName = nameEditText.text.toString()
            dialog.dismiss()
        }

        builder.setNegativeButton("취소") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    // 키/몸무게 수정 다이얼로그
    fun showHeightWeightEditDialog(view: View) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_height_weight_edit, null)
        val heightEditText: EditText = dialogView.findViewById(R.id.editTextHeight)
        val weightEditText: EditText = dialogView.findViewById(R.id.editTextWeight)

        val builder = AlertDialog.Builder(this)
        builder.setTitle("키/몸무게 변경")
        builder.setView(dialogView)

        builder.setPositiveButton("확인") { dialog, _ ->
            val newHeight = heightEditText.text.toString()
            val newWeight = weightEditText.text.toString()
            dialog.dismiss()
        }

        builder.setNegativeButton("취소") { dialog, _ -> dialog.dismiss() }
        builder.show()
    }

    // 루틴 세부사항을 보여주는 모달
    private fun showRoutineDetailsDialog(routine: RoutineItem) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_routine_details, null)

        val titleView = dialogView.findViewById<TextView>(R.id.dialog_routine_title)
        val detailsView = dialogView.findViewById<TextView>(R.id.dialog_routine_details)
        val daysView = dialogView.findViewById<TextView>(R.id.dialog_routine_days)
        val timeView = dialogView.findViewById<TextView>(R.id.dialog_routine_time)

        // 루틴 정보를 모달에 세팅
        titleView.text = "💪 ${routine.title}"
        detailsView.text = "상세 내용: ${routine.detail}" // 루틴 상세 내용 추가
        daysView.text = "요일: ${routine.days.joinToString(", ")}"
        timeView.text = "시간: ${routine.time}"

        // 모달의 '닫기' 버튼을 클릭했을 때 다이얼로그 닫기
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)  // 외부 클릭으로 모달을 닫지 않도록 설정
            .setPositiveButton("닫기") { dialogInterface, _ ->
                dialogInterface.dismiss()  // 다이얼로그 닫기
            }
            .setTitle("루틴 상세 정보")
            .create()

        // 모달을 띄움
        dialog.show()
    }
}