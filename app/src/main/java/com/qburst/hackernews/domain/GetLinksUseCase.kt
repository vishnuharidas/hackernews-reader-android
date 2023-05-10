package com.qburst.hackernews.domain

class GetLinksUseCase {

    operator fun invoke(input: String?): List<String> {

        input ?: return emptyList()

        val pattern = "href=\"(.*?)\""
        val regex = Regex(pattern)
        val matches = regex.findAll(input)

        val links = mutableListOf<String>()
        for (match in matches) {

            println(match.groupValues)

            val link = match.groupValues[1]
            val unescapedLink = link.replace("&#x2F;", "/").replace("&#x3D;", "=")
            links.add(unescapedLink)
        }

        println(links)
        return links
    }

}

internal fun main(){

    // Sample from: https://hacker-news.firebaseio.com/v0/item/35885069.json?print=pretty
    val text ="This brings back memories JS and Flash.<p>[1] <a href=\"https:&#x2F;&#x2F;news.ycombinator.com&#x2F;item?id=2556118\" rel=\"nofollow\">https:&#x2F;&#x2F;news.ycombinator.com&#x2F;item?id=2556118</a>\n[2] <a href=\"https:&#x2F;&#x2F;github.com&#x2F;EmielM&#x2F;spotifyontheweb-crypt\">https:&#x2F;&#x2F;github.com&#x2F;EmielM&#x2F;spotifyontheweb-crypt</a>"
    val useCase = GetLinksUseCase()

    println(useCase(text))

}