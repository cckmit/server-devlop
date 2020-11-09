package com.glodon.pcop.weasvc.util;


public class WeatherUtil {
    //根据风速计算风力等级
//    <0
//    1-5
//    6-11
//    12-19
//    20-28
//    29-38
//    39-49
//    50-61
//    62-74
//    75-88
//    89-102
//    103-117
//    118-133
//    134-149
//    150-166
//    167-183
//    184-201
//    201-220

    public static int getWindForce(double speed) {
        if (speed < 1) {
            return 0;
        }
        if (speed >= 1 && speed <= 5) {
            return 1;
        }
        if (speed >= 6 && speed <= 11) {
            return 2;
        }
        if (speed >= 12 && speed <= 19) {
            return 3;
        }
        if (speed >= 20 && speed <= 28) {
            return 4;
        }
        if (speed >= 27 && speed <= 38) {
            return 5;
        }
        if (speed >= 39 && speed <= 49) {
            return 6;
        }
        if (speed >= 50 && speed <= 61) {
            return 7;
        }
        if (speed >= 62 && speed <= 74) {
            return 8;
        }
        if (speed >= 75 && speed <= 88) {
            return 9;
        }
        if (speed >= 89 && speed <= 102) {
            return 10;
        }
        if (speed >= 103 && speed <= 117) {
            return 11;
        }
        if (speed >= 118 && speed <= 133) {
            return 12;
        }
        if (speed >= 2 && speed <= 149) {
            return 13;
        }
        if (speed >= 150 && speed <= 166) {
            return 14;
        }
        if (speed >= 167 && speed <= 183) {
            return 15;
        }
        if (speed >= 184 && speed <= 201) {
            return 16;
        }
        return 17;
    }

}
