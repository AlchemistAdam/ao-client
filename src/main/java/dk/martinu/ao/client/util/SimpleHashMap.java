/*
 * Copyright (c) 2023, Adam Martinu. All rights reserved. Altering or
 * removing copyright notices or this file header is not allowed.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package dk.martinu.ao.client.util;

import org.jetbrains.annotations.*;

import java.util.Objects;

/**
 * Simplified implementation of a hash map. Does not allow the use of
 * {@code null} keys or values.
 * <p>
 * <b>NOTE:</b> this class does not implement {@code Collection} and is not
 * interoperable with the Collections Framework.
 *
 * @param <K> runtime type of key objects
 * @param <V> runtime type of value objects
 * @author Adam Martinu
 * @version 1.0, 2023-06-17
 * @since 1.0
 */
public final class SimpleHashMap<K, V> {

    /**
     * The default initial capacity.
     */
    private static final int DEFAULT_CAPACITY = 32;
    /**
     * The default load factor.
     */
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    /**
     * Inserts the specified bucket into the specified table, rehashing all
     * entries.
     *
     * @param table  the table to insert into
     * @param bucket the bucket to insert
     */
    private static <K, V> void insertBucketIntoTable(final Entry<K, V>[] table, final Entry<K, V> bucket) {
        // entry to insert
        Entry<K, V> entry;
        // temporary variable for the next entry
        Entry<K, V> next = bucket;
        // loop until all entries in bucket have been rehashed
        while ((entry = next) != null) {
            // rehash entry
            final int hash = entry.key.hashCode() & (table.length - 1);
            // append to bucket if it already exists
            if (table[hash] != null) {
                Entry<K, V> last = table[hash];
                while (last.next != null)
                    last = last.next;
                last.next = entry;
            }
            // assign entry as new bucket
            else
                table[hash] = entry;
            // advance to next entry
            next = entry.next;
            // entry was appended or is new bucket; reset entry.next
            entry.next = null;
        }
    }

    /**
     * Table to hold buckets.
     */
    @SuppressWarnings("unchecked")
    private Entry<K, V>[] table = (Entry<K, V>[]) new Entry[DEFAULT_CAPACITY];
    /**
     * Load factor to determine how many lists the map can contain before it
     * is resized.
     *
     * @see #max
     * @see #resize()
     */
    private final float loadFactor = DEFAULT_LOAD_FACTOR;
    /**
     * Maximum number of buckets the map can contain before it is resized.
     *
     * @see #resize()
     */
    private int max = (int) (table.length * loadFactor);
    /**
     * Current number of entries in the map.
     */
    private int size = 0;

    /**
     * Returns the value for the specified key, or {@code null}.
     *
     * @throws NullPointerException if {@code key} is {@code null}
     */
    @Contract(pure = true)
    @Nullable
    public V get(@NotNull final K key) {
        Objects.requireNonNull(key, "key is null");
        Entry<K, V> entry = table[key.hashCode() & (table.length - 1)];
        if (entry != null) {
            do {
                if (entry.key.equals(key))
                    return entry.value;
            }
            while ((entry = entry.next) != null);
        }
        return null;
    }

    /**
     * Maps the specified value to the specified key. If the map already
     * contains an entry with the specified key, then its value is replaced.
     *
     * @param key   the key
     * @param value the value
     * @throws NullPointerException if {@code key} or {@code value} is
     *                              {@code null}
     */
    @Contract(mutates = "this")
    public void put(@NotNull final K key, @NotNull final V value) {
        Objects.requireNonNull(key, "key is null");
        Objects.requireNonNull(value, "value is null");
        if (++size > max)
            resize();
        final int hash = key.hashCode() & (table.length - 1);
        Entry<K, V> entry = table[hash];
        // add value to bucket if it already exists
        if (entry != null) {
            Entry<K, V> prev = null;
            do {
                if (entry.key.equals(key)) {
                    entry.value = value;
                    break;
                }
                else
                    prev = entry;
            }
            while ((entry = entry.next) != null);
            // create new entry if value was not assigned
            if (entry == null)
                prev.next = new Entry<>(key, value);
        }
        // create new bucket
        else
            table[hash] = new Entry<>(key, value);
    }

    /**
     * Doubles the capacity of the map and rehashes all buckets.
     */
    @Contract(mutates = "this")
    private void resize() {
        // create new table with double size
        //noinspection unchecked
        final Entry<K, V>[] newTable = (Entry<K, V>[]) new Entry[table.length << 1];
        // transfer bucket entries into new table
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

    /**
     * Key-value entry class. Keys and values are guaranteed to not be
     * {@code null}. Entries also act as buckets, each one having a
     * {@code next} field that can point to the next entry in the bucket.
     *
     * @param <K> runtime type of key objects
     * @param <V> runtime type of value objects
     */
    public static final class Entry<K, V> {

        /**
         * The key.
         */
        @NotNull
        private final K key;
        /**
         * The value.
         */
        @NotNull
        private V value;
        /**
         * The next entry in the bucket, can be {@code null}.
         */
        @Nullable
        private Entry<K, V> next = null;

        /**
         * Constructs a new entry with the specified key and value.
         *
         * @param key   the key
         * @param value the value
         * @throws NullPointerException if {@code key} or {@code value} is
         *                              {@code null}
         */
        public Entry(@NotNull final K key, @NotNull final V value) {
            this.key = Objects.requireNonNull(key, "key is null");
            this.value = Objects.requireNonNull(value, "value is null");
        }
    }
}
