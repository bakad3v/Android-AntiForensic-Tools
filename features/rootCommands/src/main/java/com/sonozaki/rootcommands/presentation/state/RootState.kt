package com.sonozaki.rootcommands.presentation.state

sealed class RootState {
    //show progress indicator
    data object Loading : RootState()
    //show dialog about absence of root rights
    data object NoRoot: RootState()
    //load root command text
    class LoadCommand(val commands: String) : RootState()
    //edit root command text
    data object EditData : RootState()
    //view root command text
    data object ViewData : RootState()
}