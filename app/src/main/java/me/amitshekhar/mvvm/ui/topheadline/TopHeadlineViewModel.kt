package me.amitshekhar.mvvm.ui.topheadline

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import me.amitshekhar.mvvm.data.model.MovieResponse
import me.amitshekhar.mvvm.data.repository.TopHeadlineRepository
import me.amitshekhar.mvvm.ui.base.UiState

class TopHeadlineViewModel(private val topHeadlineRepository: TopHeadlineRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<MovieResponse>>(UiState.Loading)

    val uiState: StateFlow<UiState<MovieResponse>> = _uiState

    init {
        fetchTopHeadlines()
    }

    private fun fetchTopHeadlines() {
        viewModelScope.launch {
            topHeadlineRepository.getTopHeadlines()
                .catch { e ->
                    _uiState.value = UiState.Error(e.toString())
                }
                .collect {
                    _uiState.value = UiState.Success(it)
                }
        }
    }

    fun fetchNextPage() {
        viewModelScope.launch {
            try {
                val nextPage = topHeadlineRepository.fetchNextPage()
                _uiState.value = UiState.Success(nextPage)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.toString())
            }
        }
    }

}