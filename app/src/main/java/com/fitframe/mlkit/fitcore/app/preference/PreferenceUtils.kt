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
package com.fitframe.mlkit.fitcore.app.preference

import android.content.Context
import android.os.Build.VERSION_CODES
import android.preference.PreferenceManager
import android.util.Size
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import com.fitframe.mlkit.fitcore.app.*
import com.fitframe.mlkit.fitcore.app.CameraSource.SizePair
import com.google.common.base.Preconditions
import com.google.mlkit.vision.demo.R
import com.google.mlkit.vision.pose.PoseDetectorOptionsBase
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions

/** Utility class to retrieve shared preferences.  */
object PreferenceUtils {
    private const val POSE_DETECTOR_PERFORMANCE_MODE_FAST = 1
    fun saveString(context: Context, @StringRes prefKeyId: Int, value: String?) {
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putString(context.getString(prefKeyId), value)
            .apply()
    }

    fun getCameraPreviewSizePair(context: Context, cameraId: Int): SizePair? {
        Preconditions.checkArgument(
            cameraId == CameraSource.Companion.CAMERA_FACING_BACK
                    || cameraId == CameraSource.Companion.CAMERA_FACING_FRONT
        )
        val previewSizePrefKey: String
        val pictureSizePrefKey: String
        if (cameraId == CameraSource.Companion.CAMERA_FACING_BACK) {
            previewSizePrefKey = context.getString(R.string.pref_key_rear_camera_preview_size)
            pictureSizePrefKey = context.getString(R.string.pref_key_rear_camera_picture_size)
        } else {
            previewSizePrefKey = context.getString(R.string.pref_key_front_camera_preview_size)
            pictureSizePrefKey = context.getString(R.string.pref_key_front_camera_picture_size)
        }
        return try {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            SizePair(
                com.google.android.gms.common.images.Size.parseSize(
                    sharedPreferences.getString(
                        previewSizePrefKey,
                        null
                    )
                ),
                com.google.android.gms.common.images.Size.parseSize(
                    sharedPreferences.getString(
                        pictureSizePrefKey,
                        null
                    )
                )
            )
        } catch (e: Exception) {
            null
        }
    }

    @RequiresApi(VERSION_CODES.LOLLIPOP)
    fun getCameraXTargetResolution(context: Context): Size? {
        val prefKey = context.getString(R.string.pref_key_camerax_target_resolution)
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return try {
            Size.parseSize(sharedPreferences.getString(prefKey, null))
        } catch (e: Exception) {
            null
        }
    }

    fun getPoseDetectorOptionsForLivePreview(context: Context): PoseDetectorOptionsBase {
        val performanceMode = getModeTypePreferenceValue(
            context,
            R.string.pref_key_live_preview_pose_detection_performance_mode,
            POSE_DETECTOR_PERFORMANCE_MODE_FAST
        )
        return if (performanceMode == POSE_DETECTOR_PERFORMANCE_MODE_FAST) {
            PoseDetectorOptions.Builder()
                .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
                .build()
        } else {
            AccuratePoseDetectorOptions.Builder()
                .setDetectorMode(AccuratePoseDetectorOptions.STREAM_MODE)
                .build()
        }
    }

    fun getPoseDetectorOptionsForStillImage(context: Context): PoseDetectorOptionsBase {
        val performanceMode = getModeTypePreferenceValue(
            context,
            R.string.pref_key_still_image_pose_detection_performance_mode,
            POSE_DETECTOR_PERFORMANCE_MODE_FAST
        )
        return if (performanceMode == POSE_DETECTOR_PERFORMANCE_MODE_FAST) {
            PoseDetectorOptions.Builder()
                .setDetectorMode(PoseDetectorOptions.SINGLE_IMAGE_MODE)
                .build()
        } else {
            AccuratePoseDetectorOptions.Builder()
                .setDetectorMode(AccuratePoseDetectorOptions.SINGLE_IMAGE_MODE)
                .build()
        }
    }

    fun shouldShowPoseDetectionInFrameLikelihoodLivePreview(context: Context): Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val prefKey =
            context.getString(R.string.pref_key_live_preview_pose_detector_show_in_frame_likelihood)
        return sharedPreferences.getBoolean(prefKey, false)
    }

    fun shouldShowPoseDetectionInFrameLikelihoodStillImage(context: Context): Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val prefKey =
            context.getString(R.string.pref_key_still_image_pose_detector_show_in_frame_likelihood)
        return sharedPreferences.getBoolean(prefKey, false)
    }

    /**
     * Mode type preference is backed by [android.preference.ListPreference] which only support
     * storing its entry value as string type, so we need to retrieve as string and then convert to
     * integer.
     */
    private fun getModeTypePreferenceValue(
        context: Context, @StringRes prefKeyResId: Int, defaultValue: Int
    ): Int {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val prefKey = context.getString(prefKeyResId)
        return sharedPreferences.getString(prefKey, defaultValue.toString())!!.toInt()
    }

    fun isCameraLiveViewportEnabled(context: Context): Boolean {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val prefKey = context.getString(R.string.pref_key_camera_live_viewport)
        return sharedPreferences.getBoolean(prefKey, false)
    }
}