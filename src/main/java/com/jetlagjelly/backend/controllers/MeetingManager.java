package com.jetlagjelly.backend.controllers;

import com.jetlagjelly.backend.Endpoints;

import java.util.ArrayList;

public class MeetingManager {

    public static ArrayList<Long> removeShort(ArrayList<Long> x, long length){

        for(int i=0; i<x.size(); i=i+2)
        {
            if((x.get(i+1) - x.get(i) < length))
            {
                x.remove(i); x.remove(i); i=i-2;
            }
        }

        return x;
    }

    public static ArrayList<Long> removeHour(ArrayList<Long> x){

        for(int i=0; i<x.size(); i=i+2)
        {
            if((x.get(i+1) - x.get(i) < 3600000)) //# of milliseconds in an hour
            {
                x.remove(i); x.remove(i); i=i-2;
            }
        }

        return x;
    }

    public static long[] removeShortReturnArray(ArrayList<Long> x, long length){

        ArrayList<Long> z = removeShort(x, length);
        long[] zz = new long[z.size()];
        for(int i=0;i<z.size();i++){zz[i]=z.get(i);}
        return zz;

    }

    public static long[] removeHourReturnArray(ArrayList<Long> x){

        ArrayList<Long> z = removeHour(x);
        long[] zz = new long[z.size()];
        for(int i=0;i<z.size();i++){zz[i]=z.get(i);}
        return zz;

    }


}