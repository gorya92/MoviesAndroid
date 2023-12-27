package me.amitshekhar.mvvm.ui.topheadline

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import me.amitshekhar.mvvm.MVVMApplication
import me.amitshekhar.mvvm.data.model.MovieResponse
import me.amitshekhar.mvvm.databinding.ActivityTopHeadlineBinding
import me.amitshekhar.mvvm.di.component.DaggerActivityComponent
import me.amitshekhar.mvvm.di.module.ActivityModule
import me.amitshekhar.mvvm.ui.base.UiState
import javax.inject.Inject


class TopHeadlineActivity : AppCompatActivity() {

    @Inject
    lateinit var topHeadlineViewModel: TopHeadlineViewModel

    @Inject
    lateinit var adapter: TopHeadlineAdapter

    private lateinit var binding: ActivityTopHeadlineBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        injectDependencies()
        super.onCreate(savedInstanceState)
        binding = ActivityTopHeadlineBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupUI()
        setupObserver()
    }

    private fun setupUI() {
        val recyclerView = binding.recyclerView

        recyclerView.layoutManager = GridLayoutManager(this, 2)

        recyclerView.adapter = adapter


        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                if (lastVisibleItem + 1 == totalItemCount) {
                    topHeadlineViewModel.fetchNextPage()
                }
            }
        })

    }

    private fun setupObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                topHeadlineViewModel.uiState.collect {
                    when (it) {
                        is UiState.Success -> {
                            if (it.data?.data?.movies?.isNotEmpty() == true) {
                                binding.progressBar.visibility = View.GONE
                                renderList(it.data)
                                binding.recyclerView.visibility = View.VISIBLE
                            }
                        }

                        is UiState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                            binding.recyclerView.visibility = View.GONE
                        }
                        is UiState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(
                                this@TopHeadlineActivity,
                                it.message,
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private fun renderList(articleList: MovieResponse) {
        adapter.addData(articleList.data.movies)
        adapter.notifyDataSetChanged()
    }




    private fun injectDependencies() {
        DaggerActivityComponent.builder()
            .applicationComponent((application as MVVMApplication).applicationComponent)
            .activityModule(ActivityModule(this)).build().inject(this)
    }
}