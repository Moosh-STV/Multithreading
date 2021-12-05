public interface TaskQueue {
    void runTask(Runnable r);

    void close();
}
