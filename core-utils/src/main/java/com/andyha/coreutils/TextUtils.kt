package com.andyha.coreutils

import android.annotation.SuppressLint
import androidx.core.text.HtmlCompat
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*
import kotlin.math.abs


object TextUtils {

    const val ELLIPSIS_NORMAL = "\u2026"

    fun getRandomString(sizeOfRandomString: Int): String {
        val allowedCharacters = "0123456789qwertyuiopasdfghjklzxcvbnm"
        val random = Random()
        val sb = StringBuilder(sizeOfRandomString)
        for (i in 0 until sizeOfRandomString)
            sb.append(allowedCharacters[random.nextInt(allowedCharacters.length)])
        return sb.toString()
    }

    @SuppressLint("DefaultLocale")
    fun formatNumber(number: Double): String {
        return if (number % 1 == 0.0) {
            DecimalFormat("#,###").format(number)
        } else {
            DecimalFormat("#,###.##")
                .apply {
                    roundingMode = RoundingMode.CEILING
                }
                .format(number)
        }
    }

    fun formatTotalKWh(totalKWh: Double):String{
        val kWh = abs(totalKWh)
        val value = formatNumber(kWh)
        return String.format("%s kWh", value)
    }

    fun formatDistanceFromMeter(distanceInMeter: Double?, defaultValue: String? = null): String {
        if (distanceInMeter == null && defaultValue == null) {
            return ""
        }
        if (distanceInMeter == null && defaultValue != null) {
            return defaultValue
        }

        val meter = abs(distanceInMeter!!)
        val value = formatNumber(meter / 1000)
        return String.format("%s km", value)
    }

    fun formatDistanceFromMeterWithoutKm(distanceInMeter: Double?, defaultValue: String? = null): String {
        if (distanceInMeter == null && defaultValue == null) {
            return ""
        }
        if (distanceInMeter == null && defaultValue != null) {
            return defaultValue
        }

        val meter = abs(distanceInMeter!!)
        val value = formatNumber(meter / 1000)
        return String.format("%s", value)
    }

    fun formatDistanceFromKm(distanceInKilometer: Double?, defaultValue: String? = null): String {
        val value = if (distanceInKilometer != null) distanceInKilometer * 1000 else null
        return formatDistanceFromMeter(value, defaultValue)
    }

    fun formatDistanceFromKmWithoutKm(distanceInKilometer: Double?, defaultValue: String? = null): String {
        val value = if (distanceInKilometer != null) distanceInKilometer * 1000 else null
        return formatDistanceFromMeterWithoutKm(value, defaultValue)
    }

    /**
     * Convert html to text
     *
     * Example when set text with faq
     * HtmlCompat.fromHtml(item.title ?: "", HtmlCompat.FROM_HTML_MODE_LEGACY)
     * Because when convert -> exist char special & &nbsp;,...
     *
     * @param strHTML
     * @return
     */
    fun convertHtmlToText(strHTML: String?): String? {
        if (strHTML.isNullOrEmpty()) {
            return strHTML
        }

        /* //create Jsoup document from HTML
         val jsoupDoc = Jsoup.parse(strHTML)

         //set pretty print to false, so \n is not removed

         //set pretty print to false, so \n is not removed
         jsoupDoc.outputSettings(Document.OutputSettings().prettyPrint(false))

         //select all <br> tags and append \n after that

         //select all <br> tags and append \n after that
         jsoupDoc.select("br").after("\\n")

         //select all <p> tags and prepend \n before that

         //select all <p> tags and prepend \n before that
         jsoupDoc.select("p").before("\\n")

         //get the HTML from the document, and retaining original new lines

         //get the HTML from the document, and retaining original new lines
         val str = jsoupDoc.html().replace("\\\\n".toRegex(), "\n")


         val strWithNewLines = Jsoup.clean(
             str,
             "",
             Whitelist.none(),
             Document.OutputSettings().prettyPrint(false)
         )
         return HtmlCompat.fromHtml(strWithNewLines, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()*/
        return HtmlCompat.fromHtml(strHTML, HtmlCompat.FROM_HTML_MODE_LEGACY).toString()
    }

    fun getTextSizeFile(fileSizeByte: Long?): String? {
        if (fileSizeByte == null) {
            return null
        }

        val df = DecimalFormat("0.00")
        val sizeKb = 1024.0f
        val sizeMb = sizeKb * sizeKb
        val sizeGb = sizeMb * sizeKb
        // val sizeTerra = sizeGb * sizeKb

        val textSize: String? = when {
            fileSizeByte < sizeKb -> {
                df.format(fileSizeByte) + " B"
            }
            fileSizeByte < sizeMb -> {
                df.format(fileSizeByte / sizeKb) + " KB"
            }

            fileSizeByte < sizeGb -> {
                df.format(fileSizeByte / sizeMb) + " MB"
            }

            fileSizeByte >= sizeGb -> {
                df.format(fileSizeByte / sizeGb) + " GB"
            }

            else -> {
                null
            }
        }
        return textSize
    }
}