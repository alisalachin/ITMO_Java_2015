package main;

import javafx.collections.transformation.SortedList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.junit.Assert.*;

/**
 * Created by iao on 15.11.2015.
 */
public class IterativeParallelismImplTests {
    IterativeParallelismImpl ip;
    List<Integer> l;
    @Before
    public void setUp() throws Exception {
        l=new ArrayList<>(300000);
        for(int j=0;j<3;j++) {
            l.add(-1);
            for (int i = 0; i < 99998; i++) {
                l.add(0);
            }
            l.add(1);
        }

        ip=new IterativeParallelismImpl();
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testMinimum() throws Exception {
        Long startTime=System.currentTimeMillis();
        Integer min=ip.minimum(2, l, Comparator.<Integer>naturalOrder());
        Long endTime=System.currentTimeMillis();
        System.out.println("minimum "+(endTime-startTime));
        assertEquals(new Integer(-1), min);



    }

    @Test
    public void testMaximum() throws Exception {
        Long startTime=System.currentTimeMillis();
        Integer max=ip.maximum(2, l, Comparator.<Integer>naturalOrder());
        Long endTime=System.currentTimeMillis();
        System.out.println("maximum "+(endTime-startTime));
        assertEquals(new Integer(1), max);



    }

    @Test
    public void testAll() throws Exception {
        Long startTime=System.currentTimeMillis();
        Boolean b=ip.all(2, l, ((p)->p>0));
        Long endTime=System.currentTimeMillis();
        System.out.println("all "+(endTime-startTime));
        assertEquals(false, b);


    }

    @Test
    public void testAny() throws Exception {
        Predicate<Integer> intPred = new Predicate<Integer>(){
            @Override
            public boolean test(Integer integer) {
                return integer==0;
            }
        };
        Long startTime=System.currentTimeMillis();
        Boolean b=ip.any(2, l, intPred);
        Long endTime=System.currentTimeMillis();
        System.out.println("any "+(endTime-startTime));
        assertEquals(true, b);

    }

    @Test
    public void testFilter() throws Exception {
        Predicate<Integer> intPred = new Predicate<Integer>() {
            @Override
            public boolean test(Integer integer) {
                return integer < 10;
            }
        };
        Long startTime=System.currentTimeMillis();
        List<Integer> list=ip.filter(2,l,intPred);
        Long endTime=System.currentTimeMillis();
        System.out.println("filter "+(endTime-startTime));
        List<Integer> list2=new ArrayList<>(300000);
        for(int j=0;j<3;j++) {
            list2.add(-1);
            for (int i = 0; i < 99998; i++) {
                list2.add(0);
            }
            list2.add(1);
        }
        assertEquals(list2,list);


    }

    @Test
    public void testMap() throws Exception {
        Function<Integer,Integer> myF = new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer integer) {
                return integer+10;
            }
        };
        Long startTime=System.currentTimeMillis();
        List<Integer> list=ip.map(2,l,myF);
        Long endTime=System.currentTimeMillis();
        System.out.println("map "+(endTime-startTime));
        List<Integer> list2=new ArrayList<>(300000);
        for(int j=0;j<3;j++) {
            list2.add(9);
            for (int i = 0; i < 99998; i++) {
                list2.add(10);
           }
            list2.add(11);
        }

        assertEquals(list2, list);

    }
}