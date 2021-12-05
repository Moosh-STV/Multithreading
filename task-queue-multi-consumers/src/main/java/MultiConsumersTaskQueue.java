import java.util.*;

public class MultiConsumersTaskQueue implements TaskQueue {
    private static final int DEFAULT_NUM_OF_CONSUMERS = 1;

    private final Queue<Runnable> tasks;
    private final Set<ConsumerThread> availableConsumers;
    private final Set<ConsumerThread> busyConsumers;
    private boolean closed;

    public MultiConsumersTaskQueue() {
        this(DEFAULT_NUM_OF_CONSUMERS);
    }

    public MultiConsumersTaskQueue(int numOfConsumers) {
        closed = false;
        tasks = new ArrayDeque<>();
        availableConsumers = getConsumers(numOfConsumers);
        busyConsumers = new HashSet<>(numOfConsumers);
    }

    private Set<ConsumerThread> getConsumers(int numOfConsumers) {
        Set<ConsumerThread> consumerThreads = new HashSet<>(numOfConsumers);

        for (int i = 0; i < numOfConsumers; i++) {
            consumerThreads.add(new ConsumerThread(String.valueOf(i)));
        }
        return consumerThreads;
    }

    @Override
    public void runTask(Runnable r) {
        if (closed)
            throw new IllegalStateException("Can't add tasks to a closed queue");

        synchronized (tasks) {
            tasks.offer(r);
        }

        runQueueTasks();
    }

    @Override
    public void close() {
        closed = true;
        boolean allocatedConsumer;

        do {
            allocatedConsumer = runQueueTasks();
        } while (!allocatedConsumer);
    }

    private boolean runQueueTasks() {
        boolean allocatedConsumer = false;
        ConsumerThread consumerThread = null;

        synchronized (availableConsumers) {
            if (!availableConsumers.isEmpty()) {
                consumerThread = availableConsumers.iterator().next();
                availableConsumers.remove(consumerThread);
                allocatedConsumer = true;
            }
        }

        if (consumerThread != null) {
            synchronized (busyConsumers) {
                busyConsumers.add(consumerThread);
            }
            consumerThread.consumeTasks();
        }
        return allocatedConsumer;
    }

    private void notifyDone(ConsumerThread consumerThread) {
        synchronized (busyConsumers) {
            busyConsumers.remove(consumerThread);
        }

        synchronized (availableConsumers) {
            availableConsumers.add(consumerThread);
        }
    }

    //for testing
    Queue<Runnable> getQueue() {
        return tasks;
    }

    private class ConsumerThread implements Runnable {
        private final String id;
        private Runnable task;
        private Thread consumerThread;

        ConsumerThread(String id) {
            this.id = id;
            task = null;
            consumerThread = null;
        }

        private void consumeTasks() {
            if (consumerThread == null) {
                consumerThread = new Thread(this);
                consumerThread.start();
            }
        }

        @Override
        public void run() {
            while (true) {
                synchronized (tasks) {
                    if (!tasks.isEmpty()) {
                        task = tasks.poll();
                    } else break;
                }
                System.out.println("Running with thread id = " + id);
                task.run();
            }
            consumerThread = null;
            notifyDone(this);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ConsumerThread that = (ConsumerThread) o;
            return Objects.equals(id, that.id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }
}
