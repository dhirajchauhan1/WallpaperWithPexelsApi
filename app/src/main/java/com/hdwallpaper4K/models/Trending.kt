package com.hdwallpaper4K.models

data class Trending(
    val next_page: String,
    val page: Int,
    val per_page: Int,
    val photos: MutableList<Photo>,
    val total_results: Int
)