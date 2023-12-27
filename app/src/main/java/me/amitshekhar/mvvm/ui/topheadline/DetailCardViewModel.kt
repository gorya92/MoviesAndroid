package me.amitshekhar.mvvm.ui.topheadline

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import me.amitshekhar.mvvm.data.model.MovieOneResponse
import me.amitshekhar.mvvm.data.model.MovieResponse
import me.amitshekhar.mvvm.data.repository.TopHeadlineRepository
import me.amitshekhar.mvvm.ui.base.UiState

class DetailCardViewModel(private val topHeadlineRepository: TopHeadlineRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<MovieOneResponse>>(UiState.Loading)

    val uiState: StateFlow<UiState<MovieOneResponse>> = _uiState


    fun fetchTopHeadlines(id : Int) {
        viewModelScope.launch {
            topHeadlineRepository.getMovieDetailsHeadlines(id)
                .catch { e ->
                    _uiState.value = UiState.Error(e.toString())
                }
                .collect {
                    _uiState.value = UiState.Success(it)
                }
        }
    }



}