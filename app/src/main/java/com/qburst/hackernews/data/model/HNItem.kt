package com.qburst.hackernews.data.model

typealias HNTopStories = List<Long>

data class HNItem(
    val id: Long,
    val deleted: Boolean?,
    val type: String?,
    val by: String?,
    val time: Long?,
    val text: String?,
    val dead: Boolean?,
    val parent: Long?,
    val kids: List<Long>?,
    val url: String?,
    val score: Long?,
    val title: String?,
    val descendants: Long?,
)

sealed class HNItemType {
    object Story : HNItemType()
    object Poll : HNItemType()
    object Comment : HNItemType()
    object Job : HNItemType()
}

fun HNItem.getTypeValue(): HNItemType = when (type) {
    "story" -> HNItemType.Story
    "job" -> HNItemType.Job
    "comment" -> HNItemType.Comment
    "poll" -> HNItemType.Poll
    else -> HNItemType.Story
}

