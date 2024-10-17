package com.oasisfeng.island.superuser.superuser

import com.oasisfeng.island.presentation.utils.UIText

class SuperUserException(message: String, val messageForLogs: UIText.StringResource): Exception(message)