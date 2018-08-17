package com.hardsoftstudio.androidx.movies.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.hardsoftstudio.androidx.movies.domain.MainActions
import com.hardsoftstudio.androidx.movies.domain.MainState

class MainViewModel(private val mainActions: MainActions) : ViewModel() {

    fun getMainState(): LiveData<MainState> = mainActions.getMainState()

    fun onBackPressed() = mainActions.goBack()
}
