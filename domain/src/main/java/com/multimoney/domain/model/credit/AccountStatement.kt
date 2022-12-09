package com.multimoney.domain.model.credit

data class AccountStatement(
    val bytePdf: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AccountStatement

        if (!bytePdf.contentEquals(other.bytePdf)) return false

        return true
    }

    override fun hashCode(): Int {
        return bytePdf.contentHashCode()
    }
}
