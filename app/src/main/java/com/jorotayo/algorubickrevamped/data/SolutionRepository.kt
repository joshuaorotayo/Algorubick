package com.jorotayo.algorubickrevamped.data

import com.jorotayo.algorubickrevamped.ObjectBox
import io.objectbox.Box
import io.objectbox.query.QueryBuilder

class SolutionRepository(
    private val solutionBox: Box<Solution> = ObjectBox.getBoxStore().boxFor(Solution::class.java),
    private val stepsBox: Box<Steps> = ObjectBox.getBoxStore().boxFor(Steps::class.java),
) {
    fun getAllSolutions(): List<Solution> = solutionBox.all

    fun getAllSortedByName(): List<Solution> =
        solutionBox.query().order(Solution_.solutionName).build().find()

    fun getAllSortedByCreator(): List<Solution> =
        solutionBox.query().order(Solution_.solutionCreator).build().find()

    fun getSolution(id: Long): Solution? = solutionBox.get(id)

    fun putSolution(solution: Solution): Long = solutionBox.put(solution)

    fun removeSolution(solution: Solution) = solutionBox.remove(solution)

    fun stepsFor(solutionName: String): List<Steps> =
        stepsBox.query()
            .equal(Steps_.solutionName, solutionName, QueryBuilder.StringOrder.CASE_INSENSITIVE)
            .build()
            .find()
            .sortedBy { it.stepNumber }

    fun putStep(step: Steps): Long = stepsBox.put(step)

    fun putSteps(steps: List<Steps>) = stepsBox.put(steps)

    fun removeSteps(steps: List<Steps>) = stepsBox.remove(steps)

    fun removeStepsFor(solutionName: String) {
        removeSteps(stepsFor(solutionName))
    }
}
