package com.andyha.coretesting.utils

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.HttpException
import retrofit2.Response
import java.lang.reflect.Type


class MockHelper {

    fun <T> mockObjectResponse(clazz: Class<T>, localJsonFile: String): T =
        LocalJsonFileReader.localJsonFileToObject(clazz, localJsonFile)

    fun <T> mockObjectResponse(type: Type, localJsonFile: String): T =
        LocalJsonFileReader.localJsonFileToObject(type, localJsonFile)

    fun mockBadRequestApiResponse(localJsonFile: String): Throwable{
        return HttpException(
            Response.error<String>(400,
                LocalJsonFileReader.localJsonToString(localJsonFile)
                    .toResponseBody("application/json".toMediaTypeOrNull()))
        )
    }

    fun mockUnauthorizedApiResponse(localJsonFile: String): Throwable{
        return HttpException(
            Response.error<String>(401,
                LocalJsonFileReader.localJsonToString(localJsonFile)
                    .toResponseBody("application/json".toMediaTypeOrNull()))
        )
    }

    fun mockForbiddenApiResponse(localJsonFile: String): Throwable{
        return HttpException(
            Response.error<String>(403,
                LocalJsonFileReader.localJsonToString(localJsonFile)
                    .toResponseBody("application/json".toMediaTypeOrNull()))
        )
    }

    fun mockNotFoundApiResponse(localJsonFile: String): Throwable{
        return HttpException(
            Response.error<String>(404,
                LocalJsonFileReader.localJsonToString(localJsonFile)
                    .toResponseBody("application/json".toMediaTypeOrNull()))
        )
    }

    fun mockInternalErrorApiResponse(localJsonFile: String): Throwable{
        return HttpException(
            Response.error<String>(500,
                LocalJsonFileReader.localJsonToString(localJsonFile)
                    .toResponseBody("application/json".toMediaTypeOrNull()))
        )
    }
}