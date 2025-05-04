package com.sonozaki.superuser.mapper

import android.os.Parcel
import android.os.UserHandle
import android.os.Process
import com.sonozaki.entities.ProfileDomain
import javax.inject.Inject

class ProfilesMapper @Inject constructor() {

    private val currentId by lazy {
        mapUserHandleToId(Process.myUserHandle())
    }

    private fun mapUserHandleToId(userHandle: UserHandle): Int {
        val parcel = Parcel.obtain()
        userHandle.writeToParcel(parcel,0)
        parcel.setDataPosition(0)
        return parcel.readInt()
    }

    private fun isCurrentUser(id: Int) = currentId == id

    fun mapUserHandleToProfile(userHandle: UserHandle): ProfileDomain {
        val id = mapUserHandleToId(userHandle)
        val main = id == 0
        val current = isCurrentUser(id)
        return ProfileDomain(id,"Unknown name",main, current)
    }

    fun mapRootOutputToProfile(output: String) : ProfileDomain {
        val info = output.split("{")[1].split(":")
        val id = info[0].toInt()
        val name = info[1]
        val running = output.endsWith("running")
        return ProfileDomain(id = id,
            name = name,
            main = id == 0,
            current = isCurrentUser(id),
            running = running)
    }

    fun mapIdToUserHandle(id: Int): UserHandle {
        val parcel = Parcel.obtain()
        parcel.writeInt(id)
        parcel.setDataPosition(0)
        val user = UserHandle.readFromParcel(parcel)
        parcel.recycle()
        return user
    }
}