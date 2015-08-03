package com.galiamov.neoshow.extension

import org.codehaus.groovy.runtime.DateGroovyMethods

import static java.lang.System.currentTimeMillis

class TVShowHelper {

    private static final Random random = new Random()

    static getRandomTVShow() {
        def tvShow = [
                title       : getRandomTitle(),
                release_date: getRandomDate()
        ]
        if (random.nextBoolean()) {
            tvShow << [end_date: getRandomDate()]
        }
        tvShow
    }

    private static getRandomTitle() {
        long now = currentTimeMillis()
        int rnd = random.nextInt(10000)
        "tvShow " + now + "_" + rnd
    }

    private static getRandomDate() {
        def year = 1900 + random.nextInt(200)
        def month = 1 + random.nextInt(11)
        def day = random.nextInt(35)
        def calendar = Calendar.getInstance()
        calendar.set(year, month, 1)
        Date randomDate = DateGroovyMethods.minus(calendar.time, day)
        randomDate.getTime()
    }

}
