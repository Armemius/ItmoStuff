package com.armemius.lab4

import com.armemius.lab4.entity.BadVoiceEntity
import com.armemius.lab4.entity.Entity
import com.armemius.lab4.entity.EntityStatus
import com.armemius.lab4.entity.StatusedEntity
import com.armemius.lab4.location.Location
import com.armemius.lab4.location.LocationStatus

fun main(args: Array<String>) {
    try {
        var entities = object {
            private var entityMap: HashMap<String, Entity> = hashMapOf()

            fun getEntity(name: String): Entity {
                if (!entityMap.containsKey(name))
                    throw NoSuchElementException("Unable to locate location with key '$name'")
                return entityMap[name]!!
            }

            init {
                entityMap["ia"] = StatusedEntity("Иа", EntityStatus.HAPPY)
                entityMap["robin"] = Entity("Робин")
                entityMap["pooh"] = Entity("Винни-Пух")
                entityMap["piglet"] = BadVoiceEntity("Пятачок")
            }
        }
        var ia = entities.getEntity("ia")
        var robin = entities.getEntity("robin")
        var pooh = entities.getEntity("pooh")
        var piglet = entities.getEntity("piglet")
        ia.moveTo("edge", {}, robin, pooh, piglet)
        Location.getLocation("house").info()
        ia.moveTo("house", {})
        ia.moveTo("edge", {})
        robin.tell(ia, "До связи", pooh, piglet)
        robin.moveTo("lunch", {
            ia.tell(robin, "Мы совершили ужасную ошибку", pooh, piglet);
            ia.sing("Дорожная Шумелка для Снежной Погоды");
            robin.sing("Дорожная Шумелка для Снежной Погоды");
            pooh.sing("Дорожная Шумелка для Снежной Погоды");
            piglet.sing("Дорожная Шумелка для Снежной Погоды");
        }, ia, pooh, piglet)
        piglet.tell(null, "Конечно, кажется, что тирлимбомбомкать легко, но далеко не каждый и с этим сумеет справиться!")
    } catch(e: Exception) {
        e.printStackTrace()
    }
}

/*
Они вышли на опушку, и там стоял дом Иа-- с виду уютный-преуютный.
Иа вошел в дом и снова вышел. Все попрощались со счастливым хозяином дома,
и Кристофер Робин пошел обедать со своими друзьями -- Пухом и Пятачком.
По дороге друзья рассказали ему об Ужасной Ошибке, которую они совершили, и,
когда он кончил смеяться, все трое дружно запели Дорожную Шумелку для Снежной Погоды и пели ее всю дорогу,
причем Пятачок, который все еще был немного не в голосе, только тирлимбомбомкал.
"Конечно, кажется, что тирлимбомбомкать легко,-- сказал Пятачок про себя,-- но далеко не каждый и с этим сумеет справиться!"
 */