package com.example.myapplication

import kotlinx.serialization.Serializable

/**
 * Movie class represents video entity with title, description, image thumbs and video url.
 */
@Serializable
data class Movie(
    var id: Long = 0,
    var title: String? = null,
    var description: String? = null,
    var backgroundImageUrl: String? = null,
    var cardImageUrl: String? = null,
    var videoUrl: String? = null,
    var studio: String? = null
) : java.io.Serializable {

    override fun toString(): String {
        return "Movie{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", videoUrl='" + videoUrl + '\'' +
                ", backgroundImageUrl='" + backgroundImageUrl + '\'' +
                ", cardImageUrl='" + cardImageUrl + '\'' +
                '}'
    }

    companion object {
        private const val serialVersionUID = 727566175075960653L
    }
}