package com.andyha.corenetwork.config


interface TokenRefresher {
    fun refreshToken(): String?
}