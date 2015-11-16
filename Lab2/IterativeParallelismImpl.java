package main;

import javafx.collections.transformation.SortedList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Created by iao on 13.11.2015.
 */
public class IterativeParallelismImpl implements IterativeParallelism {
private final Object lock=new Object();
   // public static

    class M<T> {
        T elem;
        boolean b;
    }
    class MinThread<T> extends Thread {
        int num;
        int threads;
        List<T> list;
        M m;
        Comparator<T> comparator;
        public MinThread(int num,List<T> list, int threads, M m, Comparator<T> comparator){
            this.num=num;
            this.threads=threads;
            this.list=list;
            this.m=m;
            this.comparator=comparator;
        }
        public void run() {
                for(int i=num; i< list.size(); i+=threads) {
                    if (comparator.compare(list.get(i),(T)m.elem)<0) {
                        synchronized (m) {
                            if (comparator.compare(list.get(i),(T)m.elem)<0) {
                                m.elem= list.get(i);
                            }
                        }

                    }
                }
                Thread.currentThread().interrupt();
            }
    }


    class MaxThread<T> extends Thread {
        int num;
        int threads;
        List<T> list;
        M m;
        Comparator<T> comparator;
        public MaxThread(int num,List<T> list, int threads, M m, Comparator<T> comparator){
            this.num=num;
            this.threads=threads;
            this.list=list;
            this.m=m;
            this.comparator=comparator;
        }
        public void run() {
                for(int i=num; i< list.size(); i+=threads) {
                    if (comparator.compare(list.get(i),(T)m.elem)>0) {
                        synchronized (m) {
                            if (comparator.compare(list.get(i),(T)m.elem)>0) {
                                m.elem= list.get(i);
                            }
                        }

                    }
                }
                Thread.currentThread().interrupt();

        }
    }

    class AllThread<T> extends Thread {
        int num;
        int threads;
        List<T> list;
        M m;
        Predicate<T> predicate;
        public AllThread(int num,List<T> list, int threads, M m, Predicate<T> predicate){
            this.num=num;
            this.threads=threads;
            this.list=list;
            this.m=m;
            this.predicate=predicate;
        }
        public void run() {
            for (int i = num; i < list.size(); i += threads) {
                if (m.b == false) {
                    Thread.currentThread().interrupt();
                }
                if (!predicate.test(list.get(i))) {
                    synchronized (lock) {
                        m.b = false;
                    }
                }
            }
        }
    }

    class AnyThread<T> extends Thread {
        int num;
        int threads;
        List<T> list;
        M m;
        Predicate<T> predicate;
        public AnyThread(int num,List<T> list, int threads, M m, Predicate<T> predicate){
            this.num=num;
            this.threads=threads;
            this.list=list;
            this.m=m;
            this.predicate=predicate;
        }
        public void run() {
            for (int i = num; i < list.size(); i += threads) {
                if (m.b == true) {
                    Thread.currentThread().interrupt();
                }
                if (predicate.test(list.get(i))) {
                    synchronized (lock) {
                        m.b=true;
                    }

                }
            }
        }
    }


    class FilterThread<T> extends Thread {
        int num;
        int threads;
        List<T> list;
        List<T> res;
        Predicate<T> predicate;
        public FilterThread(int num,List<T> list, int threads, List<T> res, Predicate<T> predicate){
            this.num=num;
            this.threads=threads;
            this.list=list;
            this.predicate=predicate;
            this.res=res;
        }
        public void run() {
            for (int i = num; i < list.size(); i += threads) {
                if (predicate.test(list.get(i))) {
                    synchronized (res) {
                        res.set(i, list.get(i));
                    }

                }
            }
        }
    }

    class MapThread<T,R> extends Thread {
        int num;
        int threads;
        List<T> list;
        List<R> res;
        Function<T, R> function;
        public MapThread(int num,List<T> list, int threads, List<R> res, Function<T, R> function){
            this.num=num;
            this.threads=threads;
            this.list=list;
            this.function=function;
            this.res=res;
        }
        public void run() {
            for (int i = num; i < list.size(); i += threads) {
                    R r=function.apply(list.get(i));
                    synchronized (res) {
                        res.set(i, r);
                    }
            }
        }
    }


    @Override
    public <T> T minimum(int threads, List<T> list, Comparator<T> comparator) {
        if ((threads<1)||(list==null)||(list.size()==0)) {
            System.out.println("входные данные не верны");
            return null;
        }
       M m=new M();
        m.elem=list.get(0);
        MinThread[] t =new MinThread[threads];
        for(int i=0;i<threads;i++){
            t[i]=new MinThread(i,list,threads,m, comparator);
            t[i].start();

        }
        for(int i=0;i<threads;i++){
            try {
                t[i].join();
            } catch (InterruptedException e) {
                System.out.println("InterrExc");
            }

        }
        return (T)m.elem;
    }

    @Override
    public <T> T maximum(int threads, List<T> list, Comparator<T> comparator) {
        if ((threads<1)||(list==null)||(list.size()==0)) {
            System.out.println("входные данные не верны");
            return null;
        }
        M m=new M();
        m.elem=list.get(0);
        MaxThread[] t =new MaxThread[threads];
        for(int i=0;i<threads;i++){
            t[i]=new MaxThread(i,list,threads,m, comparator);
            t[i].start();
        }
        for(int i=0;i<threads;i++){
            try {
                t[i].join();
            } catch (InterruptedException e) {
                System.out.println("InterrExc");
            }

        }
        return (T)m.elem;
    }

    @Override
    public <T> boolean all(int threads, List<T> list, Predicate<T> predicate) {
        if ((threads<1)||(list==null)||(list.size()==0)) {
            System.out.println("входные данные не верны");
            return false;
        }
        M m=new M();
        m.b=true;
        AllThread[] t =new AllThread[threads];
        for(int i=0;i<threads;i++){
            t[i]=new AllThread(i,list,threads,m,predicate);
            t[i].start();

        }
        for(int i=0;i<threads;i++){
            try {
                t[i].join();
            } catch (InterruptedException e) {
                System.out.println("InterrExc");
            }

        }
        return m.b;
    }

    @Override
    public <T> boolean any(int threads, List<T> list, Predicate<T> predicate) {
        if ((threads<1)||(list==null)||(list.size()==0)) {
            System.out.println("входные данные не верны");
            return false;
        }
        M m=new M();
        m.b=false;
        //T min=list.get(0);

        AnyThread[] t =new AnyThread[threads];
        for(int i=0;i<threads;i++){
            t[i]=new AnyThread(i,list,threads,m,predicate);
            t[i].start();

        }
        for(int i=0;i<threads;i++){
            try {
                t[i].join();
            } catch (InterruptedException e) {
                System.out.println("InterrExc");
            }

        }
       return m.b;
    }

    @Override
    public <T> List<T> filter(int threads, List<T> list, Predicate<T> predicate) {
        if ((threads<1)||(list==null)||(list.size()==0)) {
            System.out.println("входные данные не верны");
            return null;
        }
        List<T> res=new ArrayList<>();
        for(T t : list) {
            res.add(null);
        }
        FilterThread[] t =new FilterThread[threads];
        for(int i=0;i<threads;i++){
            t[i]=new FilterThread(i,list,threads,res,predicate);
            t[i].start();

        }
        for(int i=0;i<threads;i++){
            try {
                t[i].join();
            } catch (InterruptedException e) {
                System.out.println("InterrExc");
            }

        }

        List<T> resWithoutNull=new ArrayList<>();
        for(T t2 : res) {
            if(t2!=null) {
                resWithoutNull.add(t2);
            }
        }
        return resWithoutNull;
    }

    @Override
    public <T, R> List<R> map(int threads, List<T> list, Function<T, R> function) {
        if ((threads<1)||(list==null)||(list.size()==0)) {
            System.out.println("входные данные не верны");
            return null;
        }
        List<R> res=new ArrayList<>(list.size());
        for(T t : list) {
            res.add(null);
        }
        MapThread[] t =new MapThread[threads];
        for(int i=0;i<threads;i++){
            t[i]=new MapThread(i,list,threads,res,function);
            t[i].start();

        }
        for(int i=0;i<threads;i++){
            try {
                t[i].join();
            } catch (InterruptedException e) {
                System.out.println("InterrExc");
            }

        }
        return res;
    }

    public static void main(String[] args) {
        List<Integer> l=new ArrayList<>(5);
        l.add(3);
        l.add(4);
        l.add(1);
        l.add(5);
        l.add(2);
        IterativeParallelismImpl ip=new IterativeParallelismImpl();


        Integer min=ip.minimum(2, l, Comparator.<Integer>naturalOrder());
        System.out.println("min "+min);

        Integer max=ip.maximum(3, l, Comparator.<Integer>naturalOrder());
        System.out.println("max "+max);


        Boolean b=ip.all(2, l, ((p)->p<7));
        System.out.println("all "+b);

        Predicate<Integer> intPred = new Predicate<Integer>(){
            @Override
            public boolean test(Integer integer) {
                return integer>3;
            }


        };
        b=ip.any(2, l, intPred);
        System.out.println("any "+b);

        List<Integer> list=ip.filter(2,l,intPred);
        for(Integer integer : list) {
            System.out.println("filter "+integer);
        }

        Function<Integer,Integer> myF = new Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer integer) {
                return integer+10;
            }
        };

        list=ip.map(3,l,myF);
        for(Integer integer : list) {
     //       System.out.println("map "+integer);
        }
    }
}
