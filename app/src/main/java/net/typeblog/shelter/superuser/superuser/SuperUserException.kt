package net.typeblog.shelter.superuser.superuser

import net.typeblog.shelter.presentation.utils.UIText

class SuperUserException(message: String, val messageForLogs: UIText.StringResource): Exception(message)