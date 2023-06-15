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

import android.content.Context
import android.content.res.Configuration
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.ViewGroup
import com.fitframe.mlkit.fitcore.app.preference.PreferenceUtils
import com.google.android.gms.common.images.Size
import java.io.IOException

/** Preview the camera image in the screen.  */
class CameraSourcePreview constructor(private val context: Context, attrs: AttributeSet?) :
    ViewGroup(
        context, attrs
    ) {
    private val surfaceView: SurfaceView
    private var startRequested: Boolean = false
    private var surfaceAvailable: Boolean = false
    private var cameraSource: CameraSource? = null
    private var overlay: GraphicOverlay? = null

    init {
        surfaceView = SurfaceView(context)
        surfaceView.getHolder().addCallback(SurfaceCallback())
        addView(surfaceView)
    }

    @Throws(IOException::class)
    private fun start(cameraSource: CameraSource) {
        this.cameraSource = cameraSource
        if (this.cameraSource != null) {
            startRequested = true
            startIfReady()
        }
    }

    @Throws(IOException::class)
    fun start(cameraSource: CameraSource, overlay: GraphicOverlay?) {
        this.overlay = overlay
        start(cameraSource)
    }

    fun stop() {
        if (cameraSource != null) {
            cameraSource!!.stop()
        }
    }

    fun release() {
        if (cameraSource != null) {
            cameraSource!!.release()
            cameraSource = null
        }
        surfaceView.getHolder().getSurface().release()
    }

    @Throws(IOException::class, SecurityException::class)
    private fun startIfReady() {
        if (startRequested && surfaceAvailable) {
            if (PreferenceUtils.isCameraLiveViewportEnabled(context)) {
                cameraSource!!.start(surfaceView.getHolder())
            } else {
                cameraSource!!.start()
            }
            requestLayout()
            if (overlay != null) {
                val size: Size? = cameraSource.getPreviewSize()
                val min: Int = Math.min(size!!.getWidth(), size.getHeight())
                val max: Int = Math.max(size.getWidth(), size.getHeight())
                val isImageFlipped: Boolean =
                    cameraSource.getCameraFacing() == CameraSource.Companion.CAMERA_FACING_FRONT
                if (isPortraitMode) {
                    // Swap width and height sizes when in portrait, since it will be rotated by 90 degrees.
                    // The camera preview and the image being processed have the same size.
                    overlay!!.setImageSourceInfo(min, max, isImageFlipped)
                } else {
                    overlay!!.setImageSourceInfo(max, min, isImageFlipped)
                }
                overlay!!.clear()
            }
            startRequested = false
        }
    }

    private inner class SurfaceCallback constructor() : SurfaceHolder.Callback {
        public override fun surfaceCreated(surface: SurfaceHolder) {
            surfaceAvailable = true
            try {
                startIfReady()
            } catch (e: IOException) {
                Log.e(TAG, "Could not start camera source.", e)
            }
        }

        public override fun surfaceDestroyed(surface: SurfaceHolder) {
            surfaceAvailable = false
        }

        public override fun surfaceChanged(
            holder: SurfaceHolder,
            format: Int,
            width: Int,
            height: Int
        ) {
        }
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        var width: Int = 320
        var height: Int = 240
        if (cameraSource != null) {
            val size: Size? = cameraSource.getPreviewSize()
            if (size != null) {
                width = size.getWidth()
                height = size.getHeight()
            }
        }

        // Swap width and height sizes when in portrait, since it will be rotated 90 degrees
        if (isPortraitMode) {
            val tmp: Int = width
            width = height
            height = tmp
        }
        val previewAspectRatio: Float = width.toFloat() / height
        val layoutWidth: Int = right - left
        val layoutHeight: Int = bottom - top
        val layoutAspectRatio: Float = layoutWidth.toFloat() / layoutHeight
        if (previewAspectRatio > layoutAspectRatio) {
            // The preview input is wider than the layout area. Fit the layout height and crop
            // the preview input horizontally while keep the center.
            val horizontalOffset: Int =
                (previewAspectRatio * layoutHeight - layoutWidth).toInt() / 2
            surfaceView.layout(-horizontalOffset, 0, layoutWidth + horizontalOffset, layoutHeight)
        } else {
            // The preview input is taller than the layout area. Fit the layout width and crop the preview
            // input vertically while keep the center.
            val verticalOffset: Int = (layoutWidth / previewAspectRatio - layoutHeight).toInt() / 2
            surfaceView.layout(0, -verticalOffset, layoutWidth, layoutHeight + verticalOffset)
        }
        try {
            startIfReady()
        } catch (e: IOException) {
            Log.e(TAG, "Could not start camera source.", e)
        }
    }

    private val isPortraitMode: Boolean
        private get() {
            val orientation: Int = context.getResources().getConfiguration().orientation
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                return false
            }
            if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                return true
            }
            Log.d(TAG, "isPortraitMode returning false by default")
            return false
        }

    companion object {
        private val TAG: String = "MIDemoApp:Preview"
    }
}