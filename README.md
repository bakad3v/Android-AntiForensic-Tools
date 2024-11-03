# Android AntiForensic Tools
[Readme на русском](./README_RU.md)

**Read at least the "Installation" section before using the application!**

## Description

Android AntiForensic Tools is a free and open source application designed to protect your personal data from a powerful adversary that can put pressure on you and force you to unlock your device. It allows you to wipe device, user profiles or personal files when a duress password is entered, a USB device is connected, or the wrong password is entered multiple times. An application offers additional options to protect your device from advanced adversaries: it can uninstall itself, run [TRIM](https://en.wikipedia.org/wiki/Trim_%28computing%29) after data deletion to prevent deleted data recovery, disable logs before data deletion to leave no traces of it's actions and disable safe boot mode.
## Features and limitations
I was inspired to create this app by the [Wasted](https://github.com/x13a/Wasted) app by x13a. It allows you to factory reset the device when a duress password is entered, a USB is connected, or other triggers are triggered. This app is a big step forward for anti-forensics on Android devices, and in some situations it can be useful, but it has some limitations:
* Resetting device data is obvious to the adversary. This can make them angry and lead to unpredictable consequences for the user.
* The application has not been updated for 2 years.
* The protection provided by the application can be bypassed by booting the device into safe mode, where all user applications are disabled.
* The application destroys all user data.
* The application [does not work](https://github.com/x13a/Wasted/issues/73) on Android 14 and above. To reset the device, the [wipeData](https://developer.android.com/reference/android/app/admin/DevicePolicyManager#wipeData%28int,%20java.lang.CharSequence%29) method was used, which cannot be called from the primary user after the release of Android 14, which means that the device cannot be reset. Instead of wipeData, to perform a hard reset on newer versions of Android, you need to call the [wipeDevice](https://developer.android.com/reference/android/app/admin/DevicePolicyManager#wipeDevice%28int%29) method, but it is only available to the application with device owner rights - and Wasted only uses admin privileges.

My app is designed to address these shortcomings. It allows you to wipe all data on newer versions of Android without root privileges using Dhizuku, an app for handing over device owner rights to other apps. It allows you to prevent booting into safe mode using Dhizuku or root. Most importantly, it allows you to wipe data **relatively** stealthy. Instead of resetting the device, the application allows you to delete a single user profile or user files. Once all operations are completed, the application can delete itself.

Unfortunately, there are limits to the stealthiness of deleting data, although deleting data is still much more stealthy than resetting the device to factory defaults. After deleting data in various places in the system (logs, cache, statistics, etc.) remains a lot of evidence that this data existed. It is almost impossible to erase all these traces, and an advanced adversary with full access to the device will be able to detect them. Fortunately, some of these traces are harmless - you can make up whatever you want about a deleted Android profile. However, some traces allow to find out that you have recently deleted data using this application, that will make the adversary much more interested in extracting the truth from you. I try to fight with such traces. The app includes some additional options that will allow you to hide its existence on the device and its role in erasing data from your device. However, the study of traces of deleted data is still far from complete.

Another disadvantage of the app is that the most advanced features of the app, including the app self-destruction, require root permissions. Granting root privileges is usually accompanied by unlocking the bootloader, although some devices can [use root privileges with a locked bootloader](https://github.com/chenxiaolong/avbroot). Both root permissions and an unlocked bootloader [make](https://madaidans-insecurities.github.io/android.html) the device more vulnerable to some other types of attacks. Instead of root permissions, you can use app with device owner rights via Dhizuku, but in that case the Android system will show a notification if the app attempts to self-destruct, making the self-destruct option virtually useless when used without root permissions. Without root permissions, you can hide the app from the launcher and erase its data, plus the app masquerades as other apps, but it will still be possible to reveal the presence of the app on the device. Depending on your threat model, you will have to choose what you value more: stealthy data erasure or minimizing the privileges available to apps on your device.

## Installation
In the "Releases" section, you can download 5 versions of the app. 2 of them can be installed as regular APKs, and 3 need to be installed via ADB. The ADB installation process will be described below. Versions that can be installed as regular APKs have the drawback that the self-destruct option is not compatible with administrator privileges, and administrator privileges are required to protect against passwords brute-force. Therefore, they are called "NOT_RECOMMENDED" and their installation is not recommended.

4 versions of the app are disguised as other apps - [Island](https://github.com/oasisfeng/island) and [Shelter](https://gitea.angry.im/PeterCxy/Shelter). They have changed the package name, icon, and name. This is to ensure that after app uninstallation or self-destruction for an adversary examining the system it looks as if you uninstalled one of those apps from the device, not Android AntiForensic Tools. However, it is possible that even renaming the package will not prevent traces of the application from being detected by an advanced adversary; further research is required to verify this. These apps were chosen for cloaking because they're open source, and you're unlikely to use both apps at the same time. If you have one of these installed on your device, install a version of Android AntiForensic Tools that masquerades as the other.

Ideally, I recommend changing the package name yourself before installing. When renaming, select the package name used by an existing app that isn't on your device. The process of renaming a package will be described below.

### Apps versions
* AFTools_original_ADB_NOT_RECOMMENDED - app version with original package name. **Do not install without modifying the package name!**
* AFTools_island_NOT_RECOMMENDED - a version of the app masquerading as the Island app. Can be installed without ADB, but **installation is not recommended.**
* AFTools_shelter_NOT_RECOMMENDED - a version of the app masquerading as the Shelter app. Can be installed without ADB, but **installation is not recommended.**
* AFTools_island_ADB - a version of the app masquerading as the Island app. **Requires ADB for installation!**
* AFTools_shelter_ADB - a version of the app masquerading as the Shelter app. **Requires ADB for installation!**
### Installation via ADB
1. Download [SDK Platform tools](https://developer.android.com/tools/releases/platform-tools) on your PC
2. Unzip the archive
3. Download the version of the application that requires installation via ADB to your computer
4. On your mobile device, [unlock developer settings](https://developer.android.com/studio/debug/dev-options)
5. Open the developer settings and allow debugging via USB
6. Connect devices and allow USB debugging for your PC
7. Open the command prompt and enter the command \<path to platform-tools folder\>/adb[.exe] install -t \<path to apk file\>.
### Changing app's package name
#### Via Android Studio
1. Install [Android Studio](https://developer.android.com/studio).
2. [Clone the project](https://www.geeksforgeeks.org/how-to-clone-android-project-from-github-in-android-studio/) to yourself from github.
3. Open the File>Settings>Plugins menu and search for "Android package renamer"
4. Install the plugin. If installing the plugin is prohibited in your region, use a VPN.
5. Open the File>Rename package option and enter a new package name.
6. Go to the Build>Build Bundles/APKs>Build APKS menu and create an apk file.
#### Via Apktool
Coming soon...
## App usage
**It is recommended to test the work of the application at least once, especially the speed of data deletion along with self-destruction.**

When you launch the app, you'll need to enter your password. This password will be used to unlock the app, the same password will be used as a duress password, when you enter it, your data will be deleted. Try to make it strong and similar to the real password from your device so that you can tell that you entered the wrong password because of a typo.
### Permissions
After setting a password, you will open the settings. Here you will need to give the application certain permissions. For the triggers to work, you will need to enable the Accessibility service and grant device administrator rights. Other actions require either dhizuku privileges (device owner privileges that can be obtained via Dhizuku) or root privileges. Some actions can only be performed with root privileges.
###  Using with Dhizuku
If you are unable to grant root rights to the application, you can grant device ownership rights via [Dhizuku](https://github.com/iamr0s/Dhizuku). For now, Dhizuku from the original repository [doesn't work](https://github.com/iamr0s/Dhizuku/issues/85) until the device is unlocked for the first time. In addition, if you install the apk files posted there and give Dhizuku the rights of the owner of the device, then you will not be able to delete the application. You can download the Dhizuku apk files from my [fork](https://github.com/bakad3v/Dhizuku), where all the bugs are fixes and you can find a removable version of the app. There are also instructions for installing the application and granting it the rights of the device owner.
###  Triggers
Once the permissions are granted, you can configure the triggers. At the moment, the app supports three triggers:
* Run on duress password. The destruction of data will begin when a duress password is entered (coincides with the password of the application). Accessibility service is required.
* Prevent bruteforce. Data destruction will begin when a specified number of incorrect passwords are entered. Administrator privileges are required.
* Run on USB connection. Data destruction will begin when the USB device is connected. Charging from a computer also counts. Accessibility service is required. **Disable this option whenever you want to connect the device via USB.**
### Actions
Once you have set up triggers, you can choose what actions your app will perform.
* Run TRIM. After deleting data, a TRIM operation will be run, which will mark the blocks with deleted data as free. May make it difficult to recover deleted data, **recommended, especially if you are deleting files**. Requires root.
* Wipe data. Factory reset the device if called from the primary user, otherwise deletes user data. Any superuser (admin, dhizuku, or root) will work, but if you have Android 14 or later and want to do factory reset, then dhizuku or root permissions are required. Remember that deleting all data will be obvious to the adversary.
* Remove itself. After deleting the data, the app will delete itself. Rooting or dhizuku permissions is required, however, when used with dhizuku rights, Android devices will likely show a notification that the app has been uninstalled, making it obvious to the enemy. **It is strongly recommended to enable and use with root.**
* Clear itself. The app will clear its data and permissions after finishing deleting your data. A less secure alternative to self-destruct for use with dhizuku privileges. If used with dhizuku privileges, it is incompatible with admin privileges. Requires dhizuku or root privileges, Android 9 and above.
* Clear data. The app will clear its data after finishing deleting your data. Does not require permissions. After calling this function, the app can hide itself if the corresponding option is selected.
* Hide itself. The app will hide itself after finishing deleting all data. Another less secure alternative to self-destruct for use with dhizuku privileges. **If this option is called with dhizuku privileges, the app will be hidden only from the launcher, but it will be visible in the settings!**. If used with dhizuku privileges, it is incompatible with admin privileges. Requires dhizuku or root privileges.
* Stop logd on start. The app will stop the logd logging service before deleting data. This will prevent actions related to deleting data from being recorded in the logs. Requires root.
* Stop logd on boot. The app will stop the logd logging service when the device boots (or more precisely, when the accessibility service is enabled). Requires root. **Highly recommended to enable**
### Removing data
Finally, you can choose the data that will be deleted when triggers are activated. For now, you can select the profiles and files to delete.

**After selecting the candidates for deletion, don't forget to click on the "allow deletion" button (green triangle) at the top of the screen to activate the deletion of files or profiles.**

Deleting profiles requires dhizuku or root privileges. Multi-user mode may be disabled in your ROM - go to the "advanced settings" section to find out how to fix it.

Deleting files does not require special permissions. Also, it is not possible until the device is unlocked for the first time. If the trigger is activated before the device is unlocked for the first time, the deletion of files will begin only after unlocking, and only then can other actions, such as the self-destruction of the application, be started. **Therefore, deleting files is only recommended if you do not want or cannot grant special permissions to the application. Better keep your secret files in a separate profile and delete it!**
### Running root command
You can run custom root commands after profiles deletion and before TRIM/app selfdestruction - just enter commands in "Root command settings" screen. 
### Advanced settings
* Safe boot status. You can prohibit or allow to boot in Safe Mode. Safe Mode can be used to bypass the protection provided by the application, so **it is recommended to disable it!** On the other hand, disabled safe mode itself may raise suspicions, so a more elegant solution would be to convert the application to a system one, but its operation under such conditions has not been tested. Dhizuku or root privileges are required.
* Multiuser UI. If profiles settings are disabled on your device, you can enable them using this option. Root rights are required.
* User switcher UI. You can enable or disable the GUI to switch between users. This setting can help you in case it is hidden in your Android settings. If you're going to delete profiles, **it's recommended to always turn off the GUI to switch between users when you don't need it**, or the adversary will be able to see your profiles directly from the lock screen using the Quick Settings menu. Requires root and Android 10 or higher.
* Switch user permission. Set up for the same purpose for Android 9. It is recommended to use only if the setting above did not work. Requires root or dhizuku and Android 9 or higher.
* Maximum number of users. You can change the maximum number of profiles. Root rights are required.
## Acknowledgements
x13a, developer of the Wasted

iamr0s, developer of the Dhizuku

BinTianqi, developer of the [Owndroid](https://github.com/BinTianqi/OwnDroid) - he wrote code to work with Dhizuku in his app.

## Donate
You can support me using crypto.

XMR: 88Z5fsVK6FP4oVNjo2BrHydAEa5Y1gTPi5d7BN68sjVDZ9dTn8wPb89WmUxrxf3T37bRGSR5dekkU9aQ7j8ErWcBJ2GZojC
