package com.multimoney.domain.model.security

data class SendPinResponse(
    val pkUser:Int?,
    val email:String?,
    val userName:String?,
    val phone:String?,
    val identification:String?,
    val numberOfPinForwards:String?,
    val numberOfOtpForwards:String?,
    val pinExpirationTime:String?
)
