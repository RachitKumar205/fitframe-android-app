package com.fitframe.mlkit.fitcore.app.java.posedetector

class PoseComparison internal constructor() {
    var numPoses = 1

    /**
     * Function for comparing the pose.
     *
     * @param angleList Specify the angles of the various body parts in the
     * following order: left hand, right hand, left elbow,
     * right elbow, left leg, right leg, left knee, right knee,
     * left ankle, right ankle.
     * @return The errors between the given angles and the most similar angle of
     * the defined poses.
     */
    fun comparePose(angleList: FloatArray): FloatArray {
        val angleErrors: Array<FloatArray>
        val referenceAngles: Array<FloatArray>
        var error: Float
        var difference: Float
        var i: Int
        var j: Int
        var classifiedPoseIndex = 0
        var totalError: Int
        var minError = 0
        angleErrors = Array(numPoses) { FloatArray(10) }
        referenceAngles = referenceeAngles
        i = 0
        while (i < referenceAngles.size) {
            totalError = 0
            j = 0
            while (j < 10) {
                difference =
                    1 - Math.abs(angleList[j] - referenceAngles[i][j]) / referenceAngles[i][j]
                error = (100 / (1 + Math.pow(
                    Math.E,
                    -Math.E * difference + Math.pow(Math.E, 2.0)
                ))).toFloat()
                angleErrors[i][j] = error
                totalError += error.toInt()
                j++
            }
            if ((totalError < minError) or (minError == 0)) {
                minError = totalError
                classifiedPoseIndex = i
            }
            i++
        }
        return angleErrors[classifiedPoseIndex]
    }

    /**
     * INCOMPLETE FUNCTION. Function for getting the angles of defined poses.
     */
    private val referenceeAngles: Array<FloatArray>
        private get() = arrayOf(
            floatArrayOf(
                90.0f, 90.0f, 175.0f, 175.0f, 108.0f,
                131.0f, 118.0f, 180.0f, 120.0f, 175.0f
            )
        )
}