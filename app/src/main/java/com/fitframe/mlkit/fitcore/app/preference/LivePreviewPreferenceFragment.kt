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

import android.hardware.Camera
import android.os.Bundle
import android.preference.ListPreference
import android.preference.Preference
import android.preference.Preference.OnPreferenceChangeListener
import android.preference.PreferenceCategory
import android.preference.PreferenceFragment
import androidx.annotation.StringRes
import com.fitframe.mlkit.fitcore.app.*
import com.fitframe.mlkit.fitcore.app.CameraSource.SizePair
import com.google.mlkit.vision.demo.R

/** Configures live preview demo settings.  */
class LivePreviewPreferenceFragment : PreferenceFragment() {
    protected var isCameraXSetting = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.preference_live_preview_quickstart)
        setUpCameraPreferences()
    }

    private fun setUpCameraPreferences() {
        val cameraPreference =
            findPreference(getString(R.string.pref_category_key_camera)) as PreferenceCategory
        if (isCameraXSetting) {
            cameraPreference.removePreference(
                findPreference(getString(R.string.pref_key_rear_camera_preview_size))
            )
            cameraPreference.removePreference(
                findPreference(getString(R.string.pref_key_front_camera_preview_size))
            )
            setUpCameraXTargetAnalysisSizePreference()
        } else {
            cameraPreference.removePreference(
                findPreference(getString(R.string.pref_key_camerax_target_resolution))
            )
            setUpCameraPreviewSizePreference(
                R.string.pref_key_rear_camera_preview_size,
                R.string.pref_key_rear_camera_picture_size,
                CameraSource.Companion.CAMERA_FACING_BACK
            )
            setUpCameraPreviewSizePreference(
                R.string.pref_key_front_camera_preview_size,
                R.string.pref_key_front_camera_picture_size,
                CameraSource.Companion.CAMERA_FACING_FRONT
            )
        }
    }

    private fun setUpCameraPreviewSizePreference(
        @StringRes previewSizePrefKeyId: Int, @StringRes pictureSizePrefKeyId: Int, cameraId: Int
    ) {
        val previewSizePreference =
            findPreference(getString(previewSizePrefKeyId)) as ListPreference
        var camera: Camera? = null
        try {
            camera = Camera.open(cameraId)
            val previewSizeList: List<SizePair> =
                CameraSource.Companion.generateValidPreviewSizeList(camera)
            val previewSizeStringValues = arrayOfNulls<String>(previewSizeList.size)
            val previewToPictureSizeStringMap: MutableMap<String, String> = HashMap()
            for (i in previewSizeList.indices) {
                val sizePair = previewSizeList[i]
                previewSizeStringValues[i] = sizePair.preview.toString()
                if (sizePair.picture != null) {
                    previewToPictureSizeStringMap[sizePair.preview.toString()] =
                        sizePair.picture.toString()
                }
            }
            previewSizePreference.entries = previewSizeStringValues
            previewSizePreference.entryValues = previewSizeStringValues
            if (previewSizePreference.entry == null) {
                // First time of opening the Settings page.
                val sizePair: SizePair = CameraSource.Companion.selectSizePair(
                    camera,
                    CameraSource.Companion.DEFAULT_REQUESTED_CAMERA_PREVIEW_WIDTH,
                    CameraSource.Companion.DEFAULT_REQUESTED_CAMERA_PREVIEW_HEIGHT
                )
                val previewSizeString = sizePair.preview.toString()
                previewSizePreference.value = previewSizeString
                previewSizePreference.summary = previewSizeString
                PreferenceUtils.saveString(
                    activity,
                    pictureSizePrefKeyId,
                    if (sizePair.picture != null) sizePair.picture.toString() else null
                )
            } else {
                previewSizePreference.summary = previewSizePreference.entry
            }
            previewSizePreference.onPreferenceChangeListener =
                OnPreferenceChangeListener { preference: Preference?, newValue: Any? ->
                    val newPreviewSizeStringValue = newValue as String?
                    previewSizePreference.summary = newPreviewSizeStringValue
                    PreferenceUtils.saveString(
                        activity,
                        pictureSizePrefKeyId,
                        previewToPictureSizeStringMap[newPreviewSizeStringValue]
                    )
                    true
                }
        } catch (e: Exception) {
            // If there's no camera for the given camera id, hide the corresponding preference.
            (findPreference(getString(R.string.pref_category_key_camera)) as PreferenceCategory)
                .removePreference(previewSizePreference)
        } finally {
            camera?.release()
        }
    }

    private fun setUpCameraXTargetAnalysisSizePreference() {
        val pref =
            findPreference(getString(R.string.pref_key_camerax_target_resolution)) as ListPreference
        val entries = arrayOf(
            "2000x2000",
            "1600x1600",
            "1200x1200",
            "1000x1000",
            "800x800",
            "600x600",
            "400x400",
            "200x200",
            "100x100"
        )
        pref.entries = entries
        pref.entryValues = entries
        pref.summary = if (pref.entry == null) "Default" else pref.entry
        pref.onPreferenceChangeListener =
            OnPreferenceChangeListener { preference: Preference?, newValue: Any? ->
                val newStringValue = newValue as String?
                pref.summary = newStringValue
                PreferenceUtils.saveString(
                    activity, R.string.pref_key_camerax_target_resolution, newStringValue
                )
                true
            }
    }

    private fun setUpListPreference(@StringRes listPreferenceKeyId: Int) {
        val listPreference = findPreference(getString(listPreferenceKeyId)) as ListPreference
        listPreference.summary = listPreference.entry
        listPreference.onPreferenceChangeListener =
            OnPreferenceChangeListener { preference: Preference?, newValue: Any? ->
                val index = listPreference.findIndexOfValue(newValue as String?)
                listPreference.summary = listPreference.entries[index]
                true
            }
    }
}