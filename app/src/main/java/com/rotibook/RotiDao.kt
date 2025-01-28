package com.rotibook

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import androidx.room.*
import androidx.room.TypeConverter
import java.util.Date

@Dao
interface ClientDao {
    @Query("SELECT * FROM clients")
    fun getAllClients(): Flow<List<Client>>

    @Query("SELECT * FROM clients WHERE id = :clientId")
    fun getClientById(clientId: Int): Flow<Client>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClient(client: Client)

    @Update
    suspend fun updateClient(client: Client)

    @Delete
    suspend fun deleteClient(client: Client)
}

@Dao
interface RotiPurchaseDao {
    @Query("SELECT * FROM roti_purchases WHERE clientId = :clientId")
    fun getPurchasesForClient(clientId: Int): Flow<List<RotiPurchase>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPurchase(purchase: RotiPurchase)

    @Update
    suspend fun updatePurchase(purchase: RotiPurchase)

    @Delete
    suspend fun deletePurchase(purchase: RotiPurchase)

    @Query("SELECT * FROM roti_purchases")
    suspend fun getAllPurchases(): List<RotiPurchase>
}



class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}


