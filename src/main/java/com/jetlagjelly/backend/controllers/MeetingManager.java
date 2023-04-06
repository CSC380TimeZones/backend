package com.jetlagjelly.backend.controllers;

import com.jetlagjelly.backend.Endpoints;

import java.util.ArrayList;

public class MeetingManager {

    public static void testAllTestCases(){
        System.out.println("\n");
        testIntersect();
        System.out.println("\n");
        testIntersectMany();
        System.out.println("\n");
        testIntersectReturnArray();
        System.out.println("\n");
        testIntersectManyReturnArray();
    }

    public static void testIntersect(){
        ArrayList<Long> a = new ArrayList<Long>();
        ArrayList<Long> b = new ArrayList<Long>();
        ArrayList<Long> c = new ArrayList<Long>();
        a.add(0L);a.add(1L);a.add(2L);a.add(3L);a.add(4L);a.add(9L);a.add(10L);a.add(12L);
        b.add(2L);b.add(4L);b.add(6L);b.add(11L);
        System.out.print("testIntersect(a,b,_): \n");
        System.out.print("a: " + a + "\n");
        System.out.print("b: " + b + "\n");
        c = intersect(a,b,c);
        System.out.print("intersect: " + c + "\n");
    }

    public static void testIntersectMany(){
        ArrayList<Long> a = new ArrayList<Long>();
        ArrayList<Long> b = new ArrayList<Long>();
        ArrayList<Long> ab = new ArrayList<Long>();
        ArrayList<Long> c = new ArrayList<Long>();
        ArrayList<ArrayList<Long>> meta = new ArrayList<ArrayList<Long>>();
        a.add(0L);a.add(1L);a.add(3L);a.add(7L);a.add(8L);a.add(9L);a.add(11L);a.add(12L);
        b.add(0L);b.add(2L);b.add(4L);b.add(5L);b.add(6L);b.add(10L);b.add(11L);b.add(12L);
        ab.add(0L);ab.add(2L);ab.add(4L);ab.add(5L);ab.add(6L);ab.add(10L);
        meta.add(a);meta.add(b);meta.add(ab);

        System.out.print("testIntersectMany(meta): \n");
        System.out.print("a: " + a + "\n");
        System.out.print("b: " + b + "\n");
        System.out.print("ab: " + ab + "\n");
        System.out.print("meta: " + meta + "\n");
        c = intersectMany(meta);
        System.out.print("intersect: " + c + "\n");
    }

    public static void testIntersectReturnArray(){
        ArrayList<Long> a = new ArrayList<Long>();
        ArrayList<Long> b = new ArrayList<Long>();
        ArrayList<Long> c = new ArrayList<Long>();
        a.add(0L);a.add(1L);a.add(2L);a.add(3L);a.add(4L);a.add(9L);a.add(10L);a.add(12L);
        b.add(2L);b.add(4L);b.add(6L);b.add(11L);
        System.out.print("testIntersectReturnArray(a,b,_): \n");
        System.out.print("a: " + a + "\n");
        System.out.print("b: " + b + "\n");

        long[] intersectReturnArray = intersectReturnArray(a,b,c);
        System.out.print("intersect: { ");
        for(int i=0;i<intersectReturnArray.length;i++){System.out.print(intersectReturnArray[i] + " ");}
        System.out.print("} \n");
    }

    public static void testIntersectManyReturnArray(){
        ArrayList<Long> a = new ArrayList<Long>();
        ArrayList<Long> b = new ArrayList<Long>();
        ArrayList<Long> ab = new ArrayList<Long>();
        ArrayList<Long> c = new ArrayList<Long>();
        ArrayList<ArrayList<Long>> meta = new ArrayList<ArrayList<Long>>();
        a.add(0L);a.add(1L);a.add(3L);a.add(7L);a.add(8L);a.add(9L);a.add(11L);a.add(12L);
        b.add(0L);b.add(2L);b.add(4L);b.add(5L);b.add(6L);b.add(10L);b.add(11L);b.add(12L);
        ab.add(0L);ab.add(2L);ab.add(4L);ab.add(5L);ab.add(6L);ab.add(10L);
        meta.add(a);meta.add(b);meta.add(ab);

        System.out.print("testIntersectManyReturnArray(meta): \n");
        System.out.print("a: " + a + "\n");
        System.out.print("b: " + b + "\n");
        System.out.print("ab: " + ab + "\n");
        System.out.print("meta: " + meta + "\n");
        long[] intersectReturnArray = intersectManyReturnArray(meta);
        System.out.print("intersect: { ");
        for(int i=0;i<intersectReturnArray.length;i++){System.out.print(intersectReturnArray[i] + " ");}
        System.out.print("} \n");
    }


    public static ArrayList<Long> intersect(ArrayList<Long> x, ArrayList<Long> y, ArrayList<Long> z){

        //establish larger and lesser [0]
        ArrayList<Long> larger, lesser;
        if(x.get(0)>=y.get(0)) {larger = x; lesser = y;} else {larger = y; lesser = x;}

        do{

            //remove everything in lesser under larger[0]
            for(int i=1;i!=0;i=i)
            {
                if(!lesser.isEmpty() && lesser.get(i)<=larger.get(0)){
                    lesser.remove(i);lesser.remove(i-1);
                }else{i=0;}
            }

            //pick larger of [0]
            if(!larger.isEmpty() && !lesser.isEmpty()) {

                if (larger.get(0) >= lesser.get(0)) {
                    z.add(larger.get(0));
                } else {
                    z.add(lesser.get(0));
                }
            }

            //pick lesser of [1]
            if(!larger.isEmpty() && !lesser.isEmpty()) {

                if (larger.get(1) <= lesser.get(1)) {
                    z.add(larger.get(1));
                } else {
                    z.add(lesser.get(1));
                }
            }

            //remove everything in lesser under z[last]
            for(int i=1;i!=0;i=i)
            {
                if(!lesser.isEmpty() && lesser.get(i)<=z.get(z.size() - 1))
                {lesser.remove(i);lesser.remove(i-1);}else{i=0;}
            }
            //remove everything in larger under z[last]
            for(int i=1;i!=0;i=i)
            {
                if(!larger.isEmpty() && larger.get(i)<=z.get(z.size() - 1))
                {larger.remove(i);larger.remove(i-1);}else{i=0;}
            }

            //re-pick larger and lesser
            if(!larger.isEmpty() && !lesser.isEmpty() && larger.get(0)<=lesser.get(0))
            {ArrayList<Long> temp = larger; larger = lesser; lesser = temp;}

        }while(!x.isEmpty() && !y.isEmpty());

        return z;
    }



    public static long[] intersectReturnArray(ArrayList<Long> x, ArrayList<Long> y, ArrayList<Long> z){

        z = intersect(x,y,z);
        long[] zz = new long[z.size()];
        for(int i =0;i<z.size(); i++){zz[i] = z.get(i);}
        return zz;
    }


    public static ArrayList<Long> intersectMany(ArrayList<ArrayList<Long>> meta) {

        while(meta.size()>1){
            ArrayList<Long> z = new ArrayList<Long>();
            ArrayList<Long> temp = intersect(meta.get(0),meta.get(1),z);
            meta.set(1,temp);
            meta.remove(0);
        }
        return meta.get(0);
    }

    public static long[] intersectManyReturnArray(ArrayList<ArrayList<Long>> meta) {

        ArrayList<Long> z = intersectMany(meta);
        long[] zz = new long[z.size()];
        for(int i=0;i<z.size();i++){zz[i]=z.get(i);}
        return zz;
    }


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