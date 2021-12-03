import java.util.ArrayDeque;
import java.util.Queue;

public class MultiProdTaskQueue implements TaskQueue {
    private final Queue<Runnable> tasks;
    private final ConsumerThread consumer;
    private boolean first;
    private volatile boolean closed;

    public MultiProdTaskQueue() {
        closed = false;
        tasks = new ArrayDeque<>();
        consumer = new ConsumerThread(this);
        first = true;
    }

    @Override
    public void runTask(Runnable r) {
        if (first) {
            first = false;
            consumer.start();
        }
        if (closed)
            throw new IllegalStateException("Can't add tasks to a closed queue");

        synchronized (tasks) {
            tasks.offer(r);
            notifyAll();
        }
    }

    @Override
    public void close() {
        closed = true;
    }

    public Queue<Runnable> getTasks() {
        return tasks;
    }

    public boolean isClosed() {
        return closed;
    }
}
