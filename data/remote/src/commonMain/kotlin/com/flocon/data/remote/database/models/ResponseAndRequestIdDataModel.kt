package com.flocon.data.remote.database.models

data class ResponseAndRequestIdDataModel(
    val requestId: String,
    val response: DatabaseExecuteSqlResponseDataModel,
)
