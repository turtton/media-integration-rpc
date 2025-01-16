package io.github.meatwo310.media_integration_rpc.util

import evalBash
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class MetadataUtil {
    private val logger = KotlinLogging.logger {}

    data class PlayerMetadata(
        val album: String,
        val artist: String,
        val title: String,
        val url: String,
        val length: Duration,
        val artUrl: String,
    )

    companion object {
        private const val DELIMITER = " ::: "

        private fun getRawMetadata(player: String, vararg whats: String): Map<String, String> {
            if (whats.isEmpty()) return emptyMap()
            val format = "{{${whats.joinToString("}}$DELIMITER{{")}}}"
            val output = evalBash("playerctl metadata -p $player -f \"$format\"").getOrDefault("")
            //    logger.info { "output: $output" }
            val values = output.split(DELIMITER)
            return whats.zip(values).toMap()
        }

        private const val ALBUM_KEY = "xesam:album"
        private const val ARTIST_KEY = "xesam:artist"
        private const val TITLE_KEY = "xesam:title"
        private const val URL_KEY = "xesam:url"
        private const val LENGTH_KEY = "mpris:length"
        private const val ART_URL_KEY = "mpris:artUrl"

        fun getMetadata(player: String): PlayerMetadata {
            val output = getRawMetadata(player, ALBUM_KEY, ARTIST_KEY, TITLE_KEY, URL_KEY, LENGTH_KEY, ART_URL_KEY)
            return PlayerMetadata(
                album = output.getOrDefault(ALBUM_KEY, ""),
                artist = output.getOrDefault(ARTIST_KEY, ""),
                title = output.getOrDefault(TITLE_KEY, ""),
                url = output.getOrDefault(URL_KEY, ""),
                length = try {
                    output.getOrDefault(LENGTH_KEY, "0").toLong()
                } catch (_: Exception) {
                    0L
                }.toDuration(DurationUnit.MICROSECONDS),
                artUrl = output.getOrDefault(ART_URL_KEY, ""),
            )
        }

        fun isPlaying(player: String): Boolean {
            return evalBash("playerctl status -p $player").getOrDefault("").trim() == "Playing"
        }
    }
}
