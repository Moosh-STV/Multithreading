import java.util.*;
import java.util.function.Function;

public class PartitionedBasedConsumersTasksQueue {
    private static final int DEFAULT_NUM_OF_CONSUMERS = 1;

    private final List<Queue<Runnable>> partitionedTasks;
    private final List<ConsumerThread> consumers;
    private final Function<String, Integer> hashFunc;
    private final boolean closed;

    public PartitionedBasedConsumersTasksQueue() {
        this(DEFAULT_NUM_OF_CONSUMERS);
    }

    public PartitionedBasedConsumersTasksQueue(int numOfConsumers) {
        this(numOfConsumers, str -> Math.floorMod(str.hashCode(), numOfConsumers));
    }

    //for testing
    PartitionedBasedConsumersTasksQueue(int numOfConsumers, Function<String, Integer> hashFunc) {
        closed = false;
        partitionedTasks = Collections.nCopies(numOfConsumers, new ArrayDeque<>());
        consumers = getConsumers(numOfConsumers);
        this.hashFunc = hashFunc;
    }

    //for testing
    PartitionedBasedConsumersTasksQueue(int numOfConsumers, List<ConsumerThread> consumers, Function<String, Integer> hashFunc) {
        closed = false;
        partitionedTasks = Collections.nCopies(numOfConsumers, new ArrayDeque<>());
        this.consumers = consumers;
        this.hashFunc = hashFunc;
    }

    private List<ConsumerThread> getConsumers(int numOfConsumers) {
        List<ConsumerThread> consumerThreads = new ArrayList<>(numOfConsumers);

        for (int i = 0; i < numOfConsumers; i++) {
            consumerThreads.add(new ConsumerThread(i));
        }
        return consumerThreads;
    }

    public void addTask(Runnable r, String key) {
        if (closed)
            throw new IllegalStateException("Can't add tasks to a closed queue");

        int partitionIdx = getPartition(key);
        Queue<Runnable> partition = partitionedTasks.get(partitionIdx);
        synchronized (partition) {
            partition.offer(r);
        }

        runQueueTasks(partitionIdx);
    }

    private Runnable getNextTask(int consumerThreadId) {
        Queue<Runnable> partition = partitionedTasks.get(consumerThreadId);
        synchronized (partition) {
            if (!partition.isEmpty()) {
                return partition.poll();
            }
        }
        return null;
    }

    private int getPartition(String key) {
        return hashFunc.apply(key);
    }

    private void runQueueTasks(int partitionIdx) {
        ConsumerThread consumerThread = consumers.get(partitionIdx);
        consumerThread.consumeTasks();
    }

    //for testing
    List<Queue<Runnable>> getQueue() {
        return partitionedTasks;
    }

    class ConsumerThread implements Runnable {
        private final int id;
        private Runnable task;
        private Thread consumerThread;

        ConsumerThread(int id) {
            this.id = id;
            task = null;
            consumerThread = null;
        }

        void consumeTasks() {
            if (consumerThread == null) {
                consumerThread = new Thread(this);
                consumerThread.start();
            }
        }

        @Override
        public void run() {
            while ((task = getNextTask(id)) != null) {
                System.out.println("Running with thread id = " + id);
                task.run();
            }
            consumerThread = null;
        }
    }
}
