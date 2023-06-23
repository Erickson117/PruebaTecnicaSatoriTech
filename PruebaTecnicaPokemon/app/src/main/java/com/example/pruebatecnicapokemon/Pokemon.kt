import com.google.gson.Gson
import okhttp3.*
import java.io.IOException
import kotlin.random.Random

data class Pokemon(
    val name: String,
    val sprites: Sprites
)

data class Sprites(
    val front_default: String
)

fun getRandomPokemonData(callback: (String?, String?, Exception?) -> Unit) {
    val client = OkHttpClient()
    val randomPokemonId = Random.nextInt(1, 899)
    val url = "https://pokeapi.co/api/v2/pokemon/$randomPokemonId"
    val request = Request.Builder()
        .url(url)
        .build()

    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            callback(null, null, e)
        }

        override fun onResponse(call: Call, response: Response) {
            val responseBody = response.body?.string()
            if (response.isSuccessful && responseBody != null) {
                val pokemon = Gson().fromJson(responseBody, Pokemon::class.java)
                val name = pokemon.name
                val imageUrl = pokemon.sprites.front_default
                callback(name, imageUrl, null)
            } else {
                callback(null, null, IOException("Error: ${response.code} ${response.message}"))
            }
        }
    })
}
