package com.fitframe.mlkit.fitcore.app.java.posedetector

import com.google.mlkit.vision.pose.PoseLandmark

//Smoothing coords and calculating angles
class SmoothAngles internal constructor() {
    private val windowSize = 4
    private val smoothingMethod = "mean"
    private fun averageCoords(): List<Float> {
        // Please call this out if averaging could be done more efficiently :)
        // I'm a noob in Java.
        var currentCoords: List<Float>
        val averagedCoords: MutableList<Float> = ArrayList()
        for (k in CoordsArray.coordsArray[0].indices) {
            averagedCoords.add(0f)
        }
        for (i in CoordsArray.coordsArray.indices) {
            currentCoords = CoordsArray.coordsArray[i]
            for (j in currentCoords.indices) {
                averagedCoords[j] = averagedCoords[j] + currentCoords[j]
            }
        }
        for (i in averagedCoords.indices) {
            averagedCoords[i] = averagedCoords[i] / CoordsArray.coordsArray.size
        }
        if (CoordsArray.coordsArray.size == windowSize) {
            CoordsArray.pop()
        }
        CoordsArray.push(averagedCoords)
        return averagedCoords
    }

    fun smoothenCoords(pose_coords: List<Float>?): List<Float> {
        if (CoordsArray.coordsArray.size == windowSize) {
            CoordsArray.pop()
        }
        var smoothCoords: List<Float> = ArrayList()
        if (pose_coords != null) {
            CoordsArray.push(pose_coords)
        }
        if (smoothingMethod === "mean") {
            smoothCoords = averageCoords()
        }
        return smoothCoords
    }

    companion object {
        fun getAngle(
            firstPoint: PoseLandmark,
            midPoint: PoseLandmark,
            lastPoint: PoseLandmark
        ): Float {
            var result = Math.toDegrees(
                Math.atan2(
                    (lastPoint.position.y - midPoint.position.y).toDouble(),
                    (
                            lastPoint.position.x - midPoint.position.x).toDouble()
                )
                        - Math.atan2(
                    (firstPoint.position.y - midPoint.position.y).toDouble(),
                    (
                            firstPoint.position.x - midPoint.position.x).toDouble()
                )
            ).toFloat()
            result = Math.abs(result) // Angle should never be negative
            if (result > 180) {
                result =
                    (360.0 - result).toFloat() // Always get the acute representation of the angle
            }
            return result
        }
    }
}