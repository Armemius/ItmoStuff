package com.armemius.lab4.entity

interface ITalking {
    fun tell(entity: ITalking?, message: String, vararg companions: ITalking)
}