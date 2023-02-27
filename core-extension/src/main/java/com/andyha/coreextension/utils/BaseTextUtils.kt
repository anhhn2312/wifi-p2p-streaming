package com.andyha.coreextension.utils


object BaseTextUtils {
    const val unicode_char_a = "áàảãạăắằẳẵặâấẩẩẫậ"
    const val unicode_char_d = "đ"
    const val unicode_char_A = "ÁÀẢÃẠĂẮẰẲẴẶÂẤẦẨẪẬ"
    const val unicode_char_D = "Đ"
    const val unicode_char_e = "éèẻẽẹêếềểễệ"
    const val unicode_char_E = "ÉÈẺẼẸÊẾỀỂỄỆ"
    const val unicode_char_y = "ýỳỷỹỵ"
    const val unicode_char_Y = "ÝỲỶỸỴ"
    const val unicode_char_u = "úùủũụưứừửữự"
    const val unicode_char_U = "ÚÙỦŨỤƯỨỪỬỮỰ"
    const val unicode_char_i = "íìỉĩị"
    const val unicode_char_I = "ÍÌỈĨỊ"
    const val unicode_char_o = "óòỏõọơớờởỡợôốồổỗộ"
    const val unicode_char_O = "ÓÒỎÕỌƠỚỜỞỠỢÔỐỒỔỖỘ"

    var mapChar = hashMapOf(
        "a" to unicode_char_a, "A" to unicode_char_A,
        "d" to unicode_char_d, "D" to unicode_char_D,
        "e" to unicode_char_e, "E" to unicode_char_E,
        "y" to unicode_char_y, "Y" to unicode_char_Y,
        "u" to unicode_char_u, "U" to unicode_char_U,
        "i" to unicode_char_i, "I" to unicode_char_I,
        "o" to unicode_char_o, "O" to unicode_char_O
    )

    fun isVietnamese(content: String): Boolean {
        for (key in content) {
            for (valueOfChar in mapChar.values) {
                if (valueOfChar.contains(key)) {
                    return true
                }
            }
        }
        return false
    }

    fun convertToEnglish(content: String): String {
        var newContent = ""
        for (charAt in content) {
            var isVietNamese = false
            for (key in mapChar.keys) {
                val valueOfChar: String? = mapChar[key]
                if (valueOfChar != null) {
                    if (valueOfChar.contains(charAt)) {
                        newContent += key
                        isVietNamese = true
                        break
                    }
                }
            }
            if (!isVietNamese) {
                newContent += charAt
            }
        }
        return newContent
    }

}