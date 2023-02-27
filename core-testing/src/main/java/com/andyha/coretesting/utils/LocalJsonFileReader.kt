package com.andyha.coretesting.utils

import com.google.gson.Gson
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.lang.reflect.Type


object LocalJsonFileReader {

    @Throws(IOException::class)
    fun <T> localJsonFileToObject(type: Class<T>, fileName: String): T {
        val resource = LocalJsonFileReader::class.java.classLoader?.getResource(fileName)
        val f = File(resource!!.path)
        val reader = BufferedReader(FileReader(f))
        val gson = Gson()
        return gson.fromJson(reader, type)
    }

    @Throws(IOException::class)
    fun <T> localJsonFileToObject(type: Type, fileName: String): T {
        val resource = LocalJsonFileReader::class.java.classLoader?.getResource(fileName)
        val f = File(resource!!.path)
        val reader = BufferedReader(FileReader(f))
        val gson = Gson()
        return gson.fromJson(reader, type)
    }

    fun localJsonToString(fileName: String): String{
        val resource = LocalJsonFileReader::class.java.classLoader?.getResource(fileName)
        val f = File(resource!!.path)
        val reader = BufferedReader(FileReader(f))
        return reader.readText()
    }
}