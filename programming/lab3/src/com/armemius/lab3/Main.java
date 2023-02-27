package com.armemius.lab3;

public class Main {
    public static void main(String[] args) {
        FriendEntity ia = new FriendEntity("Ослик Иа", Status.NEUTRAL);
        FriendEntity robin = new FriendEntity("Кристофер Робин", Status.NEUTRAL);
        FriendEntity pooh = new FriendEntity("Винни-Пух", Status.NEUTRAL);
        FriendEntity piglet = new FriendEntity("Пятачок", Status.BAD_VOICE);
        Entity owner = new StatusEntity("Владелец дома", Status.HAPPY);
        House.enter(ia);
        House.leave(ia);
        ia.tell(owner, "До связи", robin, pooh, piglet);
        robin.moveTo("Место для обеда", () -> {
            ia.tell(robin, "Мы совершили ужасную ошибку", pooh, piglet);
            ia.sing("Дорожная Шумелка для Снежной Погоды");
            robin.sing("Дорожная Шумелка для Снежной Погоды");
            pooh.sing("Дорожная Шумелка для Снежной Погоды");
            piglet.sing("Дорожная Шумелка для Снежной Погоды");
        }, ia, pooh, piglet);
    }
}

/*
Иа вошел в дом и снова вышел. Все попрощались со счастливым хозяином дома,
и Кристофер Робин пошел обедать со своими друзьями -- Пухом и Пятачком.
По дороге друзья рассказали ему об Ужасной Ошибке, которую они совершили,
и, когда он кончил смеяться, все трое дружно запели Дорожную Шумелку для Снежной Погоды и пели ее всю дорогу,
причем Пятачок, который все еще был немного не в голосе, только тирлимбомбомкал.
*/