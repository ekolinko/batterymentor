package com.batterymentor.debug;

import android.util.Log;

/**
 * Class used for printing debug messages to logcat.
 */
public class Debug {

    /**
     * The debug tags for logcat logs.
     */
    public static final String DEBUG_TAG = "PowerBench";

    /**
     * Flag indicating that all debug logs are enabled.
     */
    public static final boolean ALL_LOGS_ENABLED = false;

    /**
     * Flag indicating that collection manager logs are enabled.
     */
    public static final boolean COLLECTION_MANAGER_LOGS_ENABLED = false;

    /**
     * Return true if collection manager logs are enabled, false otherwise.
     *
     * @return true if collection manager logs are enabled, false otherwise.
     */
    public static boolean isCollectionManagerLoggingEnabled() {
        return ALL_LOGS_ENABLED || COLLECTION_MANAGER_LOGS_ENABLED;
    }

    /**
     * Print the specified debug message to logcat.
     *
     * @param message the message to print to logcat.
     */
    public static void printDebug(String message) {
        Log.d(DEBUG_TAG, message);
    }

    /**
     * Print the specified debug exception to logcat.
     *
     * @param exception the exception to print to logcat.
     */
    public static void printDebug(Exception exception) {
        Log.d(DEBUG_TAG, Log.getStackTraceString(exception));
    }

    /**
     * Class containing the collection manager debug logs.
     */
    public class CollectionManagerLogs {

    }
}
