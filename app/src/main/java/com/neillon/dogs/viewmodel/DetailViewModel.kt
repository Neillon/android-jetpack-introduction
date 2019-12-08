package com.neillon.dogs.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.neillon.dogs.model.Dog
import com.neillon.dogs.model.DogDatabase
import kotlinx.coroutines.launch

class DetailViewModel(application: Application): BaseViewModel(application) {
    val dogLiveData = MutableLiveData<Dog>()

    fun fetch(uuid: Int) {
        launch {
            val dog = DogDatabase(getApplication()).dogDao().getDogsById(uuid)
            dogLiveData.value = dog
        }

    }
}