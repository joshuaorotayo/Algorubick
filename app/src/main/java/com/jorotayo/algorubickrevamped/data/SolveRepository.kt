package com.jorotayo.algorubickrevamped.data

import com.jorotayo.algorubickrevamped.ObjectBox
import io.objectbox.Box
import io.objectbox.query.QueryBuilder

class SolveRepository(
    private val box: Box<Solve> = ObjectBox.getBoxStore().boxFor(Solve::class.java),
) {
    fun getAll(): List<Solve> = box.all

    fun getAllSortedByDate(): List<Solve> =
        box.all.sortedWith(Solve.CompareSolvedDate())

    fun getAllSortedByCubeSize(): List<Solve> =
        box.query().order(Solve_.solve_cube_size).build().find()

    fun getAllSortedByTime(): List<Solve> =
        box.query().order(Solve_.solve_milliseconds).build().find()

    fun put(solve: Solve): Long = box.put(solve)

    fun remove(solve: Solve) = box.remove(solve)

    fun removeById(id: Long) = box.remove(id)

    fun byCubeSize(size: String): List<Solve> =
        box.query()
            .equal(Solve_.solve_cube_size, size, QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .build()
            .find()
}
