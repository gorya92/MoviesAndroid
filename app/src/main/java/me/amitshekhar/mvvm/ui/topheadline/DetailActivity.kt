package me.amitshekhar.mvvm.ui.topheadline

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import kotlinx.coroutines.launch
import me.amitshekhar.mvvm.MVVMApplication
import me.amitshekhar.mvvm.R
import me.amitshekhar.mvvm.data.model.Comment
import me.amitshekhar.mvvm.databinding.ActivityMainBinding
import me.amitshekhar.mvvm.databinding.ActivityTopHeadlineBinding
import me.amitshekhar.mvvm.di.component.DaggerActivityComponent
import me.amitshekhar.mvvm.di.module.ActivityModule
import me.amitshekhar.mvvm.ui.base.UiState
import javax.inject.Inject

class DetailActivity : AppCompatActivity() {
    @Inject
    lateinit var detailCardViewModel: DetailCardViewModel

    private lateinit var binding: ActivityMainBinding




    override fun onCreate(savedInstanceState: Bundle?) {
        injectDependencies()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        val articleId = intent.getIntExtra("ARTICLE_ID", -1)
        detailCardViewModel.fetchTopHeadlines(articleId)
        setupObserver(binding.root)
        val commentManager = CommentManager(this)

        val adapter = CommentsAdapter(ArrayList())

        val firstComment = Comment("","На этот фильм никто не оставил комментарий. Вы можете быть первым!")
        val empty = ArrayList<Comment>()
        empty.add(firstComment)

        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this@DetailActivity)
        recyclerView.adapter = adapter

        val comments = commentManager.getComments(articleId)
        if (comments.isEmpty()) {
            adapter.addData(ArrayList(empty))

        } else {
            val arrayList: ArrayList<Comment> = ArrayList(comments)
            binding.recyclerView.visibility = VISIBLE

            adapter.addData(arrayList)
            adapter.notifyDataSetChanged()
        }

        adapter.onDeleteClickListener = { comment ->
            val commentManager = CommentManager(this)
            val movieId = intent.getIntExtra("ARTICLE_ID", -1)

            // Удаление конкретного комментария из SharedPreferences
            commentManager.removeComment(movieId, comment)

            // Обновление отображения списка комментариев в адаптере
            val updatedList = commentManager.getComments(movieId)
            adapter.clearData()
            val arrayList: ArrayList<Comment> = ArrayList(updatedList)
            adapter.addData(arrayList)
            val comments = commentManager.getComments(articleId)
            if (comments.isNullOrEmpty()) {
                adapter.addData(ArrayList(empty))
            } else {
                binding.recyclerView.visibility = VISIBLE
            }
            adapter.notifyDataSetChanged()


        }



        binding.buttonSubmitComment.setOnClickListener {
            if (binding.editTextComment.text.toString() != "") {
                // Получаем текст из EditText
                val commentText = binding.editTextComment.text.toString()

                // Теперь у вас есть текст комментария (commentText), который вы можете использовать
                // для сохранения в SharedPreferences или отправки на сервер и так далее.

                // Пример: Сохранение комментария в SharedPreferences
                val movieId = articleId // Замените на реальный идентификатор фильма
                val comment = Comment("user", commentText)

                commentManager.saveComment(movieId, comment)

                // Очистим EditText после отправки комментария
                binding.editTextComment.text.clear()

                val comments = commentManager.getComments(movieId)
                if (comments.isEmpty()) {
                    adapter.addData(ArrayList(empty))


                } else {
                    val arrayList: ArrayList<Comment> = ArrayList(comments)
                    binding.recyclerView.visibility = VISIBLE

                    adapter.addData(arrayList)
                    adapter.notifyDataSetChanged()
                }

                // Дополнительные действия, которые вы можете выполнить после отправки комментария
                // Например, обновление списка комментариев на экране.
            }
        }
    }



    private fun setupObserver(view: View) {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                detailCardViewModel.uiState.collect {
                    when (it) {
                        is UiState.Success -> {
                            if (it.data?.data?.movie != null) {
                                Glide.with(binding.imageViewMovie.context)
                                    .load(it.data.data.movie.largeCoverImage)
                                    .apply(RequestOptions.bitmapTransform( RoundedCorners( 60)))
                                    .into(binding.imageViewMovie)
                                if (it.data?.data?.movie.rating < 4)
                                    binding.predTitle.background = ColorDrawable(Color.RED)
                                else if (it.data?.data?.movie.rating < 7  )
                                    binding.predTitle.background = ColorDrawable(Color.GRAY)
                                else if (it.data?.data?.movie.rating > 7  )
                                    binding.predTitle.background = ColorDrawable(Color.GREEN)
                                binding.predTitle.text = it.data?.data?.movie.rating.toString()
                                binding.titleName.text = it.data.data.movie.title
                                binding.allFeature.text = " ${it.data.data.movie.year}, ${it.data.data.movie.genres}, ${it.data.data.movie.language}"
                                binding.LikesFeature.text = "Likes: ${it.data.data.movie.like_count}"
                                binding.descriptionName.text = it.data.data.movie.descriptionFull
                            }
                        }

                        is UiState.Loading -> {

                        }
                        is UiState.Error -> {

                        }
                    }
                }
            }
        }
    }
    private fun injectDependencies() {
        DaggerActivityComponent.builder()
            .applicationComponent((application as MVVMApplication).applicationComponent)
            .activityModule(ActivityModule(this)).build().inject(this)
    }
}