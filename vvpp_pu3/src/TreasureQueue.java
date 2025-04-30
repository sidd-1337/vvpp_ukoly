import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TreasureQueue {
    private final Queue<Treasure> treasures = new LinkedList<>();
    private final Lock lock = new ReentrantLock();
    private final Condition notEmpty = lock.newCondition();

    public void addTreasure(Treasure treasure) {
        lock.lock();
        try {
            treasures.add(treasure);
            System.out.println("Volcano produced " + treasure);
            notEmpty.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public Treasure takeTreasure(int maxValue) {
        lock.lock();
        try {
            while (treasures.isEmpty()) {
                try {
                    notEmpty.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return null;
                }
            }

            for (Treasure treasure : treasures) {
                if (treasure.getValue() <= maxValue) {
                    treasures.remove(treasure);
                    return treasure;
                }
            }
            
            return null;
        } finally {
            lock.unlock();
        }
    }

    public boolean isEmpty() {
        lock.lock();
        try {
            return treasures.isEmpty();
        } finally {
            lock.unlock();
        }
    }

    public int size() {
        lock.lock();
        try {
            return treasures.size();
        } finally {
            lock.unlock();
        }
    }
} 