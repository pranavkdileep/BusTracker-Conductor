package com.pranavkd.bustracker_conductor

import android.util.Log

class Managers{
    val apiHelper = ApiHelper()
    fun loadDataHome(
        conductorId:String,
        callback: (error: String?, businfo: Businfo,passengers : List<passenger>) -> Unit
    ){
        var businfo = Businfo("","","", emptyList())
        var passengers = emptyList<passenger>()
        apiHelper.getBusInfo(conductorId) { error, bus ->
            if(error != null && bus == null){
                callback(error, businfo,passengers)
            }
            else if(bus != null){
                businfo = bus
                apiHelper.getPassengers(conductorId) { error, pass ->
                    if(error != null && pass == null){
                        callback(error, businfo,passengers)
                    }
                    else if(pass != null){
                        passengers = pass
                        callback(null, businfo,passengers)
                    }
                }
            }
        }
    }

    fun updateState(
        conductorId: String,
        state: String,
        callback: (error: String?) -> Unit
    ){
        Log.d("Managers",state)
        apiHelper.changeState(
            conductorId,
            state
        ) { error ->
            if(error != null){
                callback(error)
            }
            else{
                callback(null)
            }
        }
    }

    fun updateNoOfStopes(
        conductorId: String,
        newNoOfStops:Int,
        callback: (error: String?) -> Unit
    ){
        Log.d("Managers",newNoOfStops.toString())
        apiHelper.changeNoOfSopes(
            conductorId,
            newNoOfStops
        ) { error ->
            if(error != null){
                callback(error)
            }
            else{
                callback(null)
            }
        }
    }
}