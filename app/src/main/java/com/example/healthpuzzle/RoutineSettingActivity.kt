package com.example.healthpuzzle

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class RoutineSettingActivity : AppCompatActivity() {

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
        setupEveryDayButton()
        setupSaveButton()

        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigation.selectedItemId = R.id.nav_routine

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
                R.id.nav_routine -> true
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

    private fun setupEveryDayButton() {
        everyDayButton.setOnClickListener {
            dayButtons.forEach { it.isSelected = true }
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

            /*val routine = RoutineItem(title, detailInput.text.toString(), time, days)

            if (isDefault) {
                RoutineManager.addRoutine(routine)
                saveRoutineToDatabase(title, time, days)  // 요일 데이터도 저장
            } else {
                val intent = Intent().apply {
                    putExtra("title", title)
                    putExtra("time", time)
                }
                setResult(RESULT_OK, intent)
            }

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }*/

            val intent = Intent().apply {
                putExtra("title", title)
                putExtra("detail", detailInput.text.toString())
                putExtra("time", time)
                putStringArrayListExtra("days", ArrayList(days))
            }

            // 기본 루틴이면 RoutineManager에 등록
            if (isDefault) {
                RoutineManager.addRoutine(
                    RoutineItem(title, detailInput.text.toString(), time, days)
                )
                saveRoutineToDatabase(title, time, days)
            }

            setResult(RESULT_OK, intent)
            finish()
        }
    }

    private fun saveRoutineToDatabase(title: String, time: String, days: List<String>) {
        val prefs = getSharedPreferences("routine_prefs", MODE_PRIVATE)
        prefs.edit().apply {
            putString("routine_title", title)
            putString("routine_time", time)
            putStringSet("routine_days", days.toSet())
            apply()
        }

        Toast.makeText(this, "기본 루틴으로 저장되었습니다", Toast.LENGTH_SHORT).show()
    }
}
