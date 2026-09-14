package com.jorotayo.algorubickrevamped.data

import android.content.Context
import com.jorotayo.algorubickrevamped.ObjectBox
import io.objectbox.Box
import org.json.JSONArray

class AlgorithmRepository(
    private val box: Box<Algorithm> = ObjectBox.getBoxStore().boxFor(Algorithm::class.java),
) {
    fun getAll(): List<Algorithm> = box.all

    fun get(id: Long): Algorithm? = box.get(id)

    fun put(algorithm: Algorithm): Long = box.put(algorithm)

    fun remove(algorithm: Algorithm) {
        box.remove(algorithm)
    }

    fun removeById(id: Long) {
        box.remove(id)
    }

    fun isEmpty(): Boolean = box.isEmpty

    fun putAll(algorithms: Collection<Algorithm>) {
        box.put(algorithms)
    }

    /**
     * If empty, loads defaults from assets/default_algs.json
     * (same field mapping as the old AlgorithmHomeFragment).
     */
    fun ensureDefaultsLoaded(context: Context) {
        if (!isEmpty()) return
        try {
            val json = context.assets.open("default_algs.json").bufferedReader().use { it.readText() }
            val array = JSONArray(json)
            val packageName = context.packageName
            val resources = context.resources
            val defaults = mutableListOf<Algorithm>()
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val imageRes = resources.getIdentifier(
                    obj.getString("image"),
                    "drawable",
                    packageName,
                )
                defaults.add(
                    Algorithm(
                        alg_name = obj.getString("name"),
                        alg = obj.getString("alg"),
                        alg_description = obj.getString("description"),
                        algorithm_icon = imageRes.toString(),
                        category = obj.getString("category"),
                        practiced_correctly_int = 0,
                        practiced_number_int = 0,
                        custom_alg = false,
                        favourite_alg = false,
                        selected_alg = false,
                        learnt = false,
                        createdTime = 0,
                    ),
                )
            }
            if (defaults.isNotEmpty()) {
                putAll(defaults)
            }
        } catch (_: Exception) {
            // Keep empty if asset missing or malformed
        }
    }
}
