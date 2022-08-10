package com.kyungeun.rxjava_android_samples.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.kyungeun.recyclerview_with_mvvm.ui.drinks.ReposAdapter
import com.kyungeun.rxjava_android_samples.api.BaseApiService
import com.kyungeun.rxjava_android_samples.api.RetrofitClient
import com.kyungeun.rxjava_android_samples.databinding.ActivityMainBinding
import com.kyungeun.rxjava_android_samples.model.Repo
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers

open class MainActivity : AppCompatActivity(), ReposAdapter.RepoItemListener {

    private lateinit var binding: ActivityMainBinding

    val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private lateinit var baseApiService: BaseApiService

    private lateinit var mRepoAdapter: ReposAdapter
    private var repoList: ArrayList<Repo> = ArrayList<Repo>()

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
                requestRepos(username)
                return@OnEditorActionListener true
            }
            false
        })
    }

    open fun requestRepos(username: String) {
        Log.e("requestRepos", username)
        binding.pbLoading.visibility = View.VISIBLE


        Log.e("baseApiServiceepos", baseApiService.requestRepos(username).toString())
        val disposable = baseApiService.requestRepos(username)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ //onNext
                for (i in it.indices) {
                    val name: String = it[i].name
                    val description: String = it[i].description ?: ""

                    repoList.add(Repo(name, description))
                }
            }, { //onError
                Toast.makeText(this@MainActivity, it.message, Toast.LENGTH_SHORT).show()
            }, { //onComplete
                binding.pbLoading.visibility = View.GONE
                mRepoAdapter.setItems(repoList)
            })

        compositeDisposable.add(disposable)
    }

    //recyclerview click listener
    override fun onItemClick(position: Int) {
        Toast.makeText(this@MainActivity, "position : $position", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}