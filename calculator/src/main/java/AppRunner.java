import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;

public class AppRunner {

    public static void main(String[] args) throws InterruptedException {
        RangeCalculator rangeCalculator = new RangeCalculator();
        Map<String, BigDecimal> rangeToResult = rangeCalculator.calculate(Arrays.asList(args));

        rangeToResult.forEach((key, value) -> System.out.println(key + " - " + value));
    }
}
