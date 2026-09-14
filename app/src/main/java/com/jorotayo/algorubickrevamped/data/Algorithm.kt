package com.jorotayo.algorubickrevamped.data

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class Algorithm(
    @Id var id: Long = 0,
    var alg_name: String = "",
    var alg: String = "",
    var alg_description: String = "",
    var algorithm_icon: String = "",
    var category: String = "",
    var practiced_correctly_int: Int = 0,
    var practiced_number_int: Int = 0,
    var custom_alg: Boolean = false,
    var favourite_alg: Boolean = false,
    var selected_alg: Boolean = false,
    var learnt: Boolean = false,
    var createdTime: Long = 0,
) {
    constructor(
        alg_name: String,
        alg: String,
        alg_description: String,
        algorithm_icon: String,
        category: String,
        practiced_correctly_int: Int,
        practiced_number_int: Int,
        custom_alg: Boolean,
        favourite_alg: Boolean,
        selected_alg: Boolean,
        learnt: Boolean,
        createdTime: Long,
    ) : this(
        id = 0,
        alg_name = alg_name,
        alg = alg,
        alg_description = alg_description,
        algorithm_icon = algorithm_icon,
        category = category,
        practiced_correctly_int = practiced_correctly_int,
        practiced_number_int = practiced_number_int,
        custom_alg = custom_alg,
        favourite_alg = favourite_alg,
        selected_alg = selected_alg,
        learnt = learnt,
        createdTime = createdTime,
    )

    fun toggleFavourite() {
        favourite_alg = !favourite_alg
    }

    fun toggleLearnt() {
        learnt = !learnt
    }

    fun setCreatedTime() {
        createdTime = System.currentTimeMillis()
    }

    class CompareAlgorithmName : Comparator<Algorithm> {
        override fun compare(a: Algorithm, b: Algorithm): Int =
            a.alg_name.compareTo(b.alg_name)
    }

    class CompareCreatedDate : Comparator<Algorithm> {
        override fun compare(a: Algorithm, b: Algorithm): Int =
            a.createdTime.compareTo(b.createdTime)
    }

    class CompareCategory : Comparator<Algorithm> {
        override fun compare(a: Algorithm, b: Algorithm): Int =
            a.category.compareTo(b.category)
    }
}
