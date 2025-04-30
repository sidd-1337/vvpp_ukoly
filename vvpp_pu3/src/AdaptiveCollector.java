import java.util.ArrayList;
import java.util.List;

public class AdaptiveCollector extends Collector {
    private final int minSleepTime;
    private final int maxSleepTime;
    private final List<Integer> successfulStrengths = new ArrayList<>();
    private int defaultSleepTime;

    public AdaptiveCollector(String name, TreasureQueue treasureQueue, int minSleepTime, int maxSleepTime, int defaultSleepTime) {
        super(name, treasureQueue);
        this.minSleepTime = minSleepTime;
        this.maxSleepTime = maxSleepTime;
        this.defaultSleepTime = defaultSleepTime;
    }

    @Override
    protected int determineSleepTime() {
        if (successfulStrengths.isEmpty()) {
            return defaultSleepTime;
        }

        int lastSuccessfulStrength = successfulStrengths.get(successfulStrengths.size() - 1);
        int variation = (int) (lastSuccessfulStrength * 0.2);
        
        int adjustedSleepTime = lastSuccessfulStrength + random.nextInt(2 * variation + 1) - variation;
        
        adjustedSleepTime = Math.max(minSleepTime, Math.min(maxSleepTime, adjustedSleepTime));
        
        return adjustedSleepTime;
    }

    @Override
    public void run() {
        while (running) {
            try {
                int sleepTime = determineSleepTime();
                Thread.sleep(sleepTime);
                
                int strength = sleepTime;
                
                Treasure treasure = treasureQueue.takeTreasure(strength);
                if (treasure != null) {
                    totalTreasureValue += treasure.getValue();
                    treasuresCollected++;
                    successfulStrengths.add(strength);
                    System.out.println(name + " collected " + treasure + " (strength: " + strength + ")");
                } else {
                    System.out.println(name + " found no suitable treasure (strength: " + strength + ")");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
} 