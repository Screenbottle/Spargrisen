package com.example.spargrisen

import java.util.*

/**
 * Holds the language code (i.e. "en") and the corresponding localized full language name
 * (i.e. "English")
 */
class Language(val code: String) : Comparable<Language> {

    val displayName: String
        get() = Locale(code).displayName

    override fun equals(other: Any?): Boolean {
        if (other === this) {
            return true
        }

        if (other !is Language) {
            return false
        }

        val otherLang = other as Language?
        return otherLang!!.code == code
    }

    override fun toString(): String {
        return displayName
    }

    override fun compareTo(other: Language): Int {
        return this.displayName.compareTo(other.displayName)
    }

    override fun hashCode(): Int {
        return code.hashCode()
    }
}
