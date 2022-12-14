package com.multimoney.data.repository

import com.multimoney.data.base.BaseRepository
import com.multimoney.data.mapper.profile.mapToDomainModel
import com.multimoney.data.networking.GraphqlApi
import com.multimoney.domain.model.profile.CountryContact
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val graphqlApi: GraphqlApi
) : BaseRepository(), ProfileRepository {
    override suspend fun queryCountryContact(
        user: String,
        idBrand: Int
    ): Flow<MultimoneyResult<CountryContact?>> = fetchData(
        apolloCall = graphqlApi.queryCountryContact(
            user,
            idBrand
        ),
        apolloCallMapper = { data ->
            MultimoneyResult.Success(data.mapToDomainModel())
        }
    )
}