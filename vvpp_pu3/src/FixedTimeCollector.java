public class FixedTimeCollector extends Collector {
    private final int fixedSleepTime;

    public FixedTimeCollector(String name, TreasureQueue treasureQueue, int fixedSleepTime) {
        super(name, treasureQueue);
        this.fixedSleepTime = fixedSleepTime;
    }

    @Override
    protected int determineSleepTime() {
        return fixedSleepTime;
    }
} 