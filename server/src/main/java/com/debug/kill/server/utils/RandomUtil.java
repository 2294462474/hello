package com.debug.kill.server.utils;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.concurrent.ThreadLocalRandom;

public class RandomUtil {


    private static final SimpleDateFormat dateFormateOne=new SimpleDateFormat("yyyyMMddHHmmssSS");

    private static final ThreadLocalRandom radom=ThreadLocalRandom.current();

    public static String generateOrderCode(){

       return dateFormateOne.format(DateTime.now().toDate())+generateNumber(4);
    }


    public static String generateNumber(final int num){

        StringBuffer sb=new StringBuffer();
        for(int i=1;i<num;i++)
        {
            sb.append(radom.nextInt(9));

        }
        return sb.toString();

    }
    public static void main(String []args){
        for(int i=0;i<100000;i++)
        {
            System.out.println(generateOrderCode());
        }
    }
}
