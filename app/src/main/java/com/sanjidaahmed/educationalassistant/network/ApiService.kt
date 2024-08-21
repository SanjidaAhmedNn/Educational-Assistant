package com.sanjidaahmed.educationalassistant.network

import com.sanjidaahmed.educationalassistant.model.HuggingFaceRequest
import com.sanjidaahmed.educationalassistant.model.HuggingFaceResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {
    @Headers("Authorization: Bearer YOUR_HUGGING_FACE_API_TOKEN")
    @POST("models/microsoft/DialoGPT-medium")
    fun getAnswer(@Body request: HuggingFaceRequest): Call<HuggingFaceResponse>
}

