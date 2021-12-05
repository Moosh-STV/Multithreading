import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MultiProdTaskQueueTest {
    private MultiProdTaskQueue taskQueue;

    @Test
    public void runTasksSuccessfully() throws InterruptedException {
        taskQueue = new MultiProdTaskQueue();
        runTasks();
        assertFalse(taskQueue.getQueue().isEmpty());
        Thread.sleep(1000);
        assertTrue(taskQueue.getQueue().isEmpty());
    }

    @Test
    public void runTasksSuccessfullyUsingNewThreadAfterQueueIsEmpty() throws InterruptedException {
        taskQueue = new MultiProdTaskQueue();
        runTasks();
        assertFalse(taskQueue.getQueue().isEmpty());
        Thread.sleep(1000);
        assertTrue(taskQueue.getQueue().isEmpty());

        runTasks();
        Thread.sleep(1000);
        assertTrue(taskQueue.getQueue().isEmpty());
    }

    @Test
    public void closeAndRunTasks() throws InterruptedException {
        taskQueue = new MultiProdTaskQueue();
        runTasks();
        assertFalse(taskQueue.getQueue().isEmpty());
        taskQueue.close();
        Thread.sleep(1000);
        assertTrue(taskQueue.getQueue().isEmpty());
    }

    @Test(expected = IllegalStateException.class)
    public void runTaskThrowsAfterClose() {
        taskQueue = new MultiProdTaskQueue();
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