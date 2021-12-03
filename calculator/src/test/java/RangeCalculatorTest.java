import org.junit.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class RangeCalculatorTest {

    @Test
    public void calculateRangesSuccessfully() throws InterruptedException {
        RangeCalculator rangeCalculator = new RangeCalculator();
        Map<String, BigDecimal> result = rangeCalculator.calculate(List.of("-1,10"));
        Map.Entry<String, BigDecimal> entry = result.entrySet().iterator().next();

        assertEquals("-1,10", entry.getKey());
        assertEquals(286, entry.getValue().intValue());
        assertEquals(1, result.size());
    }
}