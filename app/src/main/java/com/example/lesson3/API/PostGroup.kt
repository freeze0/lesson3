package com.example.lesson3.API

import com.example.lesson3.data.Group;
import com.google.gson.annotations.SerializedName;
import kotlinx.serialization.Serializable;

@Serializable
class PostGroup (
    @SerializedName("action") val action: Int,
    @SerializedName("group") var group: Group
)