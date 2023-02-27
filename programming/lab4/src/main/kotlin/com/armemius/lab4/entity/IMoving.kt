package com.armemius.lab4.entity

interface IMoving {
    fun moveTo(name: String, action: () -> Unit, vararg companions: IMoving)
}
