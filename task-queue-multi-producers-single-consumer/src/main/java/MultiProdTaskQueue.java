import java.util.ArrayDeque;
import java.util.Queue;

public class MultiProdTaskQueue implements TaskQueue {
    private final Queue<Runnable> tasks;
    private final ConsumerThread consumer;
    private boolean closed;

    public MultiProdTaskQueue() {
        closed = false;
        tasks = new ArrayDeque<>();
        consumer = new ConsumerThread();
    }

    @Override
    public void runTask(Runnable r) {
        if (closed)
            throw new IllegalStateException("Can't add tasks to a closed queue");

        synchronized (tasks) {
            tasks.offer(r);
        }

        consumer.consumeTasks();
    }

    @Override
    public void close() {
        closed = true;
        consumer.consumeTasks();
    }

    //for testing
    Queue<Runnable> getQueue() {
        return tasks;
    }

    private class ConsumerThread implements Runnable {
        private Runnable task;
        private Thread consumer;

        ConsumerThread() {
            task = null;
            consumer = null;
        }

        private void consumeTasks() {
            if (consumer == null) {
                consumer = new Thread(this);
                consumer.start();
            }
        }

        @Override
        public void run() {
            while (true) {
                synchronized (tasks) {
                    if (!tasks.isEmpty()) {
                        task = tasks.poll();
                    }else break;
                }
                task.run();
            }
            consumer = null;
        }
    }
}