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
     * If empty, loads defaults from assets/default_algs.json.
     * Icons are stored as drawable **names** (stable across builds with non-final resource IDs).
     */
    fun ensureDefaultsLoaded(context: Context) {
        if (!isEmpty()) {
            migrateStaleDrawableIds(context)
            return
        }
        try {
            val defaults = readDefaultAlgorithms(context)
            if (defaults.isNotEmpty()) {
                putAll(defaults)
            }
        } catch (_: Exception) {
            // Keep empty if asset missing or malformed
        }
    }

    /**
     * Older builds stored R.drawable integer IDs in [Algorithm.algorithm_icon].
     * Those IDs are unstable with AGP 9 / non-final resource IDs, so remap from defaults by name.
     */
    fun migrateStaleDrawableIds(context: Context) {
        val defaultsByName = try {
            readDefaultAlgorithms(context).associateBy { it.alg_name }
        } catch (_: Exception) {
            emptyMap()
        }
        if (defaultsByName.isEmpty()) return

        val toUpdate = mutableListOf<Algorithm>()
        for (alg in box.all) {
            val icon = alg.algorithm_icon
            if (icon.isEmpty()) continue
            if (icon.startsWith("file:///") || icon.startsWith("content://")) continue
            // Drawable names are not pure integers; stale entries are numeric resource IDs.
            if (icon.toIntOrNull() == null) continue
            val replacement = defaultsByName[alg.alg_name]?.algorithm_icon ?: continue
            if (replacement != icon) {
                alg.algorithm_icon = replacement
                toUpdate.add(alg)
            }
        }
        if (toUpdate.isNotEmpty()) {
            putAll(toUpdate)
        }
    }

    private fun readDefaultAlgorithms(context: Context): List<Algorithm> {
        val json = context.assets.open("default_algs.json").bufferedReader().use { it.readText() }
        val array = JSONArray(json)
        val defaults = mutableListOf<Algorithm>()
        for (i in 0 until array.length()) {
            val obj = array.getJSONObject(i)
            defaults.add(
                Algorithm(
                    alg_name = obj.getString("name"),
                    alg = obj.getString("alg"),
                    alg_description = obj.getString("description"),
                    algorithm_icon = obj.getString("image"),
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
        return defaults
    }
}
