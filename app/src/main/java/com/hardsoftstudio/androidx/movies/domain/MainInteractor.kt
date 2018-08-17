package com.hardsoftstudio.androidx.movies.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.*

class MainInteractor : MainActions.List, MainActions.Details {

    private val _mainState = MutableLiveData<MainState>()

    private val stateStack = Stack<MainState>()

    init {
        pushState(MainState.MoviesList(MoviesRepository.Filter.Upcoming()))
    }

    override fun getMainState(): LiveData<MainState> = _mainState

    override fun goBack(): Boolean = popState()

    override fun onFilterSelected(filter: MoviesRepository.Filter) {
        pushState(MainState.MoviesList(filter))
    }

    override fun onMovieSelected(id: Int) {
        pushState(MainState.MovieDetails(id))
    }

    private fun pushState(newState: MainState) {
        if (!stateStack.empty() && newState.javaClass == stateStack.peek().javaClass) {
            stateStack.pop()
        }
        stateStack.push(newState)
        notifyStateChange()
    }

    private fun popState(): Boolean {
        stateStack.pop()
        if (stateStack.empty()) {
            return false
        }
        notifyStateChange()
        return true
    }

    private fun notifyStateChange() {
        val newState = stateStack.peek()
        _mainState.value = newState
    }
}
