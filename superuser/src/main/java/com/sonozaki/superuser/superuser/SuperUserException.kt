package com.sonozaki.superuser.superuser

import com.sonozaki.utils.UIText


class SuperUserException(message: String, val messageForLogs: UIText.StringResource): Exception(message)