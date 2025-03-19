package com.pranavkd.bustracker_conductor

data class Businfo(
    val busId:String,
    val busName:String,
    val state : String,
    val routes:List<Routes>
)

data class Routes(
    val name:String,
    val iscompleted:Boolean
)

data class passenger(
    val bookingId : String,
    val fullname : String,
    val email : String,
    val phone : String,
    val gender : String,
    val source : String,
    val destination : String
)