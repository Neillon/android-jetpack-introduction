package com.neillon.dogs.viewmodel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.neillon.dogs.common.utils.NotificationHelper
import com.neillon.dogs.common.utils.SharedPreferencesHelper
import com.neillon.dogs.model.Dog
import com.neillon.dogs.model.DogApiService
import com.neillon.dogs.model.DogDatabase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch

class ListViewModel(application: Application) : BaseViewModel(application) {

    private var preferencesHelper = SharedPreferencesHelper(getApplication())
    private var refreshTime = 5 * 60 * 1000 * 1000 * 1000L

    private val dogApiService = DogApiService()
    private val disposable = CompositeDisposable()

    val dogs = MutableLiveData<List<Dog>>()
    val dogsLoadError = MutableLiveData<Boolean>()
    val loading = MutableLiveData<Boolean>()

    fun refresh() {
        checkCacheDuration()
        val updateTime = preferencesHelper.getUpdateTime()
        if (updateTime != null &&
            updateTime != 0L &&
            System.nanoTime() - updateTime <= refreshTime
        ) {
            fetchFromDatabase()
        } else {
            fetchFromRemote()
        }
    }

    private fun checkCacheDuration() {
        val cachePreference = preferencesHelper.getCacheDuration()

        try {
            val cachePreferenceInt = cachePreference?.toInt() ?: 5 * 60
            refreshTime = cachePreferenceInt.times(1000 * 1000 * 1000L)
        } catch (e: Exception) {
            Log.e("CONVERT_ERROR", e.message)
            e.printStackTrace()
        }

    }

    fun refreshByPassCache() {
        fetchFromRemote()
    }

    private fun fetchFromDatabase() {
        loading.value = true
        launch {
            val dogs = DogDatabase(getApplication()).dogDao().getDogs()
            setDogsRetrived(dogs)
            Toast.makeText(getApplication(), "Dogs retrived from database", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun fetchFromRemote() {
        loading.value = true
        disposable.add(
            dogApiService.getDogs()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<List<Dog>>() {
                    override fun onSuccess(response: List<Dog>) {
                        storeDogsLocally(response)
                        preferencesHelper.saveUpdateTime(System.nanoTime())
                        Toast.makeText(
                            getApplication(),
                            "Dogs retrived from remote",
                            Toast.LENGTH_SHORT
                        ).show()
                        NotificationHelper(getApplication()).create()
                    }

                    override fun onError(error: Throwable) {
                        loading.value = false
                        dogsLoadError.value = true
                        error.printStackTrace()
                        Log.e("DOG_API", error.message)
                    }
                })
        )
    }

    private fun setDogsRetrived(dogList: List<Dog>) {
        dogs.value = dogList
        dogsLoadError.value = false
        loading.value = false
    }

    private fun storeDogsLocally(dogList: List<Dog>) {
        launch {
            val dao = DogDatabase(getApplication()).dogDao()
            dao.deleteAll()
            val result = dao.insert(*dogList.toTypedArray())

            var i = 0
            while (i < dogList.size) {
                dogList[i].UUId = result[i].toInt()
                ++i
            }
            setDogsRetrived(dogList)
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}