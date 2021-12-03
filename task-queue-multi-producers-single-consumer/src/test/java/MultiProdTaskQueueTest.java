import org.junit.Test;

public class MultiProdTaskQueueTest {
    private MultiProdTaskQueue taskQueue;

    @Test
    public void runTasksSuccessfully() {
        taskQueue = new MultiProdTaskQueue();
        taskQueue.runTask(()-> System.out.println("running task1"));
    }

    @Test(expected = IllegalStateException.class)
    public void runTaskThrowsAfterClose() {
        taskQueue = new MultiProdTaskQueue();
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        taskQueue.close();
        taskQueue.runTask(()-> System.out.println("running task1"));
    }
}