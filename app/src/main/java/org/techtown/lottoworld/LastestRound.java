package org.techtown.lottoworld;

import android.content.Context;
import android.hardware.lights.LightState;
import android.util.Log;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class LastestRound {
    public static int round = 1031;

    public LastestRound() throws ParseException {
        round = calculateWeeks();
    }

    public static int getRound() {
        return round;
    }


    public int calculateWeeks() throws ParseException {
        Calendar getToday = Calendar.getInstance();
        getToday.setTime(new Date()); //금일 날짜

        String s_date = "2022-08-13";
        Date date = new SimpleDateFormat("yyyy-MM-dd").parse(s_date);
        Calendar cmpDate = Calendar.getInstance();
        cmpDate.setTime(date); //특정 일자

        long diffSec = (getToday.getTimeInMillis() - cmpDate.getTimeInMillis()) / 1000;
        long diffDays = diffSec / (24*60*60); //일자수 차이

        round = 1028 + (int) diffDays / 7;
        return round;
    }

}
