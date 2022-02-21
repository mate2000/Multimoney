package com.multimoney.data.repository

import com.multimoney.data.base.BaseRepository
import com.multimoney.data.database.dao.TestDao
import com.multimoney.data.database.model.TestEntity
import com.multimoney.data.mapper.mapLaunchListQueryCacheToDomain
import com.multimoney.data.networking.MultimoneyApi
import com.multimoney.domain.model.launch.LaunchConnection
import com.multimoney.domain.model.util.MultimoneyResult
import com.multimoney.domain.repository.LaunchRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LaunchRepositoryImpl @Inject constructor(
    val multimoneyApi: MultimoneyApi,
    val testDao: TestDao
) : BaseRepository(),
    LaunchRepository {

//    override suspend fun getLaunchList(): Flow<MultimoneyResult<LaunchConnection>> = fetchData(
//        apolloCall = multimoneyApi.getLaunchList()
//    ).mapToDomain()

    override suspend fun getLaunchList(): Flow<MultimoneyResult<LaunchConnection>> = fetchData(
        apolloCall = multimoneyApi.getLaunchList(),
        dbSaveAction = {
            testDao.insertTest(TestEntity(id = it.launches.cursor))
        },
        dbDataProvider = {
            testDao.getTest()
        }
    ).mapLaunchListQueryCacheToDomain()
}
