package com.galiamov.neoshow.extension


class UserHelper {

    private static final Random random = new Random()

    static getRandomUser() {
        [
                email : getRandomEmail(),
                age   : getRandomAge(),
                gender: getRandomGender()
        ]
    }

    private static getRandomEmail() {
        long now = System.currentTimeMillis()
        int rnd = random.nextInt(10000)
        "u" + now + "_" + rnd + "@test.com"
    }

    private static getRandomAge() {
        1 + random.nextInt(120)
    }

    private static getRandomGender() {
        1 + random.nextInt(2)
    }

}
