/*
 * © 2021 Infosys Limited, Bangalore, India. All Rights Reserved.
 * Version:1.0.0.0
 *
 * Except for any free or open source software components
 * embedded in this Infosys proprietary software program ("Live Enterprise Employee Experience Interaction Suite"),
 * this Program is protected by copyright laws, international treaties
 * and other pending or existing intellectual property rights in India,
 * the United States and other countries. Except as expressly permitted,
 * any unauthorized reproduction, storage, transmission in any form or
 * by any means (including without limitation electronic, mechanical,
 * printing, photocopying, recording or otherwise), or any distribution
 * of this Program, or any portion of it, may result in severe civil and criminal
 * penalties, and will be prosecuted to the maximum extent possible under the law.
 */

package com.live.clientme.android.auth

import android.app.Activity
import android.content.Context
import androidx.annotation.WorkerThread
import com.microsoft.identity.client.*
import com.microsoft.identity.client.exception.MsalClientException
import com.microsoft.identity.client.exception.MsalException
import com.microsoft.identity.common.java.exception.ErrorStrings

internal class MsalAuthManager {

    private var mSingleAccountApp: ISingleAccountPublicClientApplication? = null
    private val scopes = arrayOf("api://55776430-4ced-48fe-8215-20687ca9511f/Data.Read")
    internal var callback: ((AuthResponse?) -> Unit)? = null
    private var mAccount: IAccount? = null

    @WorkerThread
    internal fun initMsalClient(context: Context) {
        if (mSingleAccountApp == null) {
            mSingleAccountApp = PublicClientApplication.createSingleAccountPublicClientApplication(
                context, R.raw.msal_config
            )
            mAccount = getCurrentAccount()
        }
    }

    internal fun getToken(activity: Activity) {
        initMsalClient(activity)
        if (mSingleAccountApp != null) {
            val account: IAccount? = getCurrentAccount()
            if (account == null) {
                val signInParameters = SignInParameters.builder()
                    .withActivity(activity)
                    .withScopes(scopes.toList())
                    .withCallback(getSignInCallback())
                    .build()
                mSingleAccountApp?.signIn(signInParameters)
            } else {
                val response = acquireTokenSilently(account)
                sendResponse(response.token, response.error)
            }
        } else {
            sendResponse(null, AuthErrorCodes.UNKNOWN)
        }
    }

    internal fun getSilentToken(context: Context): AuthResponse {
        initMsalClient(context)
        val currentAccount = mAccount ?: getCurrentAccount()
        return acquireTokenSilently(currentAccount)
    }

    internal fun getCurrentAccount(): IAccount? {
        return mAccount ?: mSingleAccountApp?.currentAccount?.currentAccount
    }

    private fun acquireTokenSilently(account: IAccount?): AuthResponse {
        val authResponse = AuthResponse()
        try {
            account?.authority?.let {
                val parameters = AcquireTokenSilentParameters.Builder()
                    .withScopes(scopes.toList())
                    .forAccount(account)
                    .fromAuthority(account.authority)
                    .forceRefresh(true)
                    .build()
                val value = mSingleAccountApp?.acquireTokenSilent(parameters)
                authResponse.token = value?.accessToken
            }
        } catch (exception: Exception) {
            authResponse.error = parseMsalException(exception)
        }
        return authResponse
    }

    private fun parseMsalException(exception: Exception?): String {
        return if (exception != null && exception is MsalClientException) {
            when (exception.errorCode) {
                MsalClientException.DEVICE_NETWORK_NOT_AVAILABLE, MsalClientException.IO_ERROR -> {
                    AuthErrorCodes.NETWORK_ERROR
                }
                ErrorStrings.UNSUPPORTED_BROKER_VERSION_ERROR_CODE -> AuthErrorCodes.BROKER_NOT_SUPPORTED
                else -> AuthErrorCodes.UNKNOWN
            }
        } else {
            AuthErrorCodes.UNKNOWN
        }
    }

    private fun getSignInCallback(): AuthenticationCallback {
        return object : AuthenticationCallback {
            override fun onSuccess(authenticationResult: IAuthenticationResult) {
                val accessToken = authenticationResult.accessToken
                sendResponse(accessToken, null)
            }

            override fun onError(exception: MsalException) {
                val errorCode = parseMsalException(exception)
                sendResponse(null, errorCode)
            }

            override fun onCancel() {
                sendResponse(null, AuthErrorCodes.USER_CANCELLED)
            }
        }
    }

    private fun sendResponse(token: String?, error: String?) {
        callback?.invoke(AuthResponse(token, error))
    }

    fun logout(context: Context, callback: ((success: Boolean, exception: Exception?) -> Unit)?) {
        initMsalClient(context)
        mSingleAccountApp?.signOut(object : ISingleAccountPublicClientApplication.SignOutCallback {
            override fun onSignOut() {
                if (mAccount != null) {
                    mAccount = null
                }
                callback?.invoke(true, null)
            }

            override fun onError(exception: MsalException) {
                callback?.invoke(false, exception)
            }
        })
    }


    companion object {
        private var INSTANCE: MsalAuthManager? = null
        fun getInstance(): MsalAuthManager {
            if (INSTANCE == null) {
                INSTANCE = MsalAuthManager()
            }
            return INSTANCE as MsalAuthManager
        }
    }
}