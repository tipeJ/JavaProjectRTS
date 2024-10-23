class ThreadTest extends Thread {
    private String threadName;

    public ThreadTest(String name) {
        this.threadName = name;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < 5; i++) {
                System.out.println(threadName + " is running: iteration " + (i + 1));
                Thread.sleep(1000); // Sleep for 1 second
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Creating and starting multiple threads
        ThreadTest thread1 = new ThreadTest("Thread 1");
        ThreadTest thread2 = new ThreadTest("Thread 2");
        ThreadTest thread3 = new ThreadTest("Thread 3");

        thread1.start();
        thread2.start();
        thread3.start();
    }
}
