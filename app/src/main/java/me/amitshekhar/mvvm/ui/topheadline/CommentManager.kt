package me.amitshekhar.mvvm.ui.topheadline

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import me.amitshekhar.mvvm.data.model.Comment

class CommentManager(private val context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("CommentsPrefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    // Метод для сохранения комментария по идентификатору фильма
    fun saveComment(movieId: Int, comment: Comment) {
        val commentsList = getComments(movieId).toMutableList()
        commentsList.add(comment)

        val json = gson.toJson(commentsList)
        sharedPreferences.edit().putString(getKey(movieId), json).apply()
    }

    fun removeComment(movieId: Int, comment: Comment) {
        val commentsList = getComments(movieId).toMutableList()
        commentsList.remove(comment)

        val json = gson.toJson(commentsList)
        sharedPreferences.edit().putString(getKey(movieId), json).apply()
    }

    // Метод для получения всех комментариев по идентификатору фильма
    fun getComments(movieId: Int): List<Comment> {
        val json = sharedPreferences.getString(getKey(movieId), null)
        return if (json != null) {
            val type = object : TypeToken<List<Comment>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }

    // Вспомогательный метод для создания уникального ключа для идентификации комментариев по идентификатору фильма
    private fun getKey(movieId: Int): String {
        return "comments_$movieId"
    }
}