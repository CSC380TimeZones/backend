package com.jetlagjelly.backend;

import java.util.ArrayList;

import com.jetlagjelly.backend.controllers.MeetingManager;

public class MeetingManagerTests {
  public static void testAllTestCases() {
    System.out.println("\n");
    testIntersect();
    System.out.println("\n");
    testIntersectMany();
    System.out.println("\n");
    testIntersectReturnArray();
    System.out.println("\n");
    testIntersectManyReturnArray();
  }

  public static void testIntersect() {
    ArrayList<Long> a = new ArrayList<Long>();
    ArrayList<Long> b = new ArrayList<Long>();
    ArrayList<Long> c = new ArrayList<Long>();
    a.add(0L);
    a.add(1L);
    a.add(2L);
    a.add(3L);
    a.add(4L);
    a.add(9L);
    a.add(10L);
    a.add(12L);
    b.add(2L);
    b.add(4L);
    b.add(6L);
    b.add(11L);
    System.out.print("testIntersect(a,b,_): \n");
    System.out.print("a: " + a + "\n");
    System.out.print("b: " + b + "\n");
    c = MeetingManager.intersect(a, b, c);
    System.out.print("intersect: " + c + "\n");
  }

  public static void testIntersectMany() {
    ArrayList<Long> a = new ArrayList<Long>();
    ArrayList<Long> b = new ArrayList<Long>();
    ArrayList<Long> ab = new ArrayList<Long>();
    ArrayList<Long> c = new ArrayList<Long>();
    ArrayList<ArrayList<Long>> meta = new ArrayList<ArrayList<Long>>();
    a.add(0L);
    a.add(1L);
    a.add(3L);
    a.add(7L);
    a.add(8L);
    a.add(9L);
    a.add(11L);
    a.add(12L);
    b.add(0L);
    b.add(2L);
    b.add(4L);
    b.add(5L);
    b.add(6L);
    b.add(10L);
    b.add(11L);
    b.add(12L);
    ab.add(0L);
    ab.add(2L);
    ab.add(4L);
    ab.add(5L);
    ab.add(6L);
    ab.add(10L);
    meta.add(a);
    meta.add(b);
    meta.add(ab);

    System.out.print("testIntersectMany(meta): \n");
    System.out.print("a: " + a + "\n");
    System.out.print("b: " + b + "\n");
    System.out.print("ab: " + ab + "\n");
    System.out.print("meta: " + meta + "\n");
    c = MeetingManager.intersectMany(meta);
    System.out.print("intersect: " + c + "\n");
  }

  public static void testIntersectReturnArray() {
    ArrayList<Long> a = new ArrayList<Long>();
    ArrayList<Long> b = new ArrayList<Long>();
    ArrayList<Long> c = new ArrayList<Long>();
    a.add(0L);
    a.add(1L);
    a.add(2L);
    a.add(3L);
    a.add(4L);
    a.add(9L);
    a.add(10L);
    a.add(12L);
    b.add(2L);
    b.add(4L);
    b.add(6L);
    b.add(11L);
    System.out.print("testIntersectReturnArray(a,b,_): \n");
    System.out.print("a: " + a + "\n");
    System.out.print("b: " + b + "\n");

    long[] intersectReturnArray = MeetingManager.intersectReturnArray(a, b, c);
    System.out.print("intersect: { ");
    for (int i = 0; i < intersectReturnArray.length; i++) {
      System.out.print(intersectReturnArray[i] + " ");
    }
    System.out.print("} \n");
  }

  public static void testIntersectManyReturnArray() {
    ArrayList<Long> a = new ArrayList<Long>();
    ArrayList<Long> b = new ArrayList<Long>();
    ArrayList<Long> ab = new ArrayList<Long>();
    // ArrayList<Long> c = new ArrayList<Long>(); This variable was never used
    ArrayList<ArrayList<Long>> meta = new ArrayList<ArrayList<Long>>();
    a.add(0L);
    a.add(1L);
    a.add(3L);
    a.add(7L);
    a.add(8L);
    a.add(9L);
    a.add(11L);
    a.add(12L);
    b.add(0L);
    b.add(2L);
    b.add(4L);
    b.add(5L);
    b.add(6L);
    b.add(10L);
    b.add(11L);
    b.add(12L);
    ab.add(0L);
    ab.add(2L);
    ab.add(4L);
    ab.add(5L);
    ab.add(6L);
    ab.add(10L);
    meta.add(a);
    meta.add(b);
    meta.add(ab);

    System.out.print("testIntersectManyReturnArray(meta): \n");
    System.out.print("a: " + a + "\n");
    System.out.print("b: " + b + "\n");
    System.out.print("ab: " + ab + "\n");
    System.out.print("meta: " + meta + "\n");
    long[] intersectReturnArray = MeetingManager.intersectManyReturnArray(meta);
    System.out.print("intersect: { ");
    for (int i = 0; i < intersectReturnArray.length; i++) {
      System.out.print(intersectReturnArray[i] + " ");
    }
    System.out.print("} \n");
  }
}
