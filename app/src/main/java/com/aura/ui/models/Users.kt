package com.aura.ui.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class Users {
    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("password")
    @Expose
    var password: String? = null
}

/*
data class Users (

    @SerializedName("id"       ) var id       : String? = null,
    @SerializedName("password" ) var password : String? = null

)
 */