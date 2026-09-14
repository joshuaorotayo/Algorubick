package com.jorotayo.algorubickrevamped.data

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class Solution(
    @Id var id: Long = 0,
    var solutionCreator: String = "",
    var solutionDescription: String = "",
    var solutionIconLocation: String = "",
    var solutionName: String = "",
) {
    constructor(
        solutionName: String,
        solutionCreator: String,
        solutionDescription: String,
        solutionIconLocation: String,
    ) : this(
        id = 0,
        solutionCreator = solutionCreator,
        solutionDescription = solutionDescription,
        solutionIconLocation = solutionIconLocation,
        solutionName = solutionName,
    )
}
