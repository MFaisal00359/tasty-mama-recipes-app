package com.example.dapurmamatur.data.repository

import com.example.dapurmamatur.api.ApiService
import com.example.dapurmamatur.data.model.response.CategoriesListResponse
import com.example.dapurmamatur.data.model.response.MealsListResponse
import com.example.dapurmamatur.data.model.db.FoodDao
import com.example.dapurmamatur.db.FoodEntity
import com.example.dapurmamatur.utils.DataStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response

class MainRepository(private val apiService: ApiService, private val dao: FoodDao) {

    suspend fun getRandomFood(): Flow<Response<MealsListResponse>> {
        return flow {
            emit(apiService.getFoodRandom())
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getCategoriesList(): Flow<DataStatus<CategoriesListResponse>> {
        return flow {
            emit(DataStatus.loading())
            val response = apiService.getCategoriesList()
            if (response.isSuccessful) {
                emit(DataStatus.success(response.body()))
            } else {
                emit(DataStatus.error("Error fetching categories"))
            }
        }.catch {
            emit(DataStatus.error(it.message.toString()))
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getFoodsList(letter: String): Flow<DataStatus<MealsListResponse>> {
        return flow {
            emit(DataStatus.loading())
            val response = apiService.getFoodList(letter)
            if (response.isSuccessful) {
                emit(DataStatus.success(response.body()))
            } else {
                emit(DataStatus.error("Error fetching foods"))
            }
        }.catch {
            emit(DataStatus.error(it.message.toString()))
        }.flowOn(Dispatchers.IO)
    }

    // Other repository methods similar to above...

    suspend fun saveFood(entity: FoodEntity) = dao.saveFood(entity)
    suspend fun deleteFood(entity: FoodEntity) = dao.deleteFood(entity)
    fun existsFood(id: Int) = dao.existsFood(id)
    fun getDbFoodList() = dao.getAllFoods()
}