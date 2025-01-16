package io.github.meatwo310.media_integration_rpc

import dev.cbyrne.kdiscordipc.KDiscordIPC
import dev.cbyrne.kdiscordipc.core.event.impl.ReadyEvent
import dev.cbyrne.kdiscordipc.data.activity.largeImage
import dev.cbyrne.kdiscordipc.data.activity.timestamps
import io.github.cdimascio.dotenv.dotenv
import io.github.meatwo310.media_integration_rpc.util.MetadataUtil
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.minutes
import kotlin.time.DurationUnit

private val logger = KotlinLogging.logger {}
val dotenv = dotenv()

val clientId = dotenv.get("CLIENT_ID")
val player = dotenv.get("PLAYER")
val allowUrlRegex = dotenv.get("ALLOW_URL_REGEX")
val largeImage = dotenv.get("LARGE_IMAGE")

suspend fun main() {
    // TODO: last.fm integration

    val ipc = KDiscordIPC(clientId)
    val scope = CoroutineScope(Dispatchers.Default)

    scope.launch {
        runPlayerctlCommand(ipc, allowUrlRegex.toRegex())
    }

    ipc.on<ReadyEvent> {
        logger.info { "Ready! (${data.user.username}#${data.user.discriminator})" }
    }

    ipc.connect()
}

suspend fun runPlayerctlCommand(ipc: KDiscordIPC, urlWhitelist: Regex) {
    var since: Long? = System.currentTimeMillis()
    while (true) {
        delay(10000)
        val meta = MetadataUtil.getMetadata(player)
        val title = meta.title
        val artist = meta.artist
        val album = meta.album
        val url = meta.url
        if (title.isEmpty() && artist.isEmpty() ||
            !urlWhitelist.matches(url) ||
            !MetadataUtil.isPlaying(player)
            ) {
            ipc.activityManager.clearActivity()
            since = null
            continue
        }
        val current = System.currentTimeMillis()
        if (since == null) {
            since = current
        }

        logger.debug { meta }
        ipc.activityManager.setActivity(title, "$artist - $album") {
            timestamps(since!!, current + 60.minutes.toLong(DurationUnit.MILLISECONDS))
            largeImage(largeImage, url)
        }
    }
}
