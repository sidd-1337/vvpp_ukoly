public class Treasure {
    private final int value;

    public Treasure(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Treasure(value=" + value + ")";
    }
} 