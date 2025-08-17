package com.sonozaki.superuser.superuser
import com.sonozaki.entities.ProfileDomain
import com.topjohnwu.superuser.Shell
import okio.BufferedSource

/**
 * Superuser interface. There are three types of superusers: device admin, device owner (provided by Dhizuku) and root. Root user is the most privileged one, and device admin is the least privileged.
 */
interface SuperUser {
    /**
     * Wipe device memory if called from main user or profiles data if called from secondary user.
     *
     * Requirements: any superuser. Calling this function from main user is restricted: device admin can't call this function on Android 14 and above.
     */
    @Throws(SuperUserException::class)
    suspend fun wipe()

    /**
     * Get list of all user profiles.
     *
     * Requirements: device owner or root. Device owner requires Android 9 and above to get profiles and can't retrieve profiles names.
     * @return list of profiles
     */
    @Throws(SuperUserException::class)
    suspend fun getProfiles(): List<ProfileDomain>

    /**
     * Remove specified user profile.
     *
     * Requirements: device owner or root.
     * @param id id of profile to remove
     */
    @Throws(SuperUserException::class)
    suspend fun removeProfile(id: Int)

    /**
     * Uninstall app with specified packageName. App leave numerous traces in android system even after uninstallation, it's recommended to clear app's data instead of deletion.
     *
     * Requirements: device owner or root. **Android can show notification if app was deleted using Dhizuku, making work of application obvious to adversary.**
     * @param packageName name of package to uninstall
     */
    @Throws(SuperUserException::class)
    suspend fun uninstallApp(packageName: String)

    /**
     * Hide app with specified packageName.
     *
     * Requirements: device owner or root. The function can work with bugs if hiding apps is done using Dhizuku.
     * @param packageName name of package to hide.
     */
    @Throws(SuperUserException::class)
    suspend fun hideApp(packageName: String)

    /**
     * Clear data of app with specified packageName.
     *
     * Requirements: device owner or root. Device owner requires Android 9 or higher.
     * @param packageName name of package to clear.
     */
    @Throws(SuperUserException::class)
    suspend fun clearAppData(packageName: String)

    /**
     * Run [TRIM](https://en.wikipedia.org/wiki/Trim_(computing)) command to mark unused blocks for clearing. Use it to reduce the likelihood of deleted data recovery.
     *
     * Requirements: root.
     */
    @Throws(SuperUserException::class)
    suspend fun runTrim()

    /**
     * Execute user-specified root commands.
     *
     * Requirements: root.
     * @return result of command
     */
    @Throws(SuperUserException::class)
    suspend fun executeRootCommand(command: String): Shell.Result

    /**
     * Stop logd service. Adversary may use logs data to get history of apps action.
     *
     * Requirements: root.
     */
    @Throws(SuperUserException::class)
    suspend fun stopLogd()

    fun getPowerButtonClicks(callback: (Boolean) -> Unit): () -> Unit

    /**
     * Enable or disable multiuser UI. May be helpful for users of custom ROMS with disabled multiuser UI.
     *
     * Requirements: root.
     * @param status enable or disable multiuser UI
     */
    @Throws(SuperUserException::class)
    suspend fun setMultiuserUI(status: Boolean)

    /**
     * Get multiuser UI status.
     *
     * Requirements: root.
     * @return is multiuser UI enabled
     */
    @Throws(SuperUserException::class)
    suspend fun getMultiuserUIStatus(): Boolean

    /**
     * Set maximum number of users on device.
     *
     * @param limit maximum number of users
     * Requirements: root.
     */
    @Throws(SuperUserException::class)
    suspend fun setUsersLimit(limit: Int)

    /**
     * Get maximum number of users on device.
     *
     * Requirements: root.
     * @return users limit or null if number was not recognised in output.
     */
    @Throws(SuperUserException::class)
    suspend fun getUserLimit(): Int?

    /**
     * Enable or disable safe boot. For unknown reason there may be problems with enabling safe boot if it was disabled.
     *
     * Adversary may use safe boot to circumvent device protection offered by the app. However, disabling safe boot may raise suspicions. Try to make Android Antiforensics Tools a system app instead.
     *
     * Requirements: device owner or root.
     * @param status enable or disable safe boot.
     */
    @Throws(SuperUserException::class)
    suspend fun setSafeBootStatus(status: Boolean)

    /**
     * Get status of safe boot.
     *
     * Requirements: device owner or root.
     * @return is safe boot enabled or disabled.
     */
    @Throws(SuperUserException::class)
    suspend fun getSafeBootStatus(): Boolean

    /**
     * Enable or disable UI for switching between users. May be helpful if this UI is disabled or, in contrast, can't be disabled in android settings. It's recommended to disable UI for switching between users every time you don't need it, because adversary may use it to get list of your profiles from lockscreen.
     *
     * Requirements: root, Android 10+.
     *  @param status enable or disable user switcher UI.
     */
    @Throws(SuperUserException::class)
    suspend fun setUserSwitcherStatus(status: Boolean)

    /**
     * Get status of UI for switching between users
     *
     * Requirements: root, Android 10+.
     * @return is UI for switching between users enabled or disabled.
     */
    @Throws(SuperUserException::class)
    suspend fun getUserSwitcherStatus(): Boolean

    /**
     * Enable or disable switching between users and corresponding UI. May be helpful if this UI is disabled or, in contrast, can't be disabled in android settings. It's recommended to disable UI for switching between users every time you don't need it, because adversary may use it to get list of your profiles from lockscreen.
     *
     * Requirements: root, Android 9+.
     * @param status allow or prohibit switching between users.
     */
    @Throws(SuperUserException::class)
    suspend fun setSwitchUserRestriction(status: Boolean)

    /**
     * Get status of switching between users
     *
     * Requirements: root, Android 9+.
     * @return is switching between users enabled or disabled.
     */
    @Throws(SuperUserException::class)
    suspend fun getSwitchUserRestriction(): Boolean

    /**
     * Reboot device
     */
    @Throws(SuperUserException::class)
    suspend fun reboot()

    /**
     * Logout specified user. Moves user to SHUTDOWN state, evicts encryption keys. Can't be called for primary user.
     */
    @Throws(SuperUserException::class)
    suspend fun stopProfile(userId: Int, isCurrent: Boolean): Boolean

    @Throws(SuperUserException::class)
    suspend fun installTestOnlyApp(length: Long, data: BufferedSource): Boolean

    @Throws(SuperUserException::class)
    suspend fun changeLogsStatus(enable: Boolean)

    @Throws(SuperUserException::class)
    suspend fun changeDeveloperSettingsStatus(unlock: Boolean)

    @Throws(SuperUserException::class)
    suspend fun getLogsStatus(): Boolean

    @Throws(SuperUserException::class)
    suspend fun getDeveloperSettingsStatus(): Boolean

    @Throws(SuperUserException::class)
    suspend fun openProfile(userId: Int)
}