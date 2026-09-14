package com.jorotayo.algorubickrevamped.data

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class Category(
    @Id var id: Long = 0,
    var category_name: String = "",
) {
    constructor(category_name: String) : this(id = 0, category_name = category_name)
}
