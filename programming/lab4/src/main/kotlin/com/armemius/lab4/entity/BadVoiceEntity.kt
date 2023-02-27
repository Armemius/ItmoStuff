package com.armemius.lab4.entity

class BadVoiceEntity(name: String) : Entity(name) {
    override fun sing(name: String) {
        println("$this тирлимбомбомкает")
    }
}