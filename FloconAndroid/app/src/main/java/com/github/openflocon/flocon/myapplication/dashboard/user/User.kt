package com.github.openflocon.flocon.myapplication.dashboard.user

import kotlinx.coroutines.flow.MutableStateFlow

data class User(
    val id: String,
    val userName: String,
    val fullName: String,
)

val userFlow = MutableStateFlow<User>(
    User(
        id = "1234",
        userName = "flo",
        fullName = "Florent",
    )
)
