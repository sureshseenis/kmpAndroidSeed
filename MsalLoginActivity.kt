/*
 * © 2021 Infosys Limited, Bangalore, India. All Rights Reserved.
 * Version:1.0.0.0
 *
 * Except for any free or open source software components
 * embedded in this Infosys proprietary software program (“Live Enterprise Employee Experience Interaction Suite”),
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

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.microsoft.aad.adal.AuthenticationConstants.UIResponse.BROWSER_CODE_CANCEL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MsalLoginActivity : AppCompatActivity() {

    private val msalAuthManager = MsalAuthManager.getInstance()
    private val authCallback = MsalAuthInteractorImpl.getInstance().callback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_msal_login)
        msalAuthManager.callback = { sendResponse(it) }

        lifecycleScope.launch(Dispatchers.IO) {
            msalAuthManager.getToken(this@MsalLoginActivity)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            BROWSER_CODE_CANCEL -> {
                onCancelled()
            }
            else -> sendResponse(AuthResponse(null, resultCode.toString()))
        }
    }

    private fun onCancelled() {
        sendResponse(AuthResponse(null, AuthErrorCodes.USER_CANCELLED))
    }

    private fun sendResponse(response: AuthResponse?) {
        authCallback?.invoke(response)
        finish()
    }

    override fun onBackPressed() {
        onCancelled()
        super.onBackPressed()
    }
}