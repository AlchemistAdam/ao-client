package dk.martinu.ao.client.event;

import org.jetbrains.annotations.*;

import java.util.Arrays;
import java.util.Objects;


/**
 * Hash map implementation for storing {@link KeyAction actions} and retrieving
 * {@link KeyActionList lists} containing actions for a specific key code.
 *
 * @author Adam Martinu
 * @version 1.0, 2023-02-05
 * @since 1.0
 */
public final class KeyMap {

    private static final int DEFAULT_CAPACITY = 32;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    /**
     * Returns a new bucket that is the result of appending the specified
     * list to the specified bucket.
     * <p>
     * <b>NOTE:</b> this method discards all elements in {@code bucket}.
     *
     * @param bucket the original bucket
     * @param list   the list to append
     * @return a new bucket containing all lists in {@code bucket} and
     * {@code list}
     */
    private static KeyActionList[] appendListToBucket(final KeyActionList[] bucket,
            @NotNull final KeyActionList list) {
        final KeyActionList[] newBucket = new KeyActionList[bucket.length + 1];
        System.arraycopy(bucket, 0, newBucket, 0, bucket.length);
        Arrays.fill(bucket, null);
        newBucket[bucket.length] = list;
        return newBucket;
    }

    /**
     * Returns a hash code for the specified key code.
     */
    private static int hash(final int keyCode) {
        final int h;
        return (h = keyCode * 31) ^ h >>> 16;
    }

    /**
     * Inserts the specified bucket into the specified table.
     * <p>
     * If {@code bucket} contains only a single list, then the bucket will be
     * reused if the position in the table is empty. Otherwise, the list will
     * be appended to the bucket at that position. If {@code bucket} contains
     * multiple lists, then each list is inserted separately into the table.
     * <p>
     * <b>NOTE:</b> this method discards all elements in {@code bucket}.
     *
     * @param table  the table to insert into
     * @param bucket the bucket to insert
     * @see #appendListToBucket(KeyActionList[], KeyActionList)
     * @see #insertListIntoTable(KeyActionList[][], KeyActionList)
     */
    private static void insertBucketIntoTable(final KeyActionList[][] table, final KeyActionList[] bucket) {
        // attempt to reuse single element buckets
        if (bucket.length == 1) {
            final KeyActionList list = bucket[0];
            final int hash = hash(list.keyCode) & (table.length - 1);
            if (table[hash] != null)
                table[hash] = appendListToBucket(table[hash], list);
            else
                table[hash] = bucket;
            bucket[0] = null;
        }
        // insert each list separately
        else
            for (int i = 0; i < bucket.length; i++) {
                insertListIntoTable(table, bucket[i]);
                bucket[i] = null;
            }
    }

    /**
     * Inserts the specified list into the specified table.
     * <p>
     * If the position in the table is empty, then a new bucket is created.
     * Otherwise, {@code list} is appended to the existing bucket.
     *
     * @param table the table to insert into
     * @param list  the list to insert
     * @see #appendListToBucket(KeyActionList[], KeyActionList)
     */
    private static void insertListIntoTable(final KeyActionList[][] table, @NotNull final KeyActionList list) {
        final int hash = hash(list.keyCode) & (table.length - 1);
        if (table[hash] != null)
            table[hash] = appendListToBucket(table[hash], list);
        else
            table[hash] = new KeyActionList[] {list};
    }

    /**
     * Table to hold {@link KeyActionList} buckets.
     */
    private KeyActionList[][] table = new KeyActionList[DEFAULT_CAPACITY][];
    /**
     * Load factor to determine how many lists the map can contain before it
     * is resized.
     *
     * @see #max
     * @see #resize()
     */
    private final float loadFactor = DEFAULT_LOAD_FACTOR;
    /**
     * Maximum number of lists the map can contain before it is resized.
     *
     * @see #resize()
     */
    private int max = (int) (table.length * loadFactor);
    /**
     * Current number of lists in the map.
     */
    private int size = 0;

    /**
     * Returns the list for the specified key code, or {@code null}.
     */
    @Contract(pure = true)
    @Nullable
    public KeyActionList getList(final int keyCode) {
        final KeyActionList[] bucket = table[hash(keyCode) & (table.length - 1)];
        if (bucket != null)
            for (final KeyActionList list : bucket)
                if (list.keyCode == keyCode)
                    return list;
        return null;
    }

    /**
     * Stores the specified action inside a {@link KeyActionList list} in
     * the map for the specified key code. All actions inserted with the same
     * key code will be stored in the same list.
     *
     * @param action  the action to store
     * @param keyCode the key code to identify the list that will store the
     *                action
     * @throws NullPointerException if {@code action} is {@code null}
     * @see #getList(int)
     */
    public void insert(@NotNull final KeyAction action, final int keyCode) {
        Objects.requireNonNull(action, "action is null");
        if (++size > max)
            resize();

        final int hash = hash(keyCode) & (table.length - 1);
        final KeyActionList[] bucket = table[hash];
        // bucket already exists
        if (bucket != null) {
            // add action to list if it exists
            for (final KeyActionList list : bucket) {
                if (list.keyCode == keyCode) {
                    list.add(action);
                    return;
                }
            }
            // create new list and append to existing bucket
            table[hash] = appendListToBucket(bucket, new KeyActionList(keyCode, action));
        }
        // create new bucket and list
        else
            table[hash] = new KeyActionList[] {new KeyActionList(keyCode, action)};
    }

    /**
     * Doubles the capacity of the map and rehashes all buckets.
     */
    private void resize() {
        // create new table with double size
        final KeyActionList[][] newTable = new KeyActionList[table.length << 1][];

        // transfer buckets into new table
        for (int i = 0; i < table.length; i++) {
            if (table[i] != null) {
                insertBucketIntoTable(newTable, table[i]);
                table[i] = null;
            }
        }

        // assign new table and threshold
        table = newTable;
        max = (int) (table.length * loadFactor);
    }
}
