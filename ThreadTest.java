public class ThreadTest extends Thread {
    private int counter = 0;

    @Override
    public void run() {
        while (true) {
            System.out.println(getName() + " : " + counter++);
            
            try {
                Thread.sleep(100); 
            } catch (InterruptedException e) {
                System.err.println(getName() + " was interrupted.");
                return;  // Exit the thread if interrupted
            }
        }
    }

    public static void main(String[] args) {
        // Create and start multiple instances of ThreadTest
        for (int i = 0; i < 5; i++) {
            ThreadTest thread = new ThreadTest();
            thread.start();
        }
    }
}
