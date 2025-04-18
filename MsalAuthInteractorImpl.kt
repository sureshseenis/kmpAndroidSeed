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

import android.content.Context
import android.content.Intent
import com.microsoft.identity.client.IAccount
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class MsalAuthInteractorImpl : MsalAuthInteractor {

    internal var callback: ((AuthResponse?) -> Unit)? = null

    lateinit var msalManager: MsalAuthManager

    override fun loginWithMsal(context: Context, callback: (token: AuthResponse?) -> Unit) {
        val response = msalManager.getSilentToken(context)
        if (response.token != null) {
            callback(response)
        } else {
            this@MsalAuthInteractorImpl.callback = callback
            val intent = Intent(context, MsalLoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    override fun getSilentToken(context: Context): AuthResponse {
        return msalManager.getSilentToken(context)
    }

    override suspend fun instantiateMsal(context: Context) {
        msalManager.initMsalClient(context)
    }

    override suspend fun getCurrentAccount(): IAccount? {
        return msalManager.getCurrentAccount()
    }

    override suspend fun logout(
        context: Context,
        callback: ((success: Boolean, exception: Exception?) -> Unit)?
    ) {
        withContext(Dispatchers.IO) {
            msalManager.logout(context, callback)
        }
    }

    companion object {
        private var INSTANCE: MsalAuthInteractorImpl? = null

        fun getInstance(): MsalAuthInteractorImpl {
            if (INSTANCE == null) {
                INSTANCE = MsalAuthInteractorImpl()
                INSTANCE?.msalManager = MsalAuthManager.getInstance()
            }

            return INSTANCE!!
        }
    }
}
