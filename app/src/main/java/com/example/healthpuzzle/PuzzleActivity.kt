package com.example.healthpuzzle

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.healthpuzzle.databinding.ActivityPuzzleBinding
import com.example.healthpuzzle.databinding.PuzzleSectionBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.app.AlertDialog
import androidx.core.content.ContextCompat

class PuzzleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPuzzleBinding
    private val puzzleData = listOf(
        PuzzleSection("헬린이", 9, R.id.section_child),
        PuzzleSection("헬스러", 9, R.id.section_adult),
    )

    private val acquiredMap = mapOf(
        "헬린이" to listOf(0, 1, 2, 3, 4, 5, 6, 7, 8),
        "헬스러" to listOf(0, 1, 2, 3),
    )

    private val imageResMap: Map<String, List<Int>> = mapOf(
        "헬린이" to listOf(
            R.drawable.child_0, R.drawable.child_1, R.drawable.child_2, R.drawable.child_3,
            R.drawable.child_4, R.drawable.child_5, R.drawable.child_6, R.drawable.child_7,
            R.drawable.child_8
        ),
        "헬스러" to listOf(
            R.drawable.adult_0, R.drawable.adult_1, R.drawable.adult_2, R.drawable.adult_3,
            R.drawable.adult_4, R.drawable.adult_5, R.drawable.adult_6, R.drawable.adult_7,
            R.drawable.adult_8
        )
    )

    // 완성 이미지
    private val completeImageMap = mapOf(
        "헬린이" to R.drawable.child,
        "헬스러" to R.drawable.adult
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPuzzleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var totalAcquired = 0
        var totalPieces = 0

        puzzleData.forEach { section ->
            val sectionView = findViewById<View>(section.sectionId)
            val sectionBinding = PuzzleSectionBinding.bind(sectionView)

            val acquiredList = acquiredMap[section.title] ?: emptyList()
            val imageList = imageResMap[section.title] ?: emptyList()

            sectionBinding.puzzleTitle.text = section.title
            sectionBinding.puzzleGrid.removeAllViews()

            // 퍼즐 조각 추가
            for (i in 0 until section.totalCount) {
                val imageView = ImageView(this).apply {
                    layoutParams = GridLayout.LayoutParams().apply {
                        width = 0
                        height = 120
                        columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                    }
                    scaleType = ImageView.ScaleType.CENTER_CROP
                    setImageResource(
                        if (i in acquiredList) imageList[i] else R.drawable.locked_piece
                    )
                    setOnClickListener {
                        if (i in acquiredList) {
                            showZoomDialog(imageList[i])
                        } else {
                            Toast.makeText(context, "획득하지 않은 퍼즐입니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                sectionBinding.puzzleGrid.addView(imageView)
            }

            sectionBinding.progressBar.max = section.totalCount
            sectionBinding.progressBar.progress = acquiredList.size
            sectionBinding.puzzleCount.text = "${acquiredList.size}/${section.totalCount} 조각"

            totalAcquired += acquiredList.size
            totalPieces += section.totalCount

            // 퍼즐 완성 여부에 따라 버튼 상태 조절
            if (acquiredList.size == section.totalCount) {
                sectionBinding.allPuzzle.isEnabled = true
                sectionBinding.allPuzzle.backgroundTintList =
                    ContextCompat.getColorStateList(this, R.color.purple_500)
            } else {
                sectionBinding.allPuzzle.isEnabled = false
                sectionBinding.allPuzzle.backgroundTintList =
                    ContextCompat.getColorStateList(this, android.R.color.darker_gray)
            }

            // 전체보기 버튼
            sectionBinding.allPuzzle.setOnClickListener {
                if (acquiredList.size == section.totalCount) {
                    val completeImage = completeImageMap[section.title]
                    if (completeImage != null) {
                        showZoomDialog(completeImage)
                    } else {
                        Toast.makeText(this, "완성 이미지를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.totalCountText.text = "$totalAcquired/$totalPieces"

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.selectedItemId = R.id.nav_puzzle // 현재 페이지 강조

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    true
                }
//                R.id.nav_routine -> {
//                    startActivity(Intent(this, RoutineSettingsActivity::class.java))
//                    true
//                }
                R.id.nav_puzzle -> true // 현재 페이지
//                R.id.nav_mypage -> {
//                    startActivity(Intent(this, MyPageActivity::class.java))
//                    true
//                }
                else -> false
            }
        }

    }

    private fun showZoomDialog(imageResId: Int) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_puzzle_zoom, null)
        val imageView = dialogView.findViewById<ImageView>(R.id.zoomedImage)
        imageView.setImageResource(imageResId)

        AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()
            .show()
    }
}