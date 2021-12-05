import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Queue;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PartitionedBasedConsumersTasksQueueTest {
    private static final int NUM_OF_CONSUMERS = 5;
    private PartitionedBasedConsumersTasksQueue taskQueue = new PartitionedBasedConsumersTasksQueue(NUM_OF_CONSUMERS);
    private final List<Queue<Runnable>> queues = taskQueue.getQueue();

    PartitionedBasedConsumersTasksQueue.ConsumerThread consumerThread = Mockito.mock(PartitionedBasedConsumersTasksQueue.ConsumerThread.class);
    PartitionedBasedConsumersTasksQueue.ConsumerThread consumerThread2 = Mockito.mock(PartitionedBasedConsumersTasksQueue.ConsumerThread.class);

    @Test
    public void runTasksSuccessfully() throws InterruptedException {
        addTasks();
        assertFalse(allEmpty(queues));
        Thread.sleep(500);
        assertTrue(allEmpty(queues));
    }

    @Test
    public void runTasksSuccessfullyUsingSameBucket() throws InterruptedException {
        taskQueue = new PartitionedBasedConsumersTasksQueue(NUM_OF_CONSUMERS, getConsumers(), str -> 0);

        addTasks(3);
        verify(consumerThread, times(3)).consumeTasks();
        verifyNoInteractions(consumerThread2);
    }

    @Test
    public void runTasksSuccessfullyUsingNewThreadAfterQueueIsEmpty() throws InterruptedException {
        addTasks();
        assertFalse(allEmpty(queues));
        Thread.sleep(500);
        assertTrue(allEmpty(queues));

        addTasks();
        Thread.sleep(500);
        assertTrue(allEmpty(queues));
    }

    private void addTasks() {
        addTasks(10);
    }

    private void addTasks(int numTasks) {
        for (int i = 0; i < numTasks; i++) {
            String msg = "running task" + i;
            taskQueue.addTask(() -> {
                System.out.println(msg);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }, msg);
        }
    }

    private boolean allEmpty(List<Queue<Runnable>> queues) {
        return queues.stream().allMatch(Collection::isEmpty);
    }

    private List<PartitionedBasedConsumersTasksQueue.ConsumerThread> getConsumers() {
        List<PartitionedBasedConsumersTasksQueue.ConsumerThread> consumerThreads = new ArrayList<>(NUM_OF_CONSUMERS);

        consumerThreads.add(consumerThread);
        for (int i = 1; i < NUM_OF_CONSUMERS; i++) {
            consumerThreads.add(consumerThread2);
        }
        return consumerThreads;
    }
}