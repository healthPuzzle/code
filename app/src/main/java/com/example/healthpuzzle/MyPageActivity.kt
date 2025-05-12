package com.example.healthpuzzle

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MyPageActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypage)

        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
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
}