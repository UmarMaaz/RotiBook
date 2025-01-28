package com.rotibook

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DataExportImport {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)

    suspend fun exportData(context: Context, uri: Uri, clients: List<Client>, purchases: List<RotiPurchase>) {
        withContext(Dispatchers.IO) {
            val jsonObject = JSONObject()

            val clientsArray = JSONArray()
            clients.forEach { client ->
                clientsArray.put(JSONObject().apply {
                    put("id", client.id)
                    put("name", client.name)
                    put("phoneNumber", client.phoneNumber)
                })
            }
            jsonObject.put("clients", clientsArray)

            val purchasesArray = JSONArray()
            purchases.forEach { purchase ->
                purchasesArray.put(JSONObject().apply {
                    put("id", purchase.id)
                    put("clientId", purchase.clientId)
                    put("quantity", purchase.quantity)
                    put("date", dateFormat.format(purchase.date))
                })
            }
            jsonObject.put("purchases", purchasesArray)

            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                OutputStreamWriter(outputStream).use { writer ->
                    writer.write(jsonObject.toString(4))
                }
            }
        }
    }

    suspend fun importData(context: Context, uri: Uri): Pair<List<Client>, List<RotiPurchase>> {
        return withContext(Dispatchers.IO) {
            val jsonString = context.contentResolver.openInputStream(uri)?.bufferedReader().use { it?.readText() }
            val jsonObject = JSONObject(jsonString)

            val clients = mutableListOf<Client>()
            val clientsArray = jsonObject.getJSONArray("clients")
            for (i in 0 until clientsArray.length()) {
                val clientObject = clientsArray.getJSONObject(i)
                clients.add(Client(
                    id = clientObject.getInt("id"),
                    name = clientObject.getString("name"),
                    phoneNumber = clientObject.getString("phoneNumber")
                ))
            }

            val purchases = mutableListOf<RotiPurchase>()
            val purchasesArray = jsonObject.getJSONArray("purchases")
            for (i in 0 until purchasesArray.length()) {
                val purchaseObject = purchasesArray.getJSONObject(i)
                purchases.add(RotiPurchase(
                    id = purchaseObject.getInt("id"),
                    clientId = purchaseObject.getInt("clientId"),
                    quantity = purchaseObject.getInt("quantity"),
                    date = dateFormat.parse(purchaseObject.getString("date")) ?: Date()
                ))
            }

            Pair(clients, purchases)
        }
    }
}

