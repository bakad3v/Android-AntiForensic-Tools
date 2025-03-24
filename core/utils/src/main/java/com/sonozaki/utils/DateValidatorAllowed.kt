package com.sonozaki.utils

import android.os.Parcel
import android.os.Parcelable.Creator
import com.google.android.material.datepicker.CalendarConstraints.DateValidator
import com.sonozaki.utils.TopLevelFunctions.milliSecondsToDays

/**
 * Date validator for Datepicker. Checking is the selected date in set of allowed dates.
 */
class DateValidatorAllowed(private val allowed: Set<Long>) : DateValidator {
  override fun isValid(date: Long): Boolean {
    val day = date.milliSecondsToDays()
    return day in allowed
  }

  override fun describeContents(): Int {
    return 0
  }

  override fun writeToParcel(dest: Parcel, flags: Int) {}
  override fun equals(other: Any?): Boolean {
    if (this === other) {
      return true
    }
    return other is DateValidatorAllowed
  }

  override fun hashCode(): Int {
    val hashedFields = arrayOf<Any>()
    return hashedFields.contentHashCode()
  }

  companion object {
    @JvmField
    val CREATOR: Creator<DateValidatorAllowed?> = object : Creator<DateValidatorAllowed?> {
      override fun createFromParcel(source: Parcel): DateValidatorAllowed {
        return DateValidatorAllowed(setOf())
      }

      override fun newArray(size: Int): Array<DateValidatorAllowed?> {
        return arrayOfNulls(size)
      }
    }
  }
}
