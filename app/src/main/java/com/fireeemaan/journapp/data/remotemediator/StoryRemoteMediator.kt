package com.fireeemaan.journapp.data.remotemediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.fireeemaan.journapp.data.retrofit.story.StoryApiService
import com.fireeemaan.journapp.database.remotekeys.RemoteKeys
import com.fireeemaan.journapp.database.story.StoryDatabase
import com.fireeemaan.journapp.database.story.StoryEntity

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(
    private val storyDatabase: StoryDatabase,
    private val storyApiService: StoryApiService
) : RemoteMediator<Int, StoryEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, StoryEntity>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }

            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey ?: return MediatorResult.Success(
                    endOfPaginationReached = remoteKeys != null
                )
                prevKey
            }

            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey ?: return MediatorResult.Success(
                    endOfPaginationReached = remoteKeys != null
                )
                nextKey
            }
        }

        try {
            val response = storyApiService.getStories(page, state.config.pageSize)
            val responseData = response.body()?.listStory?.map {
                StoryEntity(
                    id = it.id,
                    photoUrl = it.photoUrl,
                    createdAt = it.createdAt,
                    name = it.name,
                    description = it.description,
                    lat = it.lat,
                    lon = it.lon
                )
            } ?: emptyList()

            val endofPaginationReached = responseData.isEmpty()
            storyDatabase.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    storyDatabase.remoteKeysDao().deleteRemoteKeys()
                    storyDatabase.storyDao().deleteAll()
                }
                val prevKey = if (page == 1) null else page - 1
                val nextKey = if (endofPaginationReached) null else page + 1
                val keys = responseData.map {
                    RemoteKeys(
                        id = it.id,
                        prevKey = prevKey,
                        nextKey = nextKey
                    )
                }
                storyDatabase.remoteKeysDao().insertAll(keys)
                storyDatabase.storyDao().insert(responseData)
            }
            return MediatorResult.Success(endOfPaginationReached = endofPaginationReached)
        } catch (e: Exception) {
            return MediatorResult.Error(e)
        }
    }

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, StoryEntity>): RemoteKeys? {
        return state.pages.lastOrNull {
            it.data.isNotEmpty()
        }?.data?.lastOrNull()?.let { data ->
            storyDatabase.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, StoryEntity>): RemoteKeys? {
        return state.pages.firstOrNull {
            it.data.isNotEmpty()
        }?.data?.firstOrNull()?.let { data ->
            storyDatabase.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, StoryEntity>): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                storyDatabase.remoteKeysDao().getRemoteKeysId(id)
            }
        }
    }

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

}