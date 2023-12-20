package com.example.lesson3.API;

import com.example.lesson3.data.Faculty;
import com.google.gson.annotations.SerializedName;
import kotlinx.serialization.Serializable;

@Serializable
class PostFaculty (
    @SerializedName("action") val action: Int,
    @SerializedName("faculty") val faculty: Faculty
)
