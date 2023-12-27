package me.amitshekhar.mvvm.ui.topheadline

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import me.amitshekhar.mvvm.R
import me.amitshekhar.mvvm.data.model.Movie
import me.amitshekhar.mvvm.databinding.TopHeadlineItemLayoutBinding

class TopHeadlineAdapter(
    private val context: Context,
    private val articleList: ArrayList<Movie>
) : RecyclerView.Adapter<TopHeadlineAdapter.DataViewHolder>() {

    class DataViewHolder(private val binding: TopHeadlineItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(article: Movie) {
            binding.textViewTitle.text = article.title
            Glide.with(binding.imageViewBanner.context)
                .load(article.mediumCoverImage)
                .apply(RequestOptions.bitmapTransform( RoundedCorners( 60)))
                .into(binding.imageViewBanner)
            if (article.rating < 4)
                binding.textViewTopLeft.background = ColorDrawable(Color.RED)
            else if (article.rating < 7  )
                binding.textViewTopLeft.background = ColorDrawable(Color.GRAY)
            else if (article.rating > 7  )
                binding.textViewTopLeft.background = ColorDrawable(Color.GREEN)

            binding.textViewTopLeft.text = article.rating.toString()

            itemView.setOnClickListener {
                val intent = Intent(binding.root.context, DetailActivity::class.java)
                intent.putExtra("ARTICLE_ID", article.id)
                binding.root.context.startActivity(intent)

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        DataViewHolder(
            TopHeadlineItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun getItemCount(): Int = articleList.size

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) =
        holder.bind(articleList[position])

    fun addData(list: List<Movie>) {
        articleList.addAll(list)
    }

}