package com.example.keeps

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform