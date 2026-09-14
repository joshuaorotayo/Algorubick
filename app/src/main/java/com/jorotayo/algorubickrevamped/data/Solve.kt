package com.jorotayo.algorubickrevamped.data

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class Solve(
    @Id var id: Long = 0,
    var solve_cube_size: String = "",
    var solve_date: String = "",
    var solve_milliseconds: Int = 0,
    var solve_scramble: String = "",
    var solve_time: String = "",
) {
    constructor(
        solve_cube_size: String,
        solve_scramble: String,
        solve_time: String,
        solve_milliseconds: Int,
        solve_date: String,
    ) : this(
        id = 0,
        solve_cube_size = solve_cube_size,
        solve_date = solve_date,
        solve_milliseconds = solve_milliseconds,
        solve_scramble = solve_scramble,
        solve_time = solve_time,
    )

    fun solveDateOrEpoch(): Date {
        val formatter = SimpleDateFormat("HH:mm:ss dd-MM-yyyy", Locale.ENGLISH)
        return try {
            formatter.parse(solve_date) ?: Date(0)
        } catch (_: ParseException) {
            Date(0)
        }
    }

    class CompareSolvedDate : Comparator<Solve> {
        override fun compare(a: Solve, b: Solve): Int =
            a.solveDateOrEpoch().compareTo(b.solveDateOrEpoch())
    }
}
