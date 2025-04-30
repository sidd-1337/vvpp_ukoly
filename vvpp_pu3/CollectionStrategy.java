

/**
 * Defines the strategy for a collector to determine how long to rest.
 * The rest time directly translates to the collector's strength for the next collection attempt.
 */
@FunctionalInterface
public interface CollectionStrategy {
    /**
     * Calculates the rest time in milliseconds.
     *
     * @return The time to rest in milliseconds.
     */
    int calculateRestTimeMillis();
}
