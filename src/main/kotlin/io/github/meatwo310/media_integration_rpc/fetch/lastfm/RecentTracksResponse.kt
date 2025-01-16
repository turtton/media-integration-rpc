package io.github.meatwo310.media_integration_rpc.fetch.lastfm

import io.github.meatwo310.media_integration_rpc.util.MetadataUtil
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.Duration.Companion.seconds

suspend fun HttpClient.getRecentTracks(
    user: String,
    apiKey: String,
): RecentTracksResponse =
    get("https://ws.audioscrobbler.com/2.0/?method=user.getrecenttracks&user=$user&api_key=$apiKey&format=json&limit=1")
        .body<RecentTracksResponse>()

@Serializable
data class RecentTracksResponse(
    @SerialName("recenttracks")
    val recentTracks: RecentTracks,
) {
    fun toPlayerMetadata(): MetadataUtil.PlayerMetadata? {
        val track = recentTracks.trackList.firstOrNull()?.takeIf { it.attr?.nowPlaying == "true" } ?: return null
        return MetadataUtil.PlayerMetadata(
            album = track.album.text,
            artist = track.artist.text,
            title = track.name,
            url = track.url,
            length = 0.seconds,
            artUrl = track.imageList.firstOrNull { it.size == "large" }?.url ?: "",
        )
    }
}

@Serializable
data class RecentTracks(
    @SerialName("track")
    val trackList: List<Track>,
    @SerialName("@attr")
    val attr: UserAttr
)

@Serializable
data class Track(
    val artist: MediaOrganization,
    val streamable: String,
    @SerialName("image")
    val imageList: List<Image>,
    val mbid: String,
    val album: MediaOrganization,
    val name: String,
    val url: String,
    val date: Date? = null,
    @SerialName("@attr")
    val attr: TrackAttr? = null,
)

@Serializable
data class MediaOrganization(
    val mbid: String,
    @SerialName("#text")
    val text: String,
)

@Serializable
data class Image(
    // small, medium, large, extralarge
    val size: String,
    @SerialName("#text")
    val url: String,
)

@Serializable
data class Date(
    val uts: String,
    @SerialName("#text")
    val text: String,
)

@Serializable
data class TrackAttr(
    @SerialName("nowplaying")
    val nowPlaying: String,
)

@Serializable
data class UserAttr(
    val user: String,
    val page: String,
    val perPage: String,
    val totalPages: String,
    val total: String,
)
