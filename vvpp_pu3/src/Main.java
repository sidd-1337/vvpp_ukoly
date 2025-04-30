import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Main {
    private static final int SIMULATION_TIME_MINUTES = 2;
    private static final int VOLCANO_MIN_VALUE = 0;
    private static final int VOLCANO_MAX_VALUE = 5000;
    private static final int VOLCANO_MIN_DELAY_MS = 0;
    private static final int VOLCANO_MAX_DELAY_MS = 5000;

    private static final int STATUS_UPDATE_INTERVAL_SECONDS = 10;

    public static void main(String[] args) {
        System.out.println("Starting treasure collection simulation...");
        System.out.println("Simulation will run for " + SIMULATION_TIME_MINUTES + " minutes");
        
        TreasureQueue treasureQueue = new TreasureQueue();
        
        Volcano volcano = new Volcano(
            treasureQueue, 
            VOLCANO_MIN_VALUE, 
            VOLCANO_MAX_VALUE, 
            VOLCANO_MIN_DELAY_MS, 
            VOLCANO_MAX_DELAY_MS
        );
        
        List<Collector> collectors = new ArrayList<>();
        
        collectors.add(new FixedTimeCollector("Karel", treasureQueue, 2000));
        
        collectors.add(new RandomTimeCollector("Lucka", treasureQueue, 1000, 3000));
        
        collectors.add(new AdaptiveCollector("Tomáš", treasureQueue, 500, 4000, 1500));
        
        volcano.start();
        
        for (Collector collector : collectors) {
            collector.start();
        }
        
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            System.out.println("\n--- STATUS UPDATE ---");
            for (Collector collector : collectors) {
                System.out.println(collector.getCollectorName() + 
                                  ": collected " + collector.getTreasuresCollected() + 
                                  " treasures worth " + collector.getTotalTreasureValue() + 
                                  " points");
            }
            System.out.println("---------------------\n");
        }, STATUS_UPDATE_INTERVAL_SECONDS, STATUS_UPDATE_INTERVAL_SECONDS, TimeUnit.SECONDS);
        
        try {
            Thread.sleep(TimeUnit.MINUTES.toMillis(SIMULATION_TIME_MINUTES));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        System.out.println("\n--- SIMULATION ENDED ---");
        volcano.stopVolcano();
        for (Collector collector : collectors) {
            collector.stopCollector();
        }
        scheduler.shutdown();
        
        System.out.println("\n--- FINAL RESULTS ---");
        Collector winner = null;
        for (Collector collector : collectors) {
            System.out.println(collector.getCollectorName() + 
                              ": collected " + collector.getTreasuresCollected() + 
                              " treasures worth " + collector.getTotalTreasureValue() + 
                              " points");
            
            if (winner == null || collector.getTotalTreasureValue() > winner.getTotalTreasureValue()) {
                winner = collector;
            }
        }
        
        System.out.println("\nWinner: " + winner.getCollectorName() + 
                          " with " + winner.getTotalTreasureValue() + 
                          " points from " + winner.getTreasuresCollected() + 
                          " treasures!");
    }
} 