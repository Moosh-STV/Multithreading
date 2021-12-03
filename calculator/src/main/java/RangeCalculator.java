import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RangeCalculator {

    public Map<String, BigDecimal> calculate(List<String> ranges) throws InterruptedException {
        Map<Thread, MyRunnable> threadToRunnable = initializeThreadsWithRanges(ranges);
        startThreads(threadToRunnable);
        return getResults(threadToRunnable);
    }

    private Map<Thread, MyRunnable> initializeThreadsWithRanges(List<String> ranges) {
        Map<Thread, MyRunnable> threadToRunnable = new HashMap<>(ranges.size());

        for (String range : ranges) {
            MyRunnable myRunnable = new MyRunnable(range);
            Thread thread = new Thread(myRunnable, range);
            threadToRunnable.put(thread, myRunnable);
        }
        return threadToRunnable;
    }

    private void startThreads(Map<Thread, MyRunnable> threadToRunnable) {
        for (Thread thread : threadToRunnable.keySet()) {
            thread.start();
        }
    }

    private Map<String, BigDecimal> getResults(Map<Thread, MyRunnable> threadToRunnable) throws InterruptedException {
        Map<String, BigDecimal> rangeToResult = new HashMap<>(threadToRunnable.size());

        for (Map.Entry<Thread, MyRunnable> entry : threadToRunnable.entrySet()) {
            entry.getKey().join();
            MyRunnable runnable = entry.getValue();
            rangeToResult.put(runnable.range, runnable.sum);
        }
        return rangeToResult;
    }

    private static class MyRunnable implements Runnable {
        private final String range;
        private BigDecimal sum;

        public MyRunnable(String range) {
            this.range = range;
            sum = BigDecimal.ZERO;
        }

        @Override
        public void run() {
            String[] split = range.split(",");
            long start = Long.parseLong(split[0]);
            long end = Long.parseLong(split[1]);

            for (long i = start; i < end; i++) {
                sum = sum.add(BigDecimal.valueOf(i * i));
            }
        }
    }
}
