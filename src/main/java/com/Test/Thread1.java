package com.Test;

public class Thread1 {

    public static void main(String[] args){
        //ThreadRunning tr=new ThreadRunning();//初始化
        //Thread thread=new Thread(tr);
        //thread.start();//就绪
        //运行
        //System.out.print("结束了");//结束


        for (int j=0;j<10;j++){
            ThreadRunning tr=new ThreadRunning();//初始化
            Thread thread=new Thread(tr);
            System.out.println(thread.getName());
            thread.start();//就绪
            System.out.println("结束_"+String.valueOf(j));
        }

    }

}


class ThreadRunning implements Runnable{


    @Override
    public void run() {

        Sync sync=new Sync();
        sync.test();
    }
}


class  Sync{
    public synchronized void test() {
        System.out.println("test开始..");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("test结束..");
    }


    public  void test2() {
        /*这种情况下,因为每次sync都会创建，因此只能保证不同线程在访问同个对象可以互斥，如果对象呗重新创建Test2是可以被再调用的*/
        synchronized (this){

            System.out.println("test开始..");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("test结束..");
        }
    }


    public  void test3() {
        /*在底层调用锁住了class类，相当于全局锁*/
        synchronized (Sync.class){

            System.out.println("test开始..");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("test结束..");
        }
    }
}
