package com.jorotayo.algorubickrevamped.data

import com.jorotayo.algorubickrevamped.ObjectBox
import io.objectbox.Box

class CategoryRepository(
    private val box: Box<Category> = ObjectBox.getBoxStore().boxFor(Category::class.java),
) {
    fun getAll(): List<Category> = box.all

    fun put(category: Category): Long = box.put(category)

    fun remove(category: Category) = box.remove(category)

    fun seedDefaultsIfEmpty(names: List<String>) {
        if (box.isEmpty) {
            names.forEach { put(Category(it)) }
        }
    }
}
