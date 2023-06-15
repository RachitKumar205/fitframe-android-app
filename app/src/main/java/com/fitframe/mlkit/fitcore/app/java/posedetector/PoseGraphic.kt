/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fitframe.mlkit.fitcore.app.java.posedetector

import android.content.res.Resources
import android.graphics.*
import com.fitframe.mlkit.fitcore.app.GraphicOverlay
import com.fitframe.mlkit.fitcore.app.GraphicOverlay.Graphic
import com.google.mlkit.vision.pose.Pose
import com.google.mlkit.vision.pose.PoseLandmark
import java.text.DecimalFormat

/**
 * Draw the detected pose in preview.
 */
class PoseGraphic internal constructor(
    overlay: GraphicOverlay?,
    private val pose: Pose,
    showInFrameLikelihood: Boolean
) : Graphic(overlay) {
    private var showInFrameLikelihood = true
    private val dynamicPaintThreshold = 6.5f
    private var angleError = 0f
    private val linePaint: Paint
    private val linePaintGreen: Paint
    private val dynamicLinePaint: Paint
    private val dotPaint: Paint
    private val dotPaintGreen: Paint
    private val dotPaintRed: Paint
    private val textPaint: Paint

    init {
        dotPaint = Paint()
        dotPaintGreen = Paint()
        dotPaintRed = Paint()
        dotPaintGreen.color = Color.GREEN
        dotPaintRed.color = Color.RED
        dotPaintGreen.style = Paint.Style.FILL
        dotPaintRed.style = Paint.Style.FILL
        dotPaint.color = Color.WHITE
        dotPaint.textSize = IN_FRAME_LIKELIHOOD_TEXT_SIZE
        linePaint = Paint()
        linePaint.strokeWidth = 17f
        linePaint.color = Color.CYAN
        linePaintGreen = Paint()
        linePaintGreen.strokeWidth = 17f
        linePaintGreen.color = Color.GREEN
        dynamicLinePaint = Paint()
        dynamicLinePaint.strokeWidth = 17.5f
        textPaint = Paint()
        textPaint.color = Color.GREEN
        textPaint.textSize = 50f
    }

    private fun getRed(value: Float): Int {
        return ((1 - value) * 255).toInt()
        //        return 0;
    }

    private fun getGreen(value: Float): Int {
        return (value * 255).toInt()
        //        return 0;
    }

    override fun draw(canvas: Canvas) {
        val landmarks = pose.allPoseLandmarks
        if (landmarks.isEmpty()) {
            return
        }

        // Getting all the landmarks
        val nose = pose.getPoseLandmark(PoseLandmark.NOSE)
        val leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER)
        val rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER)
        val leftElbow = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW)
        val rightElbow = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW)
        val leftWrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST)
        val rightWrist = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST)
        val leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP)
        val rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP)
        val leftKnee = pose.getPoseLandmark(PoseLandmark.LEFT_KNEE)
        val rightKnee = pose.getPoseLandmark(PoseLandmark.RIGHT_KNEE)
        val leftAnkle = pose.getPoseLandmark(PoseLandmark.LEFT_ANKLE)
        val rightAnkle = pose.getPoseLandmark(PoseLandmark.RIGHT_ANKLE)
        val leftPinky = pose.getPoseLandmark(PoseLandmark.LEFT_PINKY)
        val rightPinky = pose.getPoseLandmark(PoseLandmark.RIGHT_PINKY)
        val leftIndex = pose.getPoseLandmark(PoseLandmark.LEFT_INDEX)
        val rightIndex = pose.getPoseLandmark(PoseLandmark.RIGHT_INDEX)
        val leftThumb = pose.getPoseLandmark(PoseLandmark.LEFT_THUMB)
        val rightThumb = pose.getPoseLandmark(PoseLandmark.RIGHT_THUMB)
        val leftHeel = pose.getPoseLandmark(PoseLandmark.LEFT_HEEL)
        val rightHeel = pose.getPoseLandmark(PoseLandmark.RIGHT_HEEL)
        val leftFootIndex = pose.getPoseLandmark(PoseLandmark.LEFT_FOOT_INDEX)
        val rightFootIndex = pose.getPoseLandmark(PoseLandmark.RIGHT_FOOT_INDEX)


        // Frame Visibility Check
        showInFrameLikelihood = true
        if (nose.inFrameLikelihood < threshold) {
            showInFrameLikelihood = false
        }
        if (leftShoulder.inFrameLikelihood < threshold) {
            showInFrameLikelihood = false
        }
        if (rightShoulder.inFrameLikelihood < threshold) {
            showInFrameLikelihood = false
        }
        if (leftElbow.inFrameLikelihood < threshold) {
            showInFrameLikelihood = false
        }
        if (rightElbow.inFrameLikelihood < threshold) {
            showInFrameLikelihood = false
        }
        if (leftWrist.inFrameLikelihood < threshold) {
            showInFrameLikelihood = false
        }
        if (rightWrist.inFrameLikelihood < threshold) {
            showInFrameLikelihood = false
        }
        if (leftHip.inFrameLikelihood < threshold) {
            showInFrameLikelihood = false
        }
        if (rightHip.inFrameLikelihood < threshold) {
            showInFrameLikelihood = false
        }
        if (leftKnee.inFrameLikelihood < threshold) {
            showInFrameLikelihood = false
        }
        if (rightKnee.inFrameLikelihood < threshold) {
            showInFrameLikelihood = false
        }
        if (leftAnkle.inFrameLikelihood < threshold) {
            showInFrameLikelihood = false
        }
        if (rightAnkle.inFrameLikelihood < threshold) {
            showInFrameLikelihood = false
        }
        if (showInFrameLikelihood == true) {
            val poseAlignment = PoseAlignment()
            var shoulder_alignment = true
            shoulder_alignment = poseAlignment.validatePoseAlignment(
                leftShoulder.position.x.toDouble(),
                rightShoulder.position.x.toDouble(),
                "shoulder"
            )
            if (shoulder_alignment) {
                //Angles
                val smoothAngles = SmoothAngles()
                val leftShoulder_angle: Float =
                    SmoothAngles.getAngle(leftElbow, leftShoulder, leftHip)
                val rightShoulder_angle: Float =
                    SmoothAngles.getAngle(rightElbow, rightShoulder, rightHip)
                val leftElbow_angle: Float =
                    SmoothAngles.getAngle(leftWrist, leftElbow, leftShoulder)
                val rightElbow_angle: Float =
                    SmoothAngles.getAngle(rightWrist, rightElbow, rightShoulder)
                val leftHip_angle: Float =
                    SmoothAngles.getAngle(leftKnee, leftHip, leftShoulder)
                val rightHip_angle: Float =
                    SmoothAngles.getAngle(rightKnee, rightHip, rightShoulder)
                val leftKnee_angle: Float =
                    SmoothAngles.getAngle(leftAnkle, leftKnee, leftHip)
                val rightKnee_angle: Float =
                    SmoothAngles.getAngle(rightAnkle, rightKnee, rightHip)
                val leftAnkle_angle: Float =
                    SmoothAngles.getAngle(leftFootIndex, leftAnkle, leftKnee)
                val rightAnkle_angle: Float =
                    SmoothAngles.getAngle(rightFootIndex, rightAnkle, rightKnee)
                val angleArray = FloatArray(10)
                angleArray[0] = leftShoulder_angle
                angleArray[1] = rightShoulder_angle
                angleArray[2] = leftElbow_angle
                angleArray[3] = rightElbow_angle
                angleArray[4] = leftHip_angle
                angleArray[5] = rightHip_angle
                angleArray[6] = leftKnee_angle
                angleArray[7] = rightKnee_angle
                angleArray[8] = leftAnkle_angle
                angleArray[9] = rightAnkle_angle
                var angleErrors = FloatArray(10)
                val poseCompare = PoseComparison()
                angleErrors = poseCompare.comparePose(angleArray)


                //Creating array for smoothing
                val inputArray: MutableList<Float> = ArrayList()
                inputArray.add(leftShoulder.position.x)
                inputArray.add(leftShoulder.position.y)
                inputArray.add(rightShoulder.position.x)
                inputArray.add(rightShoulder.position.y)
                inputArray.add(leftElbow.position.x)
                inputArray.add(leftElbow.position.y)
                inputArray.add(rightElbow.position.x)
                inputArray.add(rightElbow.position.y)
                inputArray.add(leftWrist.position.x)
                inputArray.add(leftWrist.position.y)
                inputArray.add(rightWrist.position.x)
                inputArray.add(rightWrist.position.y)
                inputArray.add(leftHip.position.x)
                inputArray.add(leftHip.position.y)
                inputArray.add(rightHip.position.x)
                inputArray.add(rightHip.position.y)
                inputArray.add(leftKnee.position.x)
                inputArray.add(leftKnee.position.y)
                inputArray.add(rightKnee.position.x)
                inputArray.add(rightKnee.position.y)
                inputArray.add(leftAnkle.position.x)
                inputArray.add(leftAnkle.position.y)
                inputArray.add(rightAnkle.position.x)
                inputArray.add(rightAnkle.position.y)
                inputArray.add(leftPinky.position.x)
                inputArray.add(leftPinky.position.y)
                inputArray.add(rightPinky.position.x)
                inputArray.add(rightPinky.position.y)
                inputArray.add(leftIndex.position.x)
                inputArray.add(leftIndex.position.y)
                inputArray.add(rightIndex.position.x)
                inputArray.add(rightIndex.position.y)
                inputArray.add(leftThumb.position.x)
                inputArray.add(leftThumb.position.y)
                inputArray.add(rightThumb.position.x)
                inputArray.add(rightThumb.position.y)
                inputArray.add(leftHeel.position.x)
                inputArray.add(leftHeel.position.y)
                inputArray.add(rightHeel.position.x)
                inputArray.add(rightHeel.position.y)
                inputArray.add(leftFootIndex.position.x)
                inputArray.add(leftFootIndex.position.y)
                inputArray.add(rightFootIndex.position.x)
                inputArray.add(rightFootIndex.position.y)
                inputArray.add(nose.position.x)
                inputArray.add(nose.position.y)

                //Calling the smoothing function
                CoordsArray.currentCoords = smoothAngles.smoothenCoords(inputArray)
                val new_leftShoulder =
                    PointF(CoordsArray.currentCoords[0], CoordsArray.currentCoords[1])
                val new_rightShoulder =
                    PointF(CoordsArray.currentCoords[2], CoordsArray.currentCoords[3])
                val new_shoulderMid = PointF(
                    (CoordsArray.currentCoords[0] + CoordsArray.currentCoords[2]) / 2,
                    (CoordsArray.currentCoords[1] + CoordsArray.currentCoords[3]) / 2
                )
                val new_leftElbow =
                    PointF(CoordsArray.currentCoords[4], CoordsArray.currentCoords[5])
                val new_rightElbow =
                    PointF(CoordsArray.currentCoords[6], CoordsArray.currentCoords[7])
                val new_leftWrist =
                    PointF(CoordsArray.currentCoords[8], CoordsArray.currentCoords[9])
                val new_rightWrist =
                    PointF(CoordsArray.currentCoords[10], CoordsArray.currentCoords[11])
                val new_leftHip =
                    PointF(CoordsArray.currentCoords[12], CoordsArray.currentCoords[13])
                val new_rightHip =
                    PointF(CoordsArray.currentCoords[14], CoordsArray.currentCoords[15])
                val new_leftKnee =
                    PointF(CoordsArray.currentCoords[16], CoordsArray.currentCoords[17])
                val new_rightKnee =
                    PointF(CoordsArray.currentCoords[18], CoordsArray.currentCoords[19])
                val new_leftAnkle =
                    PointF(CoordsArray.currentCoords[20], CoordsArray.currentCoords[21])
                val new_rightAnkle =
                    PointF(CoordsArray.currentCoords[22], CoordsArray.currentCoords[23])
                val new_leftPinky =
                    PointF(CoordsArray.currentCoords[24], CoordsArray.currentCoords[25])
                val new_rightPinky =
                    PointF(CoordsArray.currentCoords[26], CoordsArray.currentCoords[27])
                val new_leftIndex =
                    PointF(CoordsArray.currentCoords[28], CoordsArray.currentCoords[29])
                val new_rightIndex =
                    PointF(CoordsArray.currentCoords[30], CoordsArray.currentCoords[31])
                val new_leftThumb =
                    PointF(CoordsArray.currentCoords[32], CoordsArray.currentCoords[33])
                val new_rightThumb =
                    PointF(CoordsArray.currentCoords[34], CoordsArray.currentCoords[35])
                val new_leftHeel =
                    PointF(CoordsArray.currentCoords[36], CoordsArray.currentCoords[37])
                val new_rightHeel =
                    PointF(CoordsArray.currentCoords[38], CoordsArray.currentCoords[39])
                val new_leftFootIndex =
                    PointF(CoordsArray.currentCoords[40], CoordsArray.currentCoords[41])
                val new_rightFootIndex =
                    PointF(CoordsArray.currentCoords[42], CoordsArray.currentCoords[43])
                val new_nose = PointF(CoordsArray.currentCoords[44], CoordsArray.currentCoords[45])


                //Displaying angles
                drawAngle(canvas, leftShoulder_angle, new_leftShoulder, textPaint)
                drawAngle(canvas, rightShoulder_angle, new_rightShoulder, textPaint)
                drawAngle(canvas, leftElbow_angle, new_leftElbow, textPaint)
                drawAngle(canvas, rightElbow_angle, new_rightElbow, textPaint)
                drawAngle(canvas, leftHip_angle, new_leftHip, textPaint)
                drawAngle(canvas, rightHip_angle, new_rightHip, textPaint)
                drawAngle(canvas, leftKnee_angle, new_leftKnee, textPaint)
                drawAngle(canvas, rightKnee_angle, new_rightKnee, textPaint)
                drawAngle(canvas, leftAnkle_angle, new_leftAnkle, textPaint)
                drawAngle(canvas, rightAnkle_angle, new_rightAnkle, textPaint)


                //Drawing joint points
                //Right Shoulder
                if (rightShoulder_angle>90){
                    drawPoint(canvas, new_rightShoulder, dotPaintGreen)
                }
                else if (rightShoulder_angle<=90){
                    drawPoint(canvas, new_rightShoulder, dotPaintRed)
                }

                //Left Shoulder
                if (leftShoulder_angle > 90) {
                    drawPoint(canvas, new_leftShoulder, dotPaintGreen)
                }
                else if (leftShoulder_angle <= 90){
                    drawPoint(canvas, new_leftShoulder, dotPaintRed)
                }

                //Left Elbow
                if (leftElbow_angle > 90){
                    drawPoint(canvas, new_leftElbow, dotPaintGreen)
                }
                else if (leftElbow_angle <= 90){
                    drawPoint(canvas, new_leftElbow, dotPaintRed)
                }

                //Right Elbow
                if (rightElbow_angle > 90){
                    drawPoint(canvas, new_rightElbow, dotPaintGreen)
                }
                else if (rightElbow_angle <= 90){
                    drawPoint(canvas, new_rightElbow, dotPaintRed)
                }

                //Right Hip
                if (rightHip_angle > 90){
                    drawPoint(canvas, new_rightHip, dotPaintGreen)
                }
                else if (rightHip_angle <= 90){
                    drawPoint(canvas, new_rightHip, dotPaintRed)
                }

                //Left Hip
                if (leftHip_angle > 90){
                    drawPoint(canvas, new_leftHip, dotPaintRed)
                }
                else if (leftHip_angle <= 90){
                    drawPoint(canvas, new_leftHip, dotPaintGreen)
                }

                //Left Knee
                if (leftKnee_angle > 90){
                    drawPoint(canvas, new_leftKnee, dotPaintRed)
                }
                else if (leftKnee_angle <= 90){
                    drawPoint(canvas, new_leftKnee, dotPaintGreen)
                }

                //Right Knee
                if (rightKnee_angle > 90){
                    drawPoint(canvas, new_rightKnee, dotPaintRed)
                }
                else if (leftKnee_angle <= 90){
                    drawPoint(canvas, new_rightKnee, dotPaintGreen)
                }


                //Static keypoints
                drawPoint(canvas, new_leftWrist, dotPaint)
                drawPoint(canvas, new_rightWrist, dotPaint)
                drawPoint(canvas, new_leftAnkle, dotPaint)
                drawPoint(canvas, new_rightAnkle, dotPaint)


                //Drawing lines
                for (i in 0..9) {
                    angleError += angleErrors[i]
                }
                if (angleError > dynamicPaintThreshold) {
                    dynamicLinePaint.color =
                        Color.rgb(getRed(angleErrors[0]), getGreen(angleErrors[0]), 0)
                    drawLine(canvas, new_leftShoulder, new_leftElbow, dynamicLinePaint)
                    dynamicLinePaint.color =
                        Color.rgb(getRed(angleErrors[1]), getGreen(angleErrors[1]), 0)
                    drawLine(canvas, new_rightShoulder, new_rightElbow, dynamicLinePaint)
                    dynamicLinePaint.color =
                        Color.rgb(getRed(angleErrors[2]), getGreen(angleErrors[2]), 0)
                    drawLine(canvas, new_leftElbow, new_leftWrist, dynamicLinePaint)
                    dynamicLinePaint.color =
                        Color.rgb(getRed(angleErrors[3]), getGreen(angleErrors[3]), 0)
                    drawLine(canvas, new_rightElbow, new_rightWrist, dynamicLinePaint)
                    dynamicLinePaint.color =
                        Color.rgb(getRed(angleErrors[4]), getGreen(angleErrors[4]), 0)
                    drawLine(canvas, new_leftHip, new_leftKnee, dynamicLinePaint)
                    dynamicLinePaint.color =
                        Color.rgb(getRed(angleErrors[5]), getGreen(angleErrors[5]), 0)
                    drawLine(canvas, new_rightHip, new_rightKnee, dynamicLinePaint)
                    dynamicLinePaint.color =
                        Color.rgb(getRed(angleErrors[6]), getGreen(angleErrors[6]), 0)
                    drawLine(canvas, new_leftKnee, new_leftAnkle, dynamicLinePaint)
                    dynamicLinePaint.color =
                        Color.rgb(getRed(angleErrors[7]), getGreen(angleErrors[7]), 0)
                    drawLine(canvas, new_rightKnee, new_rightAnkle, dynamicLinePaint)
                    dynamicLinePaint.color =
                        Color.rgb(getRed(angleErrors[8]), getGreen(angleErrors[8]), 0)
                    drawLine(canvas, new_leftAnkle, new_leftFootIndex, dynamicLinePaint)
                    dynamicLinePaint.color =
                        Color.rgb(getRed(angleErrors[9]), getGreen(angleErrors[9]), 0)
                    drawLine(canvas, new_leftShoulder, new_rightShoulder, linePaintGreen)
                    drawLine(canvas, new_leftHip, new_rightHip, linePaintGreen)
                    //Left body
                    drawLine(canvas, new_leftShoulder, new_leftHip, linePaintGreen)

                    //Right body
                    drawLine(canvas, new_rightShoulder, new_rightHip, linePaintGreen)
                } else {
                    drawLine(canvas, new_leftShoulder, new_leftElbow, linePaint)
                    drawLine(canvas, new_rightShoulder, new_rightElbow, linePaint)
                    drawLine(canvas, new_leftElbow, new_leftWrist, linePaint)
                    drawLine(canvas, new_rightElbow, new_rightWrist, linePaint)
                    drawLine(canvas, new_leftHip, new_leftKnee, linePaint)
                    drawLine(canvas, new_rightHip, new_rightKnee, linePaint)
                    drawLine(canvas, new_leftKnee, new_leftAnkle, linePaint)
                    drawLine(canvas, new_rightKnee, new_rightAnkle, linePaint)
                    drawLine(canvas, new_leftShoulder, new_rightShoulder, linePaint)
                    drawLine(canvas, new_leftHip, new_rightHip, linePaint)
                    //Left body
                    drawLine(canvas, new_leftShoulder, new_leftHip, linePaint)

                    //Right body
                    drawLine(canvas, new_rightShoulder, new_rightHip, linePaint)

                }
            } else {
                val smoothAngles = SmoothAngles()
                val leftShoulder_angle: Float =
                    SmoothAngles.getAngle(leftElbow, leftShoulder, leftHip)
                val rightShoulder_angle: Float =
                    SmoothAngles.getAngle(rightElbow, rightShoulder, rightHip)
                val leftElbow_angle: Float =
                    SmoothAngles.getAngle(leftWrist, leftElbow, leftShoulder)
                val rightElbow_angle: Float =
                    SmoothAngles.getAngle(rightWrist, rightElbow, rightShoulder)
                val leftHip_angle: Float =
                    SmoothAngles.getAngle(leftKnee, leftHip, leftShoulder)
                val rightHip_angle: Float =
                    SmoothAngles.getAngle(rightKnee, rightHip, rightShoulder)
                val leftKnee_angle: Float =
                    SmoothAngles.getAngle(leftAnkle, leftKnee, leftHip)
                val rightKnee_angle: Float =
                    SmoothAngles.getAngle(rightAnkle, rightKnee, rightHip)
                val leftAnkle_angle: Float =
                    SmoothAngles.getAngle(leftFootIndex, leftAnkle, leftKnee)
                val rightAnkle_angle: Float =
                    SmoothAngles.getAngle(rightFootIndex, rightAnkle, rightKnee)
                val angleArray = FloatArray(10)
                angleArray[0] = leftShoulder_angle
                angleArray[1] = rightShoulder_angle
                angleArray[2] = leftElbow_angle
                angleArray[3] = rightElbow_angle
                angleArray[4] = leftHip_angle
                angleArray[5] = rightHip_angle
                angleArray[6] = leftKnee_angle
                angleArray[7] = rightKnee_angle
                angleArray[8] = leftAnkle_angle
                angleArray[9] = rightAnkle_angle
                var angleErrors = FloatArray(10)
                val poseCompare = PoseComparison()
                angleErrors = poseCompare.comparePose(angleArray)
                val new_leftShoulder =
                    PointF(CoordsArray.currentCoords[0], CoordsArray.currentCoords[1])
                val new_rightShoulder =
                    PointF(CoordsArray.currentCoords[2], CoordsArray.currentCoords[3])
                val new_shoulderMid = PointF(
                    (CoordsArray.currentCoords[0] + CoordsArray.currentCoords[2]) / 2,
                    (CoordsArray.currentCoords[1] + CoordsArray.currentCoords[3]) / 2
                )
                val new_leftElbow =
                    PointF(CoordsArray.currentCoords[4], CoordsArray.currentCoords[5])
                val new_rightElbow =
                    PointF(CoordsArray.currentCoords[6], CoordsArray.currentCoords[7])
                val new_leftWrist =
                    PointF(CoordsArray.currentCoords[8], CoordsArray.currentCoords[9])
                val new_rightWrist =
                    PointF(CoordsArray.currentCoords[10], CoordsArray.currentCoords[11])
                val new_leftHip =
                    PointF(CoordsArray.currentCoords[12], CoordsArray.currentCoords[13])
                val new_rightHip =
                    PointF(CoordsArray.currentCoords[14], CoordsArray.currentCoords[15])
                val new_leftKnee =
                    PointF(CoordsArray.currentCoords[16], CoordsArray.currentCoords[17])
                val new_rightKnee =
                    PointF(CoordsArray.currentCoords[18], CoordsArray.currentCoords[19])
                val new_leftAnkle =
                    PointF(CoordsArray.currentCoords[20], CoordsArray.currentCoords[21])
                val new_rightAnkle =
                    PointF(CoordsArray.currentCoords[22], CoordsArray.currentCoords[23])
                val new_leftPinky =
                    PointF(CoordsArray.currentCoords[24], CoordsArray.currentCoords[25])
                val new_rightPinky =
                    PointF(CoordsArray.currentCoords[26], CoordsArray.currentCoords[27])
                val new_leftIndex =
                    PointF(CoordsArray.currentCoords[28], CoordsArray.currentCoords[29])
                val new_rightIndex =
                    PointF(CoordsArray.currentCoords[30], CoordsArray.currentCoords[31])
                val new_leftThumb =
                    PointF(CoordsArray.currentCoords[32], CoordsArray.currentCoords[33])
                val new_rightThumb =
                    PointF(CoordsArray.currentCoords[34], CoordsArray.currentCoords[35])
                val new_leftHeel =
                    PointF(CoordsArray.currentCoords[36], CoordsArray.currentCoords[37])
                val new_rightHeel =
                    PointF(CoordsArray.currentCoords[38], CoordsArray.currentCoords[39])
                val new_leftFootIndex =
                    PointF(CoordsArray.currentCoords[40], CoordsArray.currentCoords[41])
                val new_rightFootIndex =
                    PointF(CoordsArray.currentCoords[42], CoordsArray.currentCoords[43])
                val new_nose = PointF(CoordsArray.currentCoords[44], CoordsArray.currentCoords[45])

                //Displaying angles
                drawAngle(canvas, leftShoulder_angle, new_leftShoulder, textPaint)
                drawAngle(canvas, rightShoulder_angle, new_rightShoulder, textPaint)
                drawAngle(canvas, leftElbow_angle, new_leftElbow, textPaint)
                drawAngle(canvas, rightElbow_angle, new_rightElbow, textPaint)
                drawAngle(canvas, leftHip_angle, new_leftHip, textPaint)
                drawAngle(canvas, rightHip_angle, new_rightHip, textPaint)
                drawAngle(canvas, leftKnee_angle, new_leftKnee, textPaint)
                drawAngle(canvas, rightKnee_angle, new_rightKnee, textPaint)
                drawAngle(canvas, leftAnkle_angle, new_leftAnkle, textPaint)
                drawAngle(canvas, rightAnkle_angle, new_rightAnkle, textPaint)

                //Drawing Face points
                drawPoint(canvas, new_nose, dotPaint)

                //Drawing joint points
                drawPoint(canvas, new_leftShoulder, dotPaint)
                drawPoint(canvas, new_leftElbow, dotPaint)
                drawPoint(canvas, new_rightElbow, dotPaint)
                drawPoint(canvas, new_leftWrist, dotPaint)
                drawPoint(canvas, new_rightWrist, dotPaint)
                drawPoint(canvas, new_leftThumb, dotPaint)
                drawPoint(canvas, new_leftPinky, dotPaint)
                drawPoint(canvas, new_leftIndex, dotPaint)
                drawPoint(canvas, new_rightThumb, dotPaint)
                drawPoint(canvas, new_rightPinky, dotPaint)
                drawPoint(canvas, new_rightIndex, dotPaint)
                drawPoint(canvas, new_leftHip, dotPaint)
                drawPoint(canvas, new_rightHip, dotPaint)
                drawPoint(canvas, new_leftKnee, dotPaint)
                drawPoint(canvas, new_rightKnee, dotPaint)
                drawPoint(canvas, new_leftAnkle, dotPaint)
                drawPoint(canvas, new_rightAnkle, dotPaint)
                drawPoint(canvas, new_leftHeel, dotPaint)
                drawPoint(canvas, new_rightHeel, dotPaint)
                drawPoint(canvas, new_rightFootIndex, dotPaint)
                drawPoint(canvas, new_leftFootIndex, dotPaint)

                if (angleArray[1]>90){
                    dotPaint.color = Color.GREEN
                    drawPoint(canvas, new_rightShoulder, dotPaint)
                }

                else if (angleArray[1]<=90){
                    dotPaint.color = Color.RED
                    drawPoint(canvas, new_rightShoulder, dotPaint)
                }

                //Drawing lines
                for (i in 0..9) {
                    angleError += angleErrors[i]
                }

                if (angleError > dynamicPaintThreshold) {
                    dynamicLinePaint.color =
                        Color.rgb(getRed(angleErrors[0]), getGreen(angleErrors[0]), 0)
                    drawLine(canvas, new_leftShoulder, new_leftElbow, dynamicLinePaint)
                    dynamicLinePaint.color =
                        Color.rgb(getRed(angleErrors[1]), getGreen(angleErrors[1]), 0)
                    drawLine(canvas, new_rightShoulder, new_rightElbow, dynamicLinePaint)
                    dynamicLinePaint.color =
                        Color.rgb(getRed(angleErrors[2]), getGreen(angleErrors[2]), 0)
                    drawLine(canvas, new_leftElbow, new_leftWrist, dynamicLinePaint)
                    dynamicLinePaint.color =
                        Color.rgb(getRed(angleErrors[3]), getGreen(angleErrors[3]), 0)
                    drawLine(canvas, new_rightElbow, new_rightWrist, dynamicLinePaint)
                    dynamicLinePaint.color =
                        Color.rgb(getRed(angleErrors[4]), getGreen(angleErrors[4]), 0)
                    drawLine(canvas, new_leftHip, new_leftKnee, dynamicLinePaint)
                    dynamicLinePaint.color =
                        Color.rgb(getRed(angleErrors[5]), getGreen(angleErrors[5]), 0)
                    drawLine(canvas, new_rightHip, new_rightKnee, dynamicLinePaint)
                    dynamicLinePaint.color =
                        Color.rgb(getRed(angleErrors[6]), getGreen(angleErrors[6]), 0)
                    drawLine(canvas, new_leftKnee, new_leftAnkle, dynamicLinePaint)
                    dynamicLinePaint.color =
                        Color.rgb(getRed(angleErrors[7]), getGreen(angleErrors[7]), 0)
                    drawLine(canvas, new_rightKnee, new_rightAnkle, dynamicLinePaint)
                    dynamicLinePaint.color =
                        Color.rgb(getRed(angleErrors[8]), getGreen(angleErrors[8]), 0)
                    drawLine(canvas, new_leftAnkle, new_leftFootIndex, dynamicLinePaint)
                    dynamicLinePaint.color =
                        Color.rgb(getRed(angleErrors[9]), getGreen(angleErrors[9]), 0)
                    drawLine(canvas, new_rightAnkle, new_rightFootIndex, dynamicLinePaint)
                    drawLine(canvas, new_leftShoulder, new_rightShoulder, linePaintGreen)
                    drawLine(canvas, new_leftHip, new_rightHip, linePaintGreen)
                    drawLine(canvas, new_shoulderMid, new_nose, linePaintGreen)
                    //Left body
                    drawLine(canvas, new_leftShoulder, new_leftHip, linePaintGreen)
                    drawLine(canvas, new_leftWrist, new_leftThumb, linePaintGreen)
                    drawLine(canvas, new_leftWrist, new_leftPinky, linePaintGreen)
                    drawLine(canvas, new_leftWrist, new_leftIndex, linePaintGreen)
                    drawLine(canvas, new_leftAnkle, new_leftHeel, linePaintGreen)
                    drawLine(canvas, new_leftHeel, new_leftFootIndex, linePaintGreen)

                    //Right body
                    drawLine(canvas, new_rightShoulder, new_rightHip, linePaintGreen)
                    drawLine(canvas, new_rightWrist, new_rightThumb, linePaintGreen)
                    drawLine(canvas, new_rightWrist, new_rightPinky, linePaintGreen)
                    drawLine(canvas, new_rightWrist, new_rightIndex, linePaintGreen)
                    drawLine(canvas, new_rightAnkle, new_rightHeel, linePaintGreen)
                    drawLine(canvas, new_rightHeel, new_rightFootIndex, linePaintGreen)
                } else {
                    drawLine(canvas, new_leftShoulder, new_leftElbow, linePaint)
                    drawLine(canvas, new_rightShoulder, new_rightElbow, linePaint)
                    drawLine(canvas, new_leftElbow, new_leftWrist, linePaint)
                    drawLine(canvas, new_rightElbow, new_rightWrist, linePaint)
                    drawLine(canvas, new_leftHip, new_leftKnee, linePaint)
                    drawLine(canvas, new_rightHip, new_rightKnee, linePaint)
                    drawLine(canvas, new_leftKnee, new_leftAnkle, linePaint)
                    drawLine(canvas, new_rightKnee, new_rightAnkle, linePaint)
                    drawLine(canvas, new_leftAnkle, new_leftFootIndex, linePaint)
                    drawLine(canvas, new_rightAnkle, new_rightFootIndex, linePaint)
                    drawLine(canvas, new_leftShoulder, new_rightShoulder, linePaint)
                    drawLine(canvas, new_leftHip, new_rightHip, linePaint)
                    drawLine(canvas, new_shoulderMid, new_nose, linePaint)
                    //Left body
                    drawLine(canvas, new_leftShoulder, new_leftHip, linePaint)
                    drawLine(canvas, new_leftWrist, new_leftThumb, linePaint)
                    drawLine(canvas, new_leftWrist, new_leftPinky, linePaint)
                    drawLine(canvas, new_leftWrist, new_leftIndex, linePaint)
                    drawLine(canvas, new_leftAnkle, new_leftHeel, linePaint)
                    drawLine(canvas, new_leftHeel, new_leftFootIndex, linePaint)

                    //Right body
                    drawLine(canvas, new_rightShoulder, new_rightHip, linePaint)
                    drawLine(canvas, new_rightWrist, new_rightThumb, linePaint)
                    drawLine(canvas, new_rightWrist, new_rightPinky, linePaint)
                    drawLine(canvas, new_rightWrist, new_rightIndex, linePaint)
                    drawLine(canvas, new_rightAnkle, new_rightHeel, linePaint)
                    drawLine(canvas, new_rightHeel, new_rightFootIndex, linePaint)
                }
            }
        }
    }

    fun drawAngle(canvas: Canvas, angle: Float, point: PointF?, paint: Paint?) {
        var angle = angle
        if (point == null) {
            return
        }
        val df = DecimalFormat("#.00")
        angle = java.lang.Float.valueOf(df.format(angle.toDouble()))
        canvas.drawText(angle.toString(), translateX(point.x), translateY(point.y), paint!!)
    }

    fun drawPoint(canvas: Canvas, point: PointF?, paint: Paint?) {
        if (point == null) {
            return
        }
        canvas.drawCircle(translateX(point.x), translateY(point.y), 30f, paint!!)
    }

    fun drawLine(canvas: Canvas, start: PointF?, end: PointF?, paint: Paint?) {
        if (start == null || end == null) {
            return
        }
        canvas.drawLine(
            translateX(start.x), translateY(start.y), translateX(end.x), translateY(end.y), paint!!
        )
    }

    companion object {
        private const val DOT_RADIUS = 15f
        private const val IN_FRAME_LIKELIHOOD_TEXT_SIZE = 30.0f
        private const val threshold = 0f
        val screenWidth: Int
            get() = Resources.getSystem().displayMetrics.widthPixels
        val screenHeight: Int
            get() = Resources.getSystem().displayMetrics.heightPixels
    }
}