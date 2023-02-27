package com.andyha.coredata.base

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class BaseResponse<T>(

    @SerializedName("code")
    @Expose
    var responseCode: Int? = null,

    @SerializedName("message")
    @Expose
    var message: String? = null,

    @SerializedName("data")
    @Expose
    var data: T? = null,

    @SerializedName("revision")
    @Expose
    var revision: String? = null
)