package com.example.trooute.core.interceptor

import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.TimeUnit

// Help link -> https://www.linkedin.com/pulse/interceptors-okhttp-mohamad-abuzaid#:~:text=Interceptors%20are%20a%20powerful%20feature,and%20received%20by%20the%20server.
/***********************************************************************************************
 * Interceptors can also be used for caching, by intercepting network requests and responses
 * to store and retrieve data from a local cache. This can help to reduce network traffic
 * and improve performance by serving cached data instead of making new requests to the server.
 **********************************************************************************************/
class CachingInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val cacheControl = CacheControl.Builder()
            .maxAge(1, TimeUnit.DAYS) // cache for 1 day
            .build()

        val offlineCacheControl = CacheControl.Builder()
            .maxStale(7, TimeUnit.DAYS) // serve stale cache for up to 7 days
            .build()

        val response = chain.proceed(request)
        return response.newBuilder()
            .header("Cache-Control", cacheControl.toString())
            .header("Cache-Control", offlineCacheControl.toString())
            .build()
    }
}

/************************************************************************************************************************
 * In this example, we create a CachingInterceptor class that implements the Interceptor interface.
 * We pass in a cacheSize parameter in the constructor, which is the size of the cache that we want to use in bytes.
 *
 * Inside the intercept method, we first get the current request object from the chain. We then create two
 * CacheControl objects: one for online caching, which caches responses for 1 day, and one for offline caching,
 * which serves stale cache for up to 7 days.
 *
 * We then send the request to the server using chain.proceed(request) and get the response.
 *
 * Finally, we create a new response object using response.newBuilder(), set the "Cache-Control" header to the online
 * and offline cache control objects using header(), and build and return the new response object using build().
 ************************************************************************************************************************/