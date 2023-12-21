package com.kcube.cubite.modals

import com.google.gson.annotations.SerializedName


data class Employees (

    @SerializedName("emp_id"   ) var empId   : Int?    = null,
    @SerializedName("emp_name" ) var empName : String? = null

)