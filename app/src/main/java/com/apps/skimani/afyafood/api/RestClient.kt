package com.apps.skimani.afyafood.api

import com.apps.skimani.afyafood.BuildConfig
import com.apps.skimani.afyafood.utils.Constants
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit

object RestClient {
    /**
     * Request timeout in Secs
     */
    const val REQUEST_TIMEOUT = 60

    /**
     * SSL Certificate pinning
     * to this specific endpoint to eliminate man in the middle attack
     */
    var certificatePinner = CertificatePinner.Builder()
        .add(Constants.PINNER_URL, Constants.PINNER_CERT)
        .build()
    var spec: ConnectionSpec = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
        .tlsVersions(TlsVersion.TLS_1_3, TlsVersion.TLS_1_2)
        .cipherSuites(
            CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
            CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
            CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256,
            CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA,
            CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256
        )
        .build()

    /**
     * Setup OkTttpClient with Logger interceptor, Timeout, Header Incerceptors
     */
    val httpClient = OkHttpClient().newBuilder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level =
                if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        })
        .connectTimeout(REQUEST_TIMEOUT.toLong(), TimeUnit.SECONDS)
        .readTimeout(REQUEST_TIMEOUT.toLong(), TimeUnit.SECONDS)
        .writeTimeout(REQUEST_TIMEOUT.toLong(), TimeUnit.SECONDS)
        .addInterceptor { chain: Interceptor.Chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .addHeader("version-name", "1.0.0")
                .addHeader("x-app-id", Constants.APP_ID)
                .addHeader("x-app-key", Constants.APP_KEY)
            val request = requestBuilder.build()
            Timber.i("Request HEADERS $request.toString()")
            Timber.i("HEADERS  $request.toString()")
            chain.proceed(request)
        }
        .certificatePinner(certificatePinner)
        .connectionSpecs(listOf(spec))
        .build()

    /**
     * Set up Retrofit with Response converters
     */
    private val mRetrofit = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()

    /**
     * Init the retrofit webservice
     */
    val apiService = mRetrofit.create(AfyaApiService::class.java)
}
