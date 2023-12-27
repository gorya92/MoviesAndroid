package me.amitshekhar.mvvm.ui.topheadline

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import me.amitshekhar.mvvm.R
import me.amitshekhar.mvvm.data.model.Comment
import me.amitshekhar.mvvm.data.model.Movie
import me.amitshekhar.mvvm.databinding.CommentItemBinding
import me.amitshekhar.mvvm.databinding.TopHeadlineItemLayoutBinding

class CommentsAdapter(
    private var articleList: ArrayList<Comment>,
) : RecyclerView.Adapter<CommentsAdapter.DataViewHolder>() {
    var onDeleteClickListener: ((Comment) -> Unit)? = null

    class DataViewHolder(private val binding: CommentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(article: Comment, onDeleteClickListener: (Comment) -> Unit) {
            binding.textViewComment.text = article.commentText
            binding.textViewUsername.text = article.deviceName
            if (article.commentText == "На этот фильм никто не оставил комментарий. Вы можете быть первым!")
            {
                binding.textViewUsername.visibility = GONE
                binding.buttonDelete.visibility = GONE
            }
            else{
                binding.textViewUsername.visibility = VISIBLE
                binding.buttonDelete.visibility = VISIBLE
            }
            binding.buttonDelete.setOnClickListener {
                onDeleteClickListener.invoke(article)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        DataViewHolder(
            CommentItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun getItemCount(): Int = articleList.size

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) =
        holder.bind(articleList[position], onDeleteClickListener!!)

    fun removeItem(position: Int) {
        articleList.removeAt(position)
        notifyItemRemoved(position)
    }

    // Добавляем метод для очистки списка комментариев
    fun clearData() {
        articleList.clear()
        notifyDataSetChanged()
    }

    // Добавляем метод для добавления новых комментариев
    fun addData(list: ArrayList<Comment>) {
        articleList = list
        notifyDataSetChanged()
    }

}