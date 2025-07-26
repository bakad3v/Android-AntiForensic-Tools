package com.sonozaki.profiles.entities

import com.sonozaki.entities.ProfileDomain

data class ProfileUI(val profileDomain: ProfileDomain, val isDeletionPermitted: Boolean)