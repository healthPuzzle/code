package com.example.healthpuzzle

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RoutineSettingActivity : AppCompatActivity() {

    private lateinit var repeatLayout: LinearLayout
    private lateinit var titleInput: TextInputEditText
    private lateinit var detailInput: TextInputEditText
    private lateinit var hourSpinner: Spinner
    private lateinit var minuteSpinner: Spinner
    private lateinit var everyDayButton: Button
    private lateinit var dayButtons: List<Button>
    private lateinit var defaultCheckBox: CheckBox
    private lateinit var saveButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_routine_setting)

        repeatLayout = findViewById(R.id.repeat_layout)
        defaultCheckBox = findViewById(R.id.checkbox_default)

        defaultCheckBox.setOnCheckedChangeListener { _, isChecked ->
            repeatLayout.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        titleInput = findViewById(R.id.routine_title_input)
        detailInput = findViewById(R.id.routine_detail_input)
        hourSpinner = findViewById(R.id.spinner_hour)
        minuteSpinner = findViewById(R.id.spinner_minute)
        everyDayButton = findViewById(R.id.btn_every_day)
        defaultCheckBox = findViewById(R.id.checkbox_default)
        saveButton = findViewById(R.id.btn_save)

        dayButtons = listOf(
            findViewById(R.id.btn_mon),
            findViewById(R.id.btn_tue),
            findViewById(R.id.btn_wed),
            findViewById(R.id.btn_thu),
            findViewById(R.id.btn_fri),
            findViewById(R.id.btn_sat),
            findViewById(R.id.btn_sun)
        )

        setupSpinners()
        setupDayButtons()
        setupSaveButton()

        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigation.selectedItemId = R.id.nav_routine

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.nav_routine -> {
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

    private fun setupSpinners() {
        val hours = (0..23).map { String.format("%02d", it) }
        val minutes = listOf("00", "30")

        hourSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, hours)
        minuteSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, minutes)
    }

    private fun setupDayButtons() {
        val selectedColor = ContextCompat.getColor(this, R.color.purple_500)
        val unselectedColor = ContextCompat.getColor(this, R.color.purple_100)

        // 초기 상태: 모든 버튼 미선택
        everyDayButton.isSelected = false
        everyDayButton.setBackgroundColor(unselectedColor)

        dayButtons.forEach { button ->
            button.isSelected = false
            button.setBackgroundColor(unselectedColor)

            button.setOnClickListener {
                // 선택 상태 토글
                button.isSelected = !button.isSelected
                button.setBackgroundColor(if (button.isSelected) selectedColor else unselectedColor)

                // 모든 요일 버튼이 선택됐으면 매일 버튼도 선택
                val allSelected = dayButtons.all { it.isSelected }
                everyDayButton.isSelected = allSelected
                everyDayButton.setBackgroundColor(if (allSelected) selectedColor else unselectedColor)
            }
        }

        // 매일 버튼 클릭 시 → 매일 버튼 포함 모든 버튼 일괄 처리
        everyDayButton.setOnClickListener {
            val newState = !everyDayButton.isSelected

            // 매일 버튼 상태 변경
            everyDayButton.isSelected = newState
            everyDayButton.setBackgroundColor(if (newState) selectedColor else unselectedColor)

            // 요일 버튼들 상태 변경
            dayButtons.forEach { button ->
                button.isSelected = newState
                button.setBackgroundColor(if (newState) selectedColor else unselectedColor)
            }
        }
    }

    private fun setupSaveButton() {
        saveButton.setOnClickListener {
            val title = titleInput.text.toString().trim()
            val hour = hourSpinner.selectedItem.toString()
            val minute = minuteSpinner.selectedItem.toString()
            val time = "$hour:$minute"
            val isDefault = defaultCheckBox.isChecked

            if (title.isBlank()) {
                Toast.makeText(this, "루틴 이름을 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val days = dayButtons.filter { it.isSelected }.map { it.text.toString() }
            val todayDate = SimpleDateFormat("yyyyMMdd", Locale.KOREAN).format(Date())
            val routine = RoutineItem(title, detailInput.text.toString(), time, days)

            if (isDefault) {
                RoutineManager.addRoutine(routine) // RoutineManager에 기본 루틴 추가
                saveDefaultRoutineToDatabase(title, time, days) // SharedPreferences에 저장
            } else {
                RoutineManager.addRoutine(routine)  // 일반 루틴 추가
                saveRegularRoutineToDatabase(title, time, days) // 일반 루틴 저장
            }

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun saveDefaultRoutineToDatabase(title: String, time: String, days: List<String>) {
        val prefs = getSharedPreferences("routine_prefs", MODE_PRIVATE)
        val editor = prefs.edit()

        // 현재 저장된 루틴 개수 가져오기
        val count = prefs.getInt("routine_count", 0)

        // 새 루틴 저장
        editor.putString("routine_${count}_title", title)
        editor.putString("routine_${count}_time", time)
        editor.putStringSet("routine_${count}_days", days.toSet())

        // 루틴 개수 증가
        editor.putInt("routine_count", count + 1)

        editor.apply()

        Toast.makeText(this, "기본 루틴으로 저장되었습니다", Toast.LENGTH_SHORT).show()
    }

    private fun saveRegularRoutineToDatabase(title: String, time: String, days: List<String>) {
        val todayDate = SimpleDateFormat("yyyyMMdd", Locale.KOREAN).format(Date())
        val routine = RoutineItem(title, "", time, days, false, todayDate)
        RoutineManager.addRoutine(routine)
        RoutineManager.saveToPreferences(this)
        Toast.makeText(this, "일반 루틴이 저장되었습니다", Toast.LENGTH_SHORT).show()
    }

}