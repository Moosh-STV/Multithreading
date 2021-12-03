import java.util.Queue;

class ConsumerThread implements Runnable {
    private final MultiProdTaskQueue multiProdTaskQueue;
    private final Queue<Runnable> tasks;
    private Runnable task;
    private final Thread consumer;

    ConsumerThread(MultiProdTaskQueue multiProdTaskQueue) {
        this.multiProdTaskQueue = multiProdTaskQueue;
        tasks = multiProdTaskQueue.getTasks();
        task = null;
        consumer = new Thread(this);
//            consumer.start();
    }

    @Override
    public void run() {
        while (!multiProdTaskQueue.isClosed()) {
            synchronized (tasks) {
                if (noTasks()) {
                    try {
                        System.out.println("waiting");
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    task = tasks.poll();
                }
            }
            task.run();
        }
        while (!tasks.isEmpty()) {
            tasks.poll().run();
        }
    }

    private boolean noTasks() {
        //TODO single consumer. do i need to sync?
        return tasks.isEmpty();
    }

    private void sleep(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void start() {
        consumer.start();
    }
}
