package com.andyha.corenetwork

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

    @SerializedName("metadata")
    @Expose
    var metadata: Any? = null,
)