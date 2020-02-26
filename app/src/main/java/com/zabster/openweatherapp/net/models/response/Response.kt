package com.zabster.openweatherapp.net.models.response

import com.zabster.openweatherapp.net.models.errors.ErrorModel

interface Response<T> {
    fun getBody(): T?
    fun getError(): ErrorModel?
}