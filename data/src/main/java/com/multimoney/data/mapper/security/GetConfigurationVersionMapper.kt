package com.multimoney.data.mapper.security

import com.multimoney.data.networking.graphql.apollomodel.GetConfigurationVersionQuery
import com.multimoney.domain.model.security.AccountSmart
import com.multimoney.domain.model.security.Configuration
import com.multimoney.domain.model.security.ConfigurationVersion
import com.multimoney.domain.model.security.Credit
import com.multimoney.domain.model.security.Crypto
import com.multimoney.domain.model.security.Currency
import com.multimoney.domain.model.security.PaymentMethod
import com.multimoney.domain.model.security.SmartTransferLimit
import com.multimoney.domain.model.security.TransferAccount
import com.multimoney.domain.model.security.VirtualCard

private fun GetConfigurationVersionQuery.Moneda.mapToDomainModel() = Currency(code = codigo, description = descripcion)

private fun GetConfigurationVersionQuery.MetodoAbono.mapToDomainModel() = PaymentMethod(
    description = descripcion,
    type = tipo,
    mask = mascara,
    active = activo
)

private fun GetConfigurationVersionQuery.MetodoTransferencium.mapToDomainModel() = PaymentMethod(
    description = descripcion,
    type = tipo,
    mask = mascara,
    active = activo
)

private fun GetConfigurationVersionQuery.LimiteTransacion.mapToDomainModel() = SmartTransferLimit(
    code = codigo,
    description = descripcion,
    amount = monto.toString().toDoubleOrNull()
)

private fun GetConfigurationVersionQuery.AccountSmart.mapToDomainModel() = AccountSmart(
    active = activo,
    paymentMethod = metodoAbono.map { it.mapToDomainModel() },
    transferMethod = metodoTransferencia.map { it.mapToDomainModel() },
    transferLimit = limiteTransacion?.map { it?.mapToDomainModel() }
)

private fun GetConfigurationVersionQuery.MetodoPago.mapToDomainModel() = PaymentMethod(
    description = descripcion,
    type = tipo,
    mask = mascara,
    active = activo
)

private fun GetConfigurationVersionQuery.CuentaTransferencia.mapToDomainModel() = TransferAccount(
    account = numeroCuenta,
    bank = banco,
    typeTransfer = tipoTransferencia,
    beneficiaryName = nombreBeneficiario
)

private fun GetConfigurationVersionQuery.Credit.mapToDomainModel() = Credit(
    active = activo,
    paymentMethod = metodoPago.map { it.mapToDomainModel() },
    transferAccount = cuentaTransferencia.mapToDomainModel()
)

private fun GetConfigurationVersionQuery.Crypto.mapToDomainModel() = Crypto(
    active = activo,
    origin = origen,
    isTransferEnabled = habilitarTransferencias
)

private fun GetConfigurationVersionQuery.VirtualCard.mapToDomainModel() = VirtualCard(
    active = null
)

private fun GetConfigurationVersionQuery.ConfiguracionVersion.mapToDomainModel() = Configuration(
    timeSession = timeSesion,
    currency = moneda.map { it.mapToDomainModel() },
    accountSmart = accountSmart.mapToDomainModel(),
    credit = credit.mapToDomainModel(),
    crypto = crypto.mapToDomainModel(),
    virtualCard = virtualCard.mapToDomainModel()
)

private fun GetConfigurationVersionQuery.GetConfigurationVersion.mapToDomainModel() = ConfigurationVersion(
    active = aCTIVA,
    configuration = configuracionVersion?.mapToDomainModel()
)

fun GetConfigurationVersionQuery.Data.mapToDomainModel() = getConfigurationVersion.mapToDomainModel()
