import java.util.Random;

public class Volcano extends Thread {
    private final TreasureQueue treasureQueue;
    private final Random random = new Random();
    private final int minValue;
    private final int maxValue;
    private final int minDelay;
    private final int maxDelay;
    private boolean running = true;

    public Volcano(TreasureQueue treasureQueue, int minValue, int maxValue, int minDelay, int maxDelay) {
        this.treasureQueue = treasureQueue;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.minDelay = minDelay;
        this.maxDelay = maxDelay;
        setName("Volcano");
    }

    @Override
    public void run() {
        while (running) {
            try {
                int delay = random.nextInt(maxDelay - minDelay + 1) + minDelay;
                Thread.sleep(delay);
                
                int value = random.nextInt(maxValue - minValue + 1) + minValue;
                Treasure treasure = new Treasure(value);
                
                treasureQueue.addTreasure(treasure);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void stopVolcano() {
        running = false;
        interrupt();
    }
} 