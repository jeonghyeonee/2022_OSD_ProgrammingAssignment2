import java.util.ArrayList;
import java.util.Scanner;

public class Test implements Runnable, PIDManager{

    public static long startTime;
    public static int lifetimeProgram;
    public static int lifetimeThread;

    int num;

    public Test(int num){
        this.num = num;
    }

    @Override
    public int getPID() {
        return 0;
    }

    @Override
    public int getPIDWait() {
        return 0;
    }

    @Override
    public void releasePID(int pid) {

    }

    @Override
    public void run() {
        int pid = 0;

        long finishTime = System.currentTimeMillis();
        long differ = (long) ((finishTime - startTime)/(1000.0));

        if(differ <= lifetimeProgram){
            System.out.println(pid + " created at Second");
        }

        if(differ+lifetimeThread <= lifetimeProgram){
            System.out.println(pid + " destroyed at Second");
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

        System.out.println("Test program is initialized with " + threadNum + " thread and " + lifetimeProgram + " seconds, with life time "
                + lifetimeThread +" seconds of each thread");


        startTime = System.currentTimeMillis();

        ArrayList<Thread> arr = new ArrayList();

        for (int i=1; i<=threadNum; i++){
            long Time = System.currentTimeMillis();
            long differ = (long) ((Time-startTime)/(1000.0));

            if(differ > lifetimeProgram){
                break;
            }
            try{
                Thread.sleep((int) (Math.random() * 1000));
            } catch (InterruptedException e){
                e.printStackTrace();
            }

            Thread t = new Thread(new Test(i));
            arr.add(t);
            t.start();
        }

        while(true){
            long finishTime = System.currentTimeMillis();
            long differ = (long)((finishTime - startTime)/(1000.0));

            for(int i=0; i<arr.size(); i++){
                arr.get(i).interrupt();
            }

            if(differ >= lifetimeProgram){
                System.out.println(lifetimeProgram+" seconds has passed... Program ends");
                break;
            }
        }

        System.out.println();
//        System.out.println("check the number of PID using");

        System.exit(0);



    }

}
