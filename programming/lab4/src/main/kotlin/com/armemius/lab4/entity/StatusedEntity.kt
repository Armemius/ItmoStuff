package com.armemius.lab4.entity

class StatusedEntity(name: String, val status: EntityStatus) : Entity(name) {
    override fun toString(): String {
        return "${status.status} ${super.toString()}"
    }
}