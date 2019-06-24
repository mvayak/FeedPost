package com.test.Utility

import android.view.View
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.test.R


class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var progressBar: ProgressBar = itemView.findViewById(R.id.progressBarLoader)
}