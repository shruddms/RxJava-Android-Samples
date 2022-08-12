package com.kyungeun.rxjava_android_samples.ui.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.kyungeun.rxjava_android_samples.api.BaseApiService
import com.kyungeun.rxjava_android_samples.api.RetrofitClient
import com.kyungeun.rxjava_android_samples.databinding.ActivityMainBinding
import com.kyungeun.rxjava_android_samples.model.Repo
import com.kyungeun.rxjava_android_samples.ui.webview.WebViewActivity
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

open class MainActivity : AppCompatActivity(), ReposAdapter.RepoItemListener {

    private lateinit var binding: ActivityMainBinding

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private lateinit var baseApiService: BaseApiService

    private lateinit var mRepoAdapter: ReposAdapter
    private lateinit var repoList: ArrayList<Repo>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        baseApiService = RetrofitClient.create()

        mRepoAdapter = ReposAdapter(this)
        binding.rvRepos.layoutManager = LinearLayoutManager(this)
        binding.rvRepos.itemAnimator = DefaultItemAnimator()
        binding.rvRepos.setHasFixedSize(true)
        binding.rvRepos.adapter = mRepoAdapter

        binding.etUsername.setOnEditorActionListener(OnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val username: String = binding.etUsername.text.toString()
                if(username.isNotEmpty()) {
                    requestRepos(username)
                } else {
                    Toast.makeText(this@MainActivity, "Please enter user name", Toast.LENGTH_SHORT).show()
                }
                val inputMethodManager: InputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(binding.etUsername.windowToken, 0)

                return@OnEditorActionListener true
            }
            false
        })
    }

    open fun requestRepos(username: String) {
        binding.pbLoading.visibility = View.VISIBLE
        repoList = ArrayList()

        val disposable = baseApiService.requestRepos(username)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ //onNext
                for (i in it.indices) {
                    val name: String = it[i].name
                    val description: String? = it[i].description
                    val htmlUrl: String = it[i].html_url
                    repoList.add(Repo(name, description, htmlUrl))
                }
            }, { //onError
                binding.pbLoading.visibility = View.GONE
                Toast.makeText(this@MainActivity, it.message, Toast.LENGTH_SHORT).show()
            }, { //onComplete
                binding.pbLoading.visibility = View.GONE
                mRepoAdapter.setItems(repoList)
            })

        compositeDisposable.add(disposable)
    }

    //recyclerview click listener
    override fun onItemClick(item: Repo) {
        val intent = Intent(this, WebViewActivity::class.java)
        intent.putExtra("link",  item.html_url)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}