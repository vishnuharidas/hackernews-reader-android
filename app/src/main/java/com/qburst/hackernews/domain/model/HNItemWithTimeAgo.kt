package com.qburst.hackernews.domain.model

import com.qburst.hackernews.data.model.HNItem

// Preferring composition over inheritance
data class HNItemWithTimeAgo(
    val item: HNItem,
    val timeAgo: String
)