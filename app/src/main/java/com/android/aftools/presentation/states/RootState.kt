package com.android.aftools.presentation.states

sealed class RootState: ClassWithProgressBar {
    //show progress indicator
    data object Loading : RootState(), ShowProgressBar
    //show dialog about absence of root rights
    data object NoRoot: RootState()
    //load root command text
    class LoadCommand(val commands: String) : RootState()
    //edit root command text
    data object EditData : RootState()
    //view root command text
    data object ViewData : RootState()
}