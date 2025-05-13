package com.example.healthpuzzle

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class RoutineAdapter(
    private val items: MutableList<RoutineItem>,
    private val onChecked: (Int) -> Unit
) : RecyclerView.Adapter<RoutineAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val card: CardView = view.findViewById(R.id.routineCard)
        val title: TextView = view.findViewById(R.id.routineTitle)
        val time: TextView = view.findViewById(R.id.routineTime)
        val statusText: TextView = view.findViewById(R.id.status_text)
        val checkIcon: ImageView = view.findViewById(R.id.check_icon)
        val leftIndicator: View = view.findViewById(R.id.left_indicator)

        init {
            val toggleComplete = {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onChecked(position)
                }
            }
            checkIcon.setOnClickListener { toggleComplete() }
            card.setOnClickListener { toggleComplete() }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_routine, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.title.text = item.title
        holder.time.text = item.time

        // 취소선 처리
        holder.title.paintFlags = if (item.isCompleted) {
            holder.title.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            holder.title.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }

        // 완료 상태에 따른 디자인 처리
        if (item.isCompleted) {
            holder.statusText.visibility = View.VISIBLE
            holder.statusText.text = "완료됨"
            holder.checkIcon.setImageResource(R.drawable.ic_check)
            holder.checkIcon.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.circle_check_background)
            holder.title.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.green_500))
            holder.leftIndicator.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.green_500))
        } else {
            holder.statusText.visibility = View.GONE
            holder.checkIcon.setImageDrawable(null)
            holder.checkIcon.background = ContextCompat.getDrawable(holder.itemView.context, R.drawable.circle_outline_background)
            holder.title.setTextColor(ContextCompat.getColor(holder.itemView.context, R.color.purple_500))
            holder.leftIndicator.setBackgroundColor(ContextCompat.getColor(holder.itemView.context, R.color.purple_500))
        }
    }
}
