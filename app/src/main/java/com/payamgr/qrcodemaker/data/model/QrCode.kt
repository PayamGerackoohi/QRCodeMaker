package com.payamgr.qrcodemaker.data.model

data class QrCode(val size: Int, val data: BooleanArray) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as QrCode

        if (size != other.size) return false
        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = size
        result = 31 * result + data.contentHashCode()
        return result
    }
}
