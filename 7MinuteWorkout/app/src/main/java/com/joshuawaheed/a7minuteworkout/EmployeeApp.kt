package com.joshuawaheed.a7minuteworkout

import android.app.Application

class EmployeeApp: Application() {
    val db by lazy {
        EmployeeDatabase.getInstance(this)
    }
}