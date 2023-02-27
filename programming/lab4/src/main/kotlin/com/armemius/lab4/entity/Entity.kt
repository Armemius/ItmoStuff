package com.armemius.lab4.entity

import com.armemius.lab4.exception.MoveException
import com.armemius.lab4.location.Location

open class Entity(private val name: String): IMoving, ITalking, ISinger {
    private var location: Location = Location.getLocation("default")
    override fun moveTo(name: String, action: () -> Unit, vararg companions: IMoving) {
        if (name == location.name)
            throw MoveException("Trying to move to the same location from origin")
        location = Location.getLocation(name)
        if (companions.isEmpty()) {
            println("$this идёт в: ${location.name}");
            action()
            println("$this пришёл")
        } else {
            var names: String = this.toString();
            for (it in 0 until companions.size) {
                names += if (it + 1 == companions.size) {
                    " и ${companions[it]}"
                } else {
                    ", ${companions[it]}"
                }
            }
            println("$names идут в: ${location.name}");
            action();
            println("$names пришли");
        }

    }

    override fun tell(entity: ITalking?, message: String, vararg companions: ITalking) {
        if (entity == null) {
            println("$this говорит про себя: $message");
            return
        }
        if (companions.isEmpty()) {
            println("$this говорит name: $message");
        } else {
            var names: String = this.toString();
            for (it in 0 until companions.size) {
                names += if (it + 1 == companions.size) {
                    " и ${companions[it]}"
                } else {
                    ", ${companions[it]}"
                }
            }
            println("$names говорят $name: $message");
        }
    }

    override fun sing(name: String) {
        println("$this поёт $name")
    }

    override fun toString(): String {
        return name
    }
}

