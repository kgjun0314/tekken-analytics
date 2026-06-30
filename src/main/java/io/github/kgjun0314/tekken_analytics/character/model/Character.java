package io.github.kgjun0314.tekken_analytics.character.model;

import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public enum Character {
    UNKNOWN(-1, "Unknown"),
    PAUL(0, "Paul"),
    LAW(1, "Law"),
    KING(2, "King"),
    YOSHIMITSU(3, "Yoshimitsu"),
    HWOARANG(4, "Hwoarang"),
    XIAOYU(5, "Xiaoyu"),
    JIN(6, "Jin"),
    BRYAN(7, "Bryan"),
    KAZUYA(8, "Kazuya"),
    STEVE(9, "Steve"),
    JACK_8(10, "Jack-8"),
    ASUKA(11, "Asuka"),
    DEVIL_JIN(12, "Devil Jin"),
    FENG(13, "Feng"),
    LILI(14, "Lili"),
    DRAGUNOV(15, "Dragunov"),
    LEO(16, "Leo"),
    LARS(17, "Lars"),
    ALISA(18, "Alisa"),
    CLAUDIO(19, "Claudio"),
    SHAHEEN(20, "Shaheen"),
    NINA(21, "Nina"),
    LEE(22, "Lee"),
    KUMA(23, "Kuma"),
    PANDA(24, "Panda"),
    ZAFINA(28, "Zafina"),
    LEROY(29, "Leroy"),
    JUN(32, "Jun"),
    REINA(33, "Reina"),
    AZUCENA(34, "Azucena"),
    VICTOR(35, "Victor"),
    RAVEN(36, "Raven"),
    EDDY(38, "Eddy"),
    LIDIA(39, "Lidia"),
    HEIHACHI(40, "Heihachi"),
    CLIVE(41, "Clive"),
    ANNA(42, "Anna"),
    FAHKUMRAM(43, "Fahkumram"),
    ARMOR_KING(44, "Armor King"),
    MIARY_ZO(45, "Miary Zo"),
    KUNIMITSU(46, "Kunimitsu"),;

    private final int id;
    private final String displayName;

    Character(int id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    private static final Map<Integer, Character> CACHE =
            Arrays.stream(values())
                    .collect(Collectors.toUnmodifiableMap(
                            Character::getId,
                            Function.identity()
                    ));

    public static Character fromId(int id) {
        return CACHE.getOrDefault(id, UNKNOWN);
    }
}
