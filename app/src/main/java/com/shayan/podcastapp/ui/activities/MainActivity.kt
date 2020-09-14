package com.shayan.podcastapp.ui.activities

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.shayan.podcastapp.App
import com.shayan.podcastapp.R
import com.shayan.podcastapp.adapters.PodcastAdapter
import com.shayan.podcastapp.network.PodcastApi
import com.shayan.podcastapp.network.models.PodcastListModel
import com.shayan.podcastapp.network.models.PodcastModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.TimeUnit
import android.util.Pair as UtilPair

class MainActivity : AppCompatActivity(), PodcastAdapter.Interaction {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Api Licensing
        Observable.timer(2,TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { lnSnackbar.show() }

        init()
        observeUiPreferences()
        retrievePodcastList(nextPageNumber)
    }

    // LazyLoading page number
    private var nextPageNumber = 1

    private var isLoading = false
    private var isDoubleGrid = true

    // Views declaration
    private lateinit var lnSnackbar: Snackbar
    private lateinit var recyclerView: RecyclerView
    private lateinit var pb: ProgressBar
    private lateinit var errorTv: TextView

    // RecyclerView essentials declaration
    private lateinit var adapter: PodcastAdapter
    private lateinit var list: MutableList<PodcastModel>
    private lateinit var layoutManager: GridLayoutManager

    private fun init() {
        // View Binding
        recyclerView = main_rv
        pb = main_pb
        errorTv = main_error

        setupLicenseSnackbar()

        addTopMarginToAppbar()

        // RecyclerView essentials initialization
        list = ArrayList()
        adapter = PodcastAdapter(this)
        recyclerView.adapter = adapter
        layoutManager = (recyclerView.layoutManager as GridLayoutManager)

        main_appbar_gridtoggle.setOnClickListener { gridToggleClickHandler() }
        main_appbar_refresh.setOnClickListener { refreshClickHandler() }

        setupLazyLoading()
    }

    private fun setupLicenseSnackbar() {
        // License Snackbar setup
        lnSnackbar = Snackbar.make(recyclerView,getString(R.string.license_text),Snackbar.LENGTH_LONG)
        lnSnackbar.setBackgroundTint(getColor(R.color.colorAccent))
        lnSnackbar.setTextColor(getColor(R.color.colorTextPrimary))
    }

    private fun setupLazyLoading() {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1) && !isLoading) {
                    retrievePodcastList(nextPageNumber)
                }
            }
        })
    }

    private fun refreshClickHandler() {
        // Reset nextPageNumber
        nextPageNumber = 1
        retrievePodcastList(nextPageNumber)
    }

    private fun gridToggleClickHandler() {
        // setDoubleGrid is a suspend function so it needs to run within a lifecycleScope
        lifecycleScope.launch {
            when (isDoubleGrid) {
                true -> App.preferences.setDoubleGrid(false)
                false -> App.preferences.setDoubleGrid(true)
            }
        }
    }

    // Watches the data stored in DataStore for changes
    private fun observeUiPreferences() {
        App.preferences.doubleGrid.asLiveData().observe(this) {
            isDoubleGrid = it
            when (it) {
                true -> layoutManager.spanCount = 2
                false -> layoutManager.spanCount = 1
            }
        }
    }

    private fun retrievePodcastList(nextPage: Int) {
        isLoading = true

        if (nextPage == 1) {
            // Clean up the RecyclerView
            recyclerView.scrollToPosition(0)
            list = ArrayList()
            setState(States.LOADING_INITIAL)
        } else {
            //Lazy Load new items
            setState(States.LOADING_LAZY)
        }

        PodcastApi.retrofitService.getPodcasts(page = nextPage)
            .enqueue(object : Callback<PodcastListModel> {
                override fun onFailure(call: Call<PodcastListModel>, t: Throwable) {
                    Log.e("error", t.message.toString())
                    setState(States.ERROR_CONNECTION)
                    isLoading = false
                }

                override fun onResponse(
                    call: Call<PodcastListModel>,
                    response: Response<PodcastListModel>
                ) {
                    isLoading = false
                    when {
                        response.code() == 200 -> {
                            handleResponse(response.body()!!)
                            setState(States.LOADED)
                        }
                        else -> {
                            setState(States.ERROR_DATA)
                        }
                    }
                }
            })
    }

    private fun handleResponse(podcastList: PodcastListModel) {
        // Set new page number for later loading
        nextPageNumber = podcastList.next_page_number

        // Append new items to list
        val newList = podcastList.podcasts
        list.addAll(newList)

        adapter.submitList(list)
        adapter.notifyDataSetChanged()
    }

    private fun setState(state: States) {
        when (state) {
            States.LOADED -> {
                recyclerView.visibility = View.VISIBLE
                pb.visibility = View.GONE
                errorTv.visibility = View.GONE
                main_appbar_pb.visibility = View.GONE
            }
            States.LOADING_INITIAL -> {
                recyclerView.visibility = View.GONE
                pb.visibility = View.VISIBLE
                errorTv.visibility = View.GONE
                main_appbar_pb.visibility = View.GONE
            }
            States.LOADING_LAZY -> {
                recyclerView.visibility = View.VISIBLE
                main_appbar_pb.visibility = View.VISIBLE
                pb.visibility = View.GONE
                errorTv.visibility = View.GONE
            }
            States.ERROR_CONNECTION -> {
                recyclerView.visibility = View.GONE
                pb.visibility = View.GONE
                errorTv.visibility = View.VISIBLE
                errorTv.text = getText(R.string.connection_error)
                main_appbar_pb.visibility = View.GONE
            }
            States.ERROR_DATA -> {
                recyclerView.visibility = View.GONE
                pb.visibility = View.GONE
                errorTv.visibility = View.VISIBLE
                errorTv.text = getText(R.string.data_retrieval_error)
                main_appbar_pb.visibility = View.GONE
            }
        }
    }

    override fun onItemSelected(
        position: Int,
        item: PodcastModel,
        tv: TextView,
        iv: ImageView,
        gradient: ImageView
    ) {

        val intent = Intent(this, PodcastListActivity::class.java)

        // Shared Elements
        val options = ActivityOptions.makeSceneTransitionAnimation(
            this,
            UtilPair.create<View, String>(iv, "image"),
            UtilPair.create<View, String>(tv, "title"),
            UtilPair.create<View, String>(gradient, "gradient")
        )

        intent.putExtra("podcast", item)
        startActivity(intent, options.toBundle())
    }


    // Translucent NavigationBar pushes the layout up
    // This makes up for it by adding a top margin with the size of status bar
    private fun addTopMarginToAppbar() {
        (main_appbar.layoutParams as ConstraintLayout.LayoutParams).setMargins(
            0,
            getStatusBarHeight(),
            0,
            0
        )
    }

    private fun getStatusBarHeight(): Int {
        var result = 0
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) result = resources.getDimensionPixelSize(resourceId)
        return result
    }
}

enum class States {
    LOADING_INITIAL, LOADING_LAZY, LOADED, ERROR_DATA, ERROR_CONNECTION
}


