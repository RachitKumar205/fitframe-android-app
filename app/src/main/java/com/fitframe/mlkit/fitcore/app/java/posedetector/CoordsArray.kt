package com.fitframe.mlkit.fitcore.app.java.posedetector

object CoordsArray {
    var coordsArray: MutableList<List<Float>> = ArrayList()
    fun push(inArray: List<Float>) {
        coordsArray.add(inArray)
    }

    fun pop() {
        coordsArray.removeAt(0)
    }

    var currentCoords: List<Float> = ArrayList()
}