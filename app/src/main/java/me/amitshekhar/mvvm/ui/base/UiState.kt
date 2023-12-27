package me.amitshekhar.mvvm.ui.base

sealed interface UiState<out T> {

    data class Success<T>(val data: T? = null, val dataTop: T? = null) : UiState<T>

    data class SuccessTop<T>(val data: T) : UiState<T>

    data class Error(val message: String) : UiState<Nothing>

    object Loading : UiState<Nothing>

}