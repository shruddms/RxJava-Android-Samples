package com.kyungeun.rxjava_android_samples.api

import com.kyungeun.rxjava_android_samples.model.Repo
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.GET
import retrofit2.http.Path

interface BaseApiService {
    @GET("users/{username}/repos")
    fun requestRepos(@Path("username") username: String): Observable<List<Repo>>
}