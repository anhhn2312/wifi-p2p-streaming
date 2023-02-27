package com.andyha.coreextension.utils


object ValidationRegex {
    /**
     * PhoneNumberUtil: The ITU says the maximum length should be 15, but we have found longer numbers in Germany.
     * static final int MAX_LENGTH_FOR_NSN = 17;
     *
     * https://www.itu.int/dms_pub/itu-t/oth/02/02/T02020000B90001PDFE.pdf
     */
    const val MAX_LENGTH_FOR_NSN = 17
    // Min 7, max 15
    const val REGEX_TELEPHONE_NUMBER =
        "^(\\+)?(\\d{1,2})?[( .-]*(\\d{3})[) .-]*(\\d{3,4})[ .-]?(\\d{4})\$"

    // Minimum six characters, at least one letter and one number
    const val PASS_WORD_2 = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{6,}\$"

    // Minimum eight characters, at least one uppercase letter, one lowercase letter, one number

    const val LIMIT_SHORTEST_PASSWORD_LENGTH = 8
    const val REGEX_PASS_WORD_CONTAIN_NUMBER = "^(?=.*\\d).{1,}$"
    const val REGEX_PASS_WORD_CONTAIN_LOWER_CASE = "^(?=.*[a-z]).{1,}$"
    const val REGEX_PASS_WORD_CONTAIN_UPPER_CASE = "^(?=.*[A-Z]).{1,}$"
    const val REGEX_PASS_WORD_CONTAIN_SPECIAL_CHARACTER =
        "^(?=.*[`!@#\$%^&*()_+\\-=\\[\\]{};':\"\\|,.<>\\/\\?~]).{1,}$"

    /**
     * General Email Regex (RFC 5322 Official Standard)
     */
    const val REGEX_EMAIL =
        "(?:[a-zA-Z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-zA-Z0-9!#\$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?\\.)+[a-zA-Z0-9](?:[a-zA-Z0-9-]*[a-zA-Z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-zA-Z0-9-]*[a-zA-Z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])"
    const val VIETNAMESE_DIACRITIC_CHARACTERS =
        "ẮẰẲẴẶĂẤẦẨẪẬÂÁÀÃẢẠĐẾỀỂỄỆÊÉÈẺẼẸÍÌỈĨỊỐỒỔỖỘÔỚỜỞỠỢƠÓÒÕỎỌỨỪỬỮỰƯÚÙỦŨỤÝỲỶỸỴ"
}