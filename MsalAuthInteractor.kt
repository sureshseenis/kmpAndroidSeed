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
import com.microsoft.identity.client.IAccount

interface MsalAuthInteractor {

    fun loginWithMsal(context: Context, callback: (token: AuthResponse?) -> Unit)

    fun getSilentToken(context: Context): AuthResponse

    suspend fun instantiateMsal(context: Context)

    suspend fun getCurrentAccount(): IAccount?

    suspend fun logout(
        context: Context,
        callback: ((success: Boolean, exception: Exception?) -> Unit)? = null
    )

    companion object {
        fun getInstance(): MsalAuthInteractor = MsalAuthInteractorImpl.getInstance()
    }
}