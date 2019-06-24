package com.feed.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.feed.Model.RootModel
import com.feed.R
import com.feed.Utility.AppConst.Companion.VIEW_TYPE_ITEM
import com.feed.Utility.AppConst.Companion.VIEW_TYPE_LOADING
import com.feed.Utility.LoadingViewHolder
import com.feed.Utility.convertDateInputFormatToGiveDateFormat

class SearchDataAdapter(
    private val context: Context,
    private val searchList: List<RootModel.HitModel>?,
    private val onItemClick: (Int) -> Unit = {}
) :
    BaseRecyclerViewAdapter<RecyclerView.ViewHolder>() {
     var selectedItem:MutableList<Int> = mutableListOf()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_ITEM) {
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_recyclerview_item, parent, false))
        } else {
            LoadingViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.recycler_item_progess_bar,
                    parent,
                    false
                )
            )
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position >= itemCount - 1 && isMoreDataExists && !isLoading && loadMoreDataListener != null) {
            isLoading = true
            loadMoreDataListener!!.onLoadMore()
        }
        if (getItemViewType(position) == VIEW_TYPE_ITEM) {
            searchList?.let {
                (holder as ViewHolder).bindData(it[position])
            }

        }
    }


    override fun getItemViewType(position: Int): Int {
        return if (isMoreDataExists && position >= searchList!!.size) VIEW_TYPE_LOADING else VIEW_TYPE_ITEM
    }

    override fun getItemCount(): Int {
        return if (isMoreDataExists) searchList!!.size + 1 else searchList!!.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewPostTitle: TextView = itemView.findViewById(R.id.textViewPostTitle)
        private val textViewCreateAt: TextView = itemView.findViewById(R.id.textViewCreateAt)
        private val switchToggle: Switch = itemView.findViewById(R.id.switchToggle)

        init {
            // Make switch click
            switchToggle.setOnClickListener {

                onItemClick(adapterPosition)


            }
        }

        fun bindData(hitModel: RootModel.HitModel) {
            // Assign value of view
            hitModel.title?.let {
                textViewPostTitle.text = it
            }
            hitModel.createdAt?.let {
                textViewCreateAt.text =
                    it.convertDateInputFormatToGiveDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "dd MMM, yyyy")
            }

            switchToggle.isChecked = selectedItem.contains(adapterPosition)

        }
    }


}
