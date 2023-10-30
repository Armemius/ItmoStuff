package com.armemius.lab4.location

class Location(val name: String, val status: LocationStatus) {
    override fun toString(): String {
        return "${status.status} $name"
    }

    fun info() {
        println("Тут находится $this")
    }

    companion object {
        private var locationMap: HashMap<String, Location> = hashMapOf()

        fun getLocation(name: String): Location {
            if (!locationMap.containsKey(name))
                throw NoSuchElementException("Unable to locate location with key '$name'")
            return locationMap[name]!!
        }

        init {
            locationMap["default"] = Location("default", LocationStatus.NORMAL)
            locationMap["lunch"] = Location("обед", LocationStatus.NORMAL)
            locationMap["edge"] = Location("опушка", LocationStatus.NORMAL)
            locationMap["house"] = Location("дом Иа", LocationStatus.EXTRA_COSY)
        }
    }
}