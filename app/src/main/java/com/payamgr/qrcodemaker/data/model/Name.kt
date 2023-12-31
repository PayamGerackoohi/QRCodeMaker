package com.payamgr.qrcodemaker.data.model

data class Name(
//    val prefix: String,
    val firstName: String,
//    val middleName: String,
    val lastName: String,
//    val postfix: String,
) {
    fun print() = buildString {
        appendLine("First Name:")
        appendLine(firstName)
        appendLine()
        appendLine("Last Name:")
        appendLine(lastName)
    }
}
