import java.util.ArrayList;
import java.util.Scanner;


public class Test implements Runnable, PIDManager{

    public static long startTime;
    public static int lifetimeProgram;
    public static int lifetimeThread;
    public static int cur = MIN_PID;  // current pid
    public static int[] pidArr = new int[MAX_PID - MIN_PID + 1];
    public static int semaphore = MAX_PID - MIN_PID + 1;
    public static int select;

    int num;    // thread num

    public Test(int num) {
        this.num = num;
    }

    private Object obj = new Object();

    @Override
    public int getPID() {
        // if no pids are available, return -1.
        if (MIN_PID <= cur & cur <= MAX_PID){
            pidArr[cur-MIN_PID] = 1;    // change flag to represent it is using
            cur++;
            return (cur-1);
        }
        else{
            return -1;
        }
    }

    @Override
    public int getPIDWait() {
        // if no pids are available, block the calling process until a pid becomes available.
        synchronized (obj){
            if (semaphore > 0){
                semaphore--;
                for (int i=0; i<MAX_PID-MIN_PID+1; i++){
                    if(pidArr[i] == 0){
                        pidArr[i] = 1;
                        return i + MIN_PID;
                    }
                }
            }
            else{
                try{
                    obj.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            semaphore--;
            for (int i=0; i<MAX_PID-MIN_PID+1; i++){
                if(pidArr[i] == 0){
                    pidArr[i] = 1;
                    return i + MIN_PID;
                }
            }

        }
        return 0;
    }

    @Override
    public void releasePID(int pid) {
        synchronized (obj){
            pidArr[pid-MIN_PID] = 0;
            semaphore++;
            obj.notify();
        }

    }

    @Override
    public void run() {
        int pid = 0;

        if (select == 1){
            pid = getPID();
            if (pid == -1){
                System.out.println("Thread "+ num + " No pids are available");
                return;
            }
        }
        else{
            pid = getPIDWait();
        }

        long finishTime = System.currentTimeMillis();
        long differ = (long)((finishTime - startTime)/(1000));

        if(differ <= lifetimeProgram){
            System.out.println(num + " created at Second " + differ + " / ");
        }

        try{
            Thread.sleep(1000*lifetimeThread);
        } catch (InterruptedException e) {
        }

        if(differ+lifetimeThread <= lifetimeProgram){
            System.out.println(num + " destroyed at Second " + (differ + lifetimeThread));
        }
        releasePID(pid);

    }


    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // the number of threads
        System.out.print("Please input the number of threads : ");
        int threadNum = sc.nextInt();

        // the lifetime of the program
        System.out.print("Please input the lifetime of the program : ");
        lifetimeProgram = sc.nextInt();

        // lifetime of a thread
        System.out.print("Please input the lifetime of the thread : ");
        lifetimeThread = sc.nextInt();

        // select (1) getPID() or (2) getPIDWait()
        System.out.println("------------------------------------------");
        System.out.println("(1) getPID()\t(2) getPIDWait()");
        System.out.print("Please select the number you want to use : ");
        select = sc.nextInt();
        System.out.println("------------------------------------------");
        System.out.println("Test program is initialized with " + threadNum + " thread and " + lifetimeProgram + " seconds, with life time " + lifetimeThread +" seconds of each thread");


        startTime = System.currentTimeMillis();

        ArrayList<Thread> arr = new ArrayList();

        for (int i=1; i<=threadNum; i++){
            long Time = System.currentTimeMillis();
            long differ = (long)((Time-startTime)/(1000));

            if(differ > lifetimeProgram){
                break;
            }
            try{
                Thread.sleep((int)(Math.random() * 1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Thread t = new Thread(new Test(i));
            arr.add(t);
            t.start();
        }

        while(true){
            long finishTime = System.currentTimeMillis();
            long differ = (long)((finishTime - startTime)/(1000));

            for(int i=0; i<arr.size(); i++){
                arr.get(i).interrupt();
            }

            if(differ >= lifetimeProgram){
                System.out.println(lifetimeProgram+" seconds has passed... Program ends");
                break;
            }
        }

        System.exit(0);



    }


}
