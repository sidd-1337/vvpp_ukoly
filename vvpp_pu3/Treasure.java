
/**
 * Represents a treasure with a specific value.
 */
public class Treasure {
    private final int value;

    public Treasure(int value) {
        if (value < 0) {
            throw new IllegalArgumentException("Treasure value cannot be negative.");
        }
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Treasure{value=" + value + '}';
    }
}
