package com.jorotayo.algorubickrevamped.data

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class Steps(
    @Id var id: Long = 0,
    var solutionName: String = "",
    var stepAlgorithm: String = "",
    var stepDescription: String = "",
    var stepImageEnd: String = "",
    var stepImageStart: String = "",
    var stepName: String = "",
    var stepNumber: Int = 0,
) : Comparable<Steps> {
    constructor(
        solutionName: String,
        stepNumber: Int,
        stepName: String,
        stepDescription: String,
        stepAlgorithm: String,
        stepImageStart: String,
        stepImageEnd: String,
    ) : this(
        id = 0,
        solutionName = solutionName,
        stepAlgorithm = stepAlgorithm,
        stepDescription = stepDescription,
        stepImageEnd = stepImageEnd,
        stepImageStart = stepImageStart,
        stepName = stepName,
        stepNumber = stepNumber,
    )

    override fun compareTo(other: Steps): Int = stepNumber - other.stepNumber
}
