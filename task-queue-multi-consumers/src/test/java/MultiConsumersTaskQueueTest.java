import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MultiConsumersTaskQueueTest {

    private static final int NUM_OF_CONSUMERS = 5;
    private final MultiConsumersTaskQueue taskQueue = new MultiConsumersTaskQueue(NUM_OF_CONSUMERS);

    @Test
    public void runTasksSuccessfully() throws InterruptedException {
        runTasks();
        assertFalse(taskQueue.getQueue().isEmpty());
        Thread.sleep(500);
        assertTrue(taskQueue.getQueue().isEmpty());
    }

    @Test
    public void runTasksSuccessfullyUsingNewThreadAfterQueueIsEmpty() throws InterruptedException {
        runTasks();
        assertFalse(taskQueue.getQueue().isEmpty());
        Thread.sleep(500);
        assertTrue(taskQueue.getQueue().isEmpty());

        runTasks();
        Thread.sleep(500);
        assertTrue(taskQueue.getQueue().isEmpty());
    }

    @Test
    public void closeAndRunTasks() throws InterruptedException {
        runTasks();
        assertFalse(taskQueue.getQueue().isEmpty());
        taskQueue.close();
        Thread.sleep(500);
        assertTrue(taskQueue.getQueue().isEmpty());
    }

    @Test(expected = IllegalStateException.class)
    public void runTaskThrowsAfterClose() {
        taskQueue.close();
        taskQueue.runTask(() -> System.out.println("running task1"));
    }

    private void runTasks() {
        for (int i = 0; i < 10; i++) {
            String msg = "running task" + i;
            taskQueue.runTask(() -> {
                System.out.println(msg);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
    }
}