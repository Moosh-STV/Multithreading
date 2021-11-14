import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RangeCalculator {

    public Map<String, BigDecimal> calculate(List<String> ranges) throws InterruptedException {
        Map<Thread, MyRunnable> threadToRunnable = new HashMap<>(ranges.size());
        Map<String, BigDecimal> rangeToResult = new HashMap<>(ranges.size());

        for (String range : ranges) {
            MyRunnable myRunnable = new MyRunnable(range);
            Thread thread = new Thread(myRunnable);
            threadToRunnable.put(thread, myRunnable);
            thread.start();
        }

        for (Map.Entry<Thread, MyRunnable> entry : threadToRunnable.entrySet()) {
            entry.getKey().join();
            MyRunnable runnable = entry.getValue();
            rangeToResult.put(runnable.range, runnable.sum);
        }
        return rangeToResult;
    }

    public static class MyRunnable implements Runnable {
        private final String range;
        private BigDecimal sum;

        public MyRunnable(String range) {
            this.range = range;
            sum = BigDecimal.ZERO;
        }

        @Override
        public void run() {
            String[] split = range.split(",");
            int start = Integer.parseInt(split[0]);
            int end = Integer.parseInt(split[1]);

            for (long i = start; i < end; i++) {
                sum = sum.add(BigDecimal.valueOf(i * i));
            }
        }
    }
}
