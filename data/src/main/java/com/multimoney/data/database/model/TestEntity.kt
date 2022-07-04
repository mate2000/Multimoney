package com.multimoney.data.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.multimoney.data.base.DomainMapper
import com.multimoney.domain.model.security.UserData

@Entity
data class TestEntity(
    @PrimaryKey
    val id: String
) : DomainMapper<UserData> {
    override fun mapToDomainModel() = UserData(
        pkUser = "pkUser",
        userName = "userName",
        email = "email",
        phoneNumber = "phoneNumber",
        fullName = "fullName",
        firstName = "firstName",
        secondName = "secondName",
        firstLastName = "lastName",
        secondLastName = "secondLastName",
        contactMeans = "contactMeans",
        nationality = "nationality",
        identification = "identification",
        countryCode = "countryCode",
        currentStep = "currentStep",
        userStatus = "userStatus"
    )
}
