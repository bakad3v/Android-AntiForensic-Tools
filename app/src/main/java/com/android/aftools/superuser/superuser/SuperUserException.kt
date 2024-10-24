package com.android.aftools.superuser.superuser

import com.android.aftools.presentation.utils.UIText

class SuperUserException(message: String, val messageForLogs: UIText.StringResource): Exception(message)