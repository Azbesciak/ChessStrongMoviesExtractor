package pl.poznan.put.omw

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import okhttp3.OkHttpClient
import kotlin.concurrent.thread


fun main(args: Array<String>) = ProgramExecutor {
    val game = ChessBoardReader.getGames(inputPath)
    val mainPathMovesGame = VariantMovesFilter.filter(game)
    val client = OkHttpClient()
    val json = Json(JsonConfiguration.Stable)
    val uciServerConfig = readServerConfig(json)
    println(uciServerConfig)
    println(mainPathMovesGame)
    UciServerConnector(client, json, uciServerConfig, this).run {
        val closeConnection = connect { newGame ->
            mainPathMovesGame.forEach {
                val gameConnection = newGame()
                val player = GamePlayer(it, gameConnection)
                player.play()
                gameConnection.close()
            }
        }
        registerCloseTask { closeConnection() }
    }
}.main(args)

private fun Params.readServerConfig(json: Json) =
        ServerConfigReader(json) read uciServerConfigPath

fun registerCloseTask(task: () -> Unit) {
    Runtime.getRuntime().addShutdownHook(thread(start = false, block = task))
}
