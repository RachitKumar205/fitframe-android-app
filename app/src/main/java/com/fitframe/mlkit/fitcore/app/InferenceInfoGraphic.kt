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
package com.fitframe.mlkit.fitcore.app

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.fitframe.mlkit.fitcore.app.GraphicOverlay.Graphic

/** Graphic instance for rendering inference info (latency, FPS, resolution) in an overlay view.  */
class InferenceInfoGraphic constructor(
    private val overlay: GraphicOverlay,
    private val latency: Double, // Only valid when a stream of input images is being processed. Null for single image mode.
    private val framesPerSecond: Int?
) : Graphic(overlay) {
    private val textPaint: Paint

    init {
        textPaint = Paint()
        textPaint.setColor(TEXT_COLOR)
        textPaint.setTextSize(TEXT_SIZE)
        postInvalidate()
    }

    @Synchronized
    public override fun draw(canvas: Canvas) {
        val x: Float = TEXT_SIZE * 0.5f
        val y: Float = TEXT_SIZE * 1.5f
        canvas.drawText(
            "InputImage size: " + overlay.getImageWidth() + "x" + overlay.getImageHeight(),
            x,
            y,
            textPaint
        )

        // Draw FPS (if valid) and inference latency
        if (framesPerSecond != null) {
            canvas.drawText(
                "FPS: " + framesPerSecond + ", latency: " + latency + " ms",
                x,
                y + TEXT_SIZE,
                textPaint
            )
        } else {
            canvas.drawText("Latency: " + latency + " ms", x, y + TEXT_SIZE, textPaint)
        }
    }

    companion object {
        private val TEXT_COLOR: Int = Color.WHITE
        private val TEXT_SIZE: Float = 60.0f
    }
}