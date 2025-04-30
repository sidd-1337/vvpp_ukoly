public class RandomTimeCollector extends Collector {
    private final int minSleepTime;
    private final int maxSleepTime;

    public RandomTimeCollector(String name, TreasureQueue treasureQueue, int minSleepTime, int maxSleepTime) {
        super(name, treasureQueue);
        this.minSleepTime = minSleepTime;
        this.maxSleepTime = maxSleepTime;
    }

    @Override
    protected int determineSleepTime() {
        return random.nextInt(maxSleepTime - minSleepTime + 1) + minSleepTime;
    }
} 