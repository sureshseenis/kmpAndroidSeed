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

data class AuthResponse(
    var token: String? = null,
    var error: String? = null
)

object AuthErrorCodes {
    const val USER_CANCELLED = "user_cancelled"
    const val UNKNOWN = "unknown_error"
    const val NETWORK_ERROR = "device_network_not_available"
    const val BROKER_NOT_SUPPORTED = "broker_not_supported"
    const val IO_ERROR = "io_error"
    const val ACCESS_DENIED = "access_denied"
    const val UNAUTHORIZED_CLIENT = "unauthorized_client"
    const val SERVICE_NOT_AVAILABLE = "service_not_available"
    const val INVALID_REQUEST = "invalid_request"
}