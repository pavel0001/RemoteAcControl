package by.pzmandroid.mac.utils

import java.util.regex.Pattern

fun IntRange.toIntArray(): IntArray {
    if (last < first)
        return IntArray(0)

    val result = IntArray(last - first + 1)
    var index = 0
    for (element in this)
        result[index++] = element
    return result
}