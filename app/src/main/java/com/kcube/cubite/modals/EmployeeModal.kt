package com.kcube.cubite.modals

import com.google.gson.annotations.SerializedName


data class EmployeeModel (

    @SerializedName("employess" ) var employess : MutableList<Employees> = arrayListOf()

)