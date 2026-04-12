package com.zyvault.app.data.api

import retrofit2.http.Body
import retrofit2.http.POST

data class LinkTokenRequest(val userId: String)
data class LinkTokenResponse(val linkToken: String)

data class ExchangeTokenRequest(val publicToken: String, val userId: String)
data class ExchangeTokenResponse(val success: Boolean)

data class IdentitySessionRequest(val userId: String)
data class IdentitySessionResponse(val clientSecret: String)

interface PlaidApi {
    @POST("api/plaid/create_link_token")
    suspend fun createLinkToken(@Body request: LinkTokenRequest): LinkTokenResponse

    @POST("api/plaid/exchange_public_token")
    suspend fun exchangePublicToken(@Body request: ExchangeTokenRequest): ExchangeTokenResponse

    @POST("api/plaid/transfer/create")
    suspend fun createTransfer(@Body request: TransferRequest): TransferResponse
}

data class TransferRequest(
    val userId: String,
    val fromAccountId: String,
    val toAccountId: String,
    val amount: Double
)

data class TransferResponse(val success: Boolean, val status: String)

interface StripeApi {
    @POST("api/stripe/create_verification_session")
    suspend fun createVerificationSession(@Body request: IdentitySessionRequest): IdentitySessionResponse
}
