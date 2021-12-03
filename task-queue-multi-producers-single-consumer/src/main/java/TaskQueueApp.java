public class TaskQueueApp {

    /*
    Implement a multi-producer, single consumer task queue.

    runTask can be executed from any thread. Tasks are handled in FIFO order.

    Once close has been called:
    Execute remaining tasks


    Implementation notes:
    When it comes to concurrency, you may only use Java's Thread class
    Robustness is essential
    Unit testing is a requirement (namely, mock Thread)

     */
    public static void main(String[] args) {
        MultiProdTaskQueue multiProdTaskQueue = new MultiProdTaskQueue();
    }
}
