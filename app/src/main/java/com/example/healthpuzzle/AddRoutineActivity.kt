package com.example.healthpuzzle

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class AddRoutineActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_routine)

        val titleInput: EditText = findViewById(R.id.input_title)
        val timeInput: EditText = findViewById(R.id.input_time)
        val submitButton: Button = findViewById(R.id.submit_button)

        submitButton.setOnClickListener {
            val intent = Intent().apply {
                putExtra("title", titleInput.text.toString())
                putExtra("time", timeInput.text.toString())
            }
            setResult(RESULT_OK, intent)
            finish()
        }
    }
}