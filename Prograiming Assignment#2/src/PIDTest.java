
import java.util.ArrayList;
import java.util.Scanner;

public class PIDTest implements Runnable, PIDManager {
    static long beforeTime; // start time
    static int threadLife; // thread life
    static int programLife; // program life
    static int[] pid_arr = new int[MAX_PID - MIN_PID + 1]; // array to check PID
    static int point = MIN_PID; // pointer to check pid_arr
    static int checknum; // 1.get 2.wait
    static int sem = MAX_PID - MIN_PID + 1; // semaphore variable to check wait and notify

    // thread number
    int seq;

    // static object to synchronized in same monitor
    private static Object obj = new Object();

    // check var (to show result specific)
    static int[] pidcheck = new int[MAX_PID - MIN_PID + 1];

    // constructor(thread number)
    public PIDTest(int seq) {
        this.seq = seq;
    }

    @Override
    public int getPID() {
        // 1. getPID() check pid_arr to know whether there is available pid
        if (MIN_PID <= point & MAX_PID >= point) {
            pid_arr[point - MIN_PID] = 1;
            pidcheck[point - MIN_PID]++; /// check
            point++;
            return point - 1;
        } else {
            return -1;
        }

    }

    @Override
    public int getPIDWait() {
        // 2. getPIDWait
        // it is synchronized(obj) to use wait
        synchronized (obj) {
            // thread is available
            if (sem > 0) {
                sem--; // thread is used
                // check pid_arr to know whether there is available pid
                for (int i = 0; i < MAX_PID - MIN_PID + 1; i++) {
                    if (pid_arr[i] == 0) {
                        pidcheck[i]++; /// check
                        pid_arr[i] = 1;
                        return i + MIN_PID;
                    }
                }
                return 0;
                // thread is not available
            } else {
                try {
                    // wait until notify from releasePID()
                    obj.wait();
                } catch (Exception e) {

                }
            }
            // after wakeup, thread is used
            sem--;
            // check pid_arr to know whether there is available pid
            for (int i = 0; i < MAX_PID - MIN_PID + 1; i++) {
                if (pid_arr[i] == 0) {
                    pidcheck[i]++; /// check
                    pid_arr[i] = 1;
                    return i + MIN_PID;
                }
            }
            return 0;
        }
    }

    @Override
    public void releasePID(int pid) {
        // it is synchronized(obj) to use notify
        synchronized (obj) {
            pid_arr[pid - MIN_PID] = 0;
            // thread is available
            sem++;
            // notify to wait thread
            obj.notify();
        }
    }

    @Override
    public void run() {
        // id is thread number
        int id = 0;
        if (checknum == 1) {
            // 1. getPID()
            id = getPID();
            if (id == -1) {
                System.out.println(this.seq + " thread : no pids are available");
                return;
            }
            // 2. getPIDWait()
        } else {
            id = getPIDWait();

        }


        // check time to calcurate time
        long afterTime = System.currentTimeMillis();
        long secDiffTime = (long) ((afterTime - beforeTime) / (1000.0));
        if (secDiffTime <= programLife)
            // print thread is created
            System.out.println(this.seq + " created at Second " + secDiffTime + " (pid:" + id + ")");

        try {
            // print is sleep during threadlife
            Thread.sleep(1000 * threadLife);
        } catch (Exception e) {
        }
        if (secDiffTime + threadLife <= programLife)
            // print thread is destroyed
            System.out.println(this.seq + " destroyed at Second " + (secDiffTime + threadLife) + "(pid:" + id + ")");
        // release PID
        releasePID(id);

    }

    public static void main(String[] args) {
        // take variable from user
        Scanner sc = new Scanner(System.in);
        System.out.print(" - The number of threads created : ");
        int threadNumber = sc.nextInt();
        System.out.print(" - The number of life time of the program : ");
        programLife = sc.nextInt();
        System.out.print(" - The number of the life time of thread : ");
        threadLife = sc.nextInt();
        System.out.print(" - random term between thread creation(unit = 0.1second) (recommand=10) : ");
        int randomtime = sc.nextInt();
        System.out.println("If you write 20, then thread is created after random number between 0 to 2 second");
        System.out.print(" - 1. getPID() 2. getPIDWait() Write number(1,2) : ");
        checknum = sc.nextInt();
        System.out.println("Test program is initialized with " + threadNumber + " thread and " + programLife
                + " seconds,with the life time " + threadLife + " seconds of each thread");


        // check start time
        beforeTime = System.currentTimeMillis();
        // to interrupt thread make arraylist
        ArrayList<Thread> arr = new ArrayList();

        // start
        for (int i = 1; i <= threadNumber; i++) {
            long Time = System.currentTimeMillis();
            long secDiffTime = (long) ((Time - beforeTime) / (1000.0));
            if(secDiffTime > programLife) break;
            // sleep random time to create randomly
            try {
                Thread.sleep((int) (Math.random() * randomtime * 100));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Thread t = new Thread((Runnable) new Test(i));
            arr.add(t);
            t.start();

        }
        // end
        while (true) {
            // check time
            long afterTime = System.currentTimeMillis();
            long secDiffTime = (long) ((afterTime - beforeTime) / (1000.0));
            // program ends
            for(int i = 0;i<arr.size();i++) {
                arr.get(i).interrupt();
            }

            if (secDiffTime >= programLife) {
                System.out.println(programLife + " seconds has passed... Program ends");
                break;
            }
        }

        // show result more detail
        System.out.println();
        // you can see the overall PID number how much used
        System.out.println("check the number of PID using");
        for (int i = 0; i < 124; i++) {
            System.out.print("pid[" + (i + MIN_PID) + "]:" + pidcheck[i] + "\t");
            if (i % 10 == 9)
                System.out.println();
        }


        System.exit(0);

    }

}