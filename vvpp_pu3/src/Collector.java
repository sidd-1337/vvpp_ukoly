import java.util.Random;

public abstract class Collector extends Thread {
    protected final TreasureQueue treasureQueue;
    protected final Random random = new Random();
    protected final String name;
    protected int totalTreasureValue = 0;
    protected int treasuresCollected = 0;
    protected boolean running = true;

    public Collector(String name, TreasureQueue treasureQueue) {
        this.name = name;
        this.treasureQueue = treasureQueue;
        setName(name);
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

    protected abstract int determineSleepTime();

    public int getTotalTreasureValue() {
        return totalTreasureValue;
    }

    public int getTreasuresCollected() {
        return treasuresCollected;
    }

    public String getCollectorName() {
        return name;
    }

    public void stopCollector() {
        running = false;
        interrupt();
    }
} 