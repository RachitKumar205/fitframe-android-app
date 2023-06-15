package com.fitframe.mlkit.fitcore.app.java.posedetector

class PoseAlignment internal constructor() {
    var pose_alignments: Map<String, Boolean> = HashMap()

    /**
     * Constructor for reading the defined pose alignments and storing them in a hashmap.
     */
    init {
        pose_alignments = poseAlignment
    }

    /**
     * Function reading the defined poses from a file.
     * Currently statically written, can be modified in the future.
     * @return the hashmap of the alignments for various poses.
     */
    private val poseAlignment: Map<String, Boolean>
        private get() {
            val pose_alignments: MutableMap<String, Boolean> = HashMap()
            pose_alignments["shoulder"] = true
            return pose_alignments
        }

    /**
     * Function for classifying the alignment (Front Facing or Back facing).
     *
     * @param left_shoulder The coordinate of the left shoulder
     * @param right_shoulder The coordinate of the right shoulder
     * @return True if front facing, False if back facing.
     */
    private fun classifyPoseAlignment(left_shoulder: Double, right_shoulder: Double): Boolean {
        return if (left_shoulder - right_shoulder > 0) {
            true
        } else {
            false
        }
    }

    /**
     * Function for checking if the pose is aligned as expected.
     *
     * @param left_shoulder The coordinate of the left shoulder.
     * @param right_shoulder The coordinate of the right shoulder.
     * @param pose_name The name of the pose suffixed by the x or the y coodinate being checked.
     * @return True if pose is aligned as expected False otherwise.
     * Will return true if the pose alignment is not defined.
     */
    fun validatePoseAlignment(
        left_shoulder: Double,
        right_shoulder: Double,
        pose_name: String
    ): Boolean {
        val current_pose_alignment: Boolean
        val actual_pose_alignment: Boolean
        current_pose_alignment = classifyPoseAlignment(left_shoulder, right_shoulder)
        actual_pose_alignment = try {
            pose_alignments[pose_name]!!
        } catch (e: NullPointerException) {
            println("Alignment for $pose_name is not defined")
            return true
        }
        return if (current_pose_alignment == actual_pose_alignment) {
            true
        } else {
            false
        }
    } //    public static void main(String args[]) {
    //        PoseAlignment alignment_checker = new PoseAlignment();
    //        System.out.println(alignment_checker.validatePoseAlignment(10.0, 12.0, "demo_pose_x"));
    //        System.out.println(alignment_checker.validatePoseAlignment(0.0, 0.2, "demo_pose"));
    //        System.out.println(alignment_checker.validatePoseAlignment(10.0, 8.0, "demo_pose"));
    //    }
}