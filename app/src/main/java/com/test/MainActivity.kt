package com.test

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.mvpproject.Util.RestClient
import com.test.Adapter.SearchDataAdapter
import com.test.Listener.OnLoadMoreListener
import com.test.Model.RootModel
import com.test.Utility.AppConst
import com.test.Utility.isConnected
import com.test.Utility.showToast
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private var page = 1
    private var tag = "story"
    private var ndPage = 0
    private var isLoadMoreData = false
    private var callRootBack: Call<RootModel>? = null
    private var hitModelList: MutableList<RootModel.HitModel> = mutableListOf()
    private var searchDataAdapter: SearchDataAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpToolbar()
        initView()
        callSearchDataAPI()
    }

    private fun setUpToolbar() {

        supportActionBar!!.title = resources.getString(R.string.app_name)
    }

    private fun callSearchDataAPI() {
        callRootBack = RestClient(this).getApiService().searchData(tag, page)
        callRootBack?.enqueue(object : Callback<RootModel> {
            override fun onResponse(call: Call<RootModel>, response: Response<RootModel>) {
                if (response.code() == AppConst.SUCCESS_CODE) {
                    ndPage = response.body()?.nbPages!!
                    response.body()?.hits?.let { hitModelList.addAll(it) }
                    setUpSearchDataAdapter()
                }

                progressBarLoader.visibility = View.GONE
                swipeRefreshLayout.isRefreshing = false
            }

            override fun onFailure(call: Call<RootModel>, t: Throwable) {
                progressBarLoader.visibility = View.GONE
                swipeRefreshLayout.isRefreshing = false
                if (isConnected()) {
                    resources.getString(R.string.someting_wrong).showToast(this@MainActivity)
                } else {
                    if (hitModelList.size > 0) {
                        resources.getString(R.string.no_internet_connection).showToast(this@MainActivity)
                        textViewNoInternet.visibility = View.GONE

                    } else {
                        recyclerView.visibility=View.GONE
                        textViewNoInternet.visibility = View.VISIBLE
                    }

                }
            }

        })

    }

    // Set Up RecyclerView Adapter
    private fun setUpSearchDataAdapter() {
        if (!isLoadMoreData) {
            searchDataAdapter = SearchDataAdapter(this, hitModelList, onItemClick = ::onItemClick)
            recyclerView.adapter = searchDataAdapter
            searchDataAdapter?.setLoadMoreListener(object : OnLoadMoreListener {
                override fun onLoadMore() {
                    page += 1
                    isLoadMoreData = true
                    callSearchDataAPI()
                }

            })
        } else {
            if (ndPage == 0) {
                searchDataAdapter?.setMoreDataAvailable(false)
            }
            searchDataAdapter?.notifyDataChanged()
        }

        if (hitModelList.size > 0) {
            recyclerView.visibility=View.VISIBLE
            textViewNoInternet.visibility = View.GONE
        } else {
            recyclerView.visibility=View.GONE
            textViewNoInternet.visibility = View.VISIBLE
        }
    }



    // Initialing View
    private fun initView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
        swipeRefreshLayout.setOnRefreshListener {
            callRootBack?.let {
                it.cancel()
            }
            hitModelList.clear()
            isLoadMoreData = false
            page = 1
            searchDataAdapter?.let {
                it.selectedItem.clear()
                it.setMoreDataAvailable(false)
            }
            textViewNoInternet.visibility = View.GONE
            setUpToolbar()
            callSearchDataAPI()
        }

    }

    private fun onItemClick(position: Int) {
        if (searchDataAdapter?.selectedItem?.contains(position)!!) {
            searchDataAdapter?.selectedItem?.remove(position)
        } else {
            searchDataAdapter?.selectedItem?.add(position)
        }
        searchDataAdapter?.notifyItemChanged(position)
        if (searchDataAdapter?.selectedItem!!.size > 0) {
            supportActionBar!!.title =
                resources.getString(R.string.app_name).plus(" (").plus(searchDataAdapter?.selectedItem!!.size).plus(")")
        } else {
            setUpToolbar()
        }

    }
}
