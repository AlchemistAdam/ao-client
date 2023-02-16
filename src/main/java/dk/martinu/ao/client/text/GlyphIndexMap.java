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
package dk.martinu.ao.client.text;

import org.jetbrains.annotations.*;

import java.util.Arrays;
import java.util.Objects;

/**
 * Hash map implementation used by {@link Font fonts} for storing and
 * retrieving {@link Glyph} indices.
 *
 * @author Adam Martinu
 * @version 1.0, 2023-02-014
 * @since 1.0
 */
final class GlyphIndexMap {

    private static final int DEFAULT_CAPACITY = 32;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    /**
     * Adds the contents of specified bucket to the specified table.
     * <p>
     * If the bucket only contains a single entry, then it is reused if the
     * position in the table is empty. Otherwise, the entry is added to the
     * bucket at that position. If the bucket contains multiple entries, then
     * each entry is added separately to the table.
     * <p>
     * <b>NOTE:</b> this method changes the entry links in {@code bucket}, and
     * it should not be used after this call.
     *
     * @param table  the table to insert into
     * @param bucket the bucket to insert
     * @see #addEntryToBucket(Entry, Entry)
     */
    private static void addBucketToTable(final Entry[] table, @NotNull final Entry bucket) {
        // attempt to reuse single element buckets
        if (bucket.next == null) {
            final int hash = Glyph.hash(bucket.chars) & (table.length - 1);
            if (table[hash] != null)
                table[hash] = addEntryToBucket(table[hash], bucket);
            else
                table[hash] = bucket;
        }
        // insert each entry separately
        else {
            Entry entry = bucket;
            Entry next;
            int hash;
            do {
                next = entry.next;
                hash = Glyph.hash(entry.chars) & (table.length - 1);
                if (table[hash] != null)
                    table[hash] = addEntryToBucket(table[hash], entry);
                else {
                    entry.next = null;
                    table[hash] = entry;
                }
            }
            while ((entry = next) != null);
        }
    }

    /**
     * Adds the specified entry to the specified bucket and returns a reference
     * to the bucket.
     * <p>
     * <b>NOTE:</b> this method changes the entry links in {@code bucket}, and
     * it should not be used after this call.
     *
     * @param bucket the bucket to add the entry to
     * @param entry  the entry to add
     * @return a bucket with the added entry
     */
    @NotNull
    private static Entry addEntryToBucket(@NotNull final Entry bucket, @NotNull final Entry entry) {
        // current entry in bucket
        Entry cursor = bucket;
        // last iterated entry
        Entry last = null;

        // cursor character
        char cc;
        // glyph represents a single character
        if (entry.chars.length == 1) {
            final char c = entry.chars[0];
            do {
                cc = cursor.chars[0];
                if (cc < c) {
                    last = cursor;
                    cursor = cursor.next;
                }
                else
                    break;
            }
            while (cursor != null);
        }
        // glyph represents a ligature
        else {
            // ligature index
            int li = 0;
            do {
                if (li < cursor.chars.length) {
                    cc = cursor.chars[li];
                    if (cc < entry.chars[li]) {
                        last = cursor;
                        cursor = cursor.next;
                    }
                    else if (cc == entry.chars[li] && cursor.chars.length <= entry.chars.length)
                        if (cursor.chars.length < entry.chars.length)
                            li++;
                        else
                            break;
                    else
                        break;
                }
            }
            while (cursor != null);
        }

        // entry becomes new first link in bucket
        if (last == null) {
            if (Arrays.equals(cursor.chars, entry.chars))
                entry.next = cursor.next;
            else
                entry.next = cursor;
            return entry;
        }
        // bucket retains first entry link
        else {
            last.next = entry;
            if (cursor != null && Arrays.equals(cursor.chars, entry.chars))
                entry.next = cursor.next;
            else
                entry.next = cursor;
            return bucket;
        }
    }

    /**
     * Table to hold glyph index buckets.
     */
    private Entry[] table = new Entry[DEFAULT_CAPACITY];
    /**
     * Load factor to determine how many indices the map can contain before it is
     * resized.
     *
     * @see #max
     * @see #resize()
     */
    private final float loadFactor = DEFAULT_LOAD_FACTOR;
    /**
     * Maximum number of indices the map can contain before it is resized.
     *
     * @see #resize()
     */
    private int max = (int) (table.length * loadFactor);
    /**
     * Current number of indices in the map.
     */
    private int size = 0;

    /**
     * Returns the glyph index for the specified array of characters, or
     * {@code -1} if on index was found.
     */
    @Contract(value = "null -> fail", pure = true)
    int getIndex(final char[] chars) {
        Entry entry = table[Glyph.hash(chars) & (table.length - 1)];
        // TODO Lookup could be faster if entries stored a sum value used for
        //  comparisons and were sorted by sum value.
        //  Only implement if/when many glyphs are stored and several buckets
        //  have multiple collisions, as each entry will allocate 32 more bytes.
        if (entry != null) {
            // entry character
            char ec;
            // glyph represents a single character
            if (chars.length == 1) {
                final char c = chars[0];
                do {
                    ec = entry.chars[0];
                    if (ec < c)
                        entry = entry.next;
                    else if (ec == c && entry.chars.length == 1)
                        return entry.index;
                    else
                        break;
                }
                while (entry != null);
            }
            // glyph represents a ligature
            else {
                // ligature index
                int li = 0;
                do {
                    if (li < entry.chars.length) {
                        ec = entry.chars[li];
                        if (ec < chars[li])
                            entry = entry.next;
                        else if (ec == chars[li] && entry.chars.length <= chars.length)
                            if (entry.chars.length < chars.length)
                                li++;
                            else
                                return entry.index;
                        else
                            break;
                    }
                }
                while (entry != null);
            }
        }
        return -1;
    }

    /**
     * Stores the specified index for a glyph that represents the specified
     * characters.
     *
     * @param chars the characters (key) to identify the glyph index
     * @param index the glyph index to store
     * @throws NullPointerException     if {@code chars} is {@code null}
     * @throws IllegalArgumentException if {@code index < 0}
     * @see #getIndex(char[])
     */
    @Contract(value = "null, _ -> fail", pure = true)
    void putIndex(final char[] chars, final int index) {
        Objects.requireNonNull(chars, "chars array is null");
        if (index < 0)
            throw new IllegalArgumentException("index is less than 0");

        if (++size > max)
            resize();

        final int hash = Glyph.hash(chars) & (table.length - 1);
        final Entry entry = table[hash];
        if (entry != null)
            table[hash] = addEntryToBucket(entry, new Entry(chars, index));
        else
            table[hash] = new Entry(chars, index);
    }

    /**
     * Doubles the capacity of the map and rehashes all entries.
     */
    private void resize() {
        final Entry[] newTable = new Entry[table.length << 1];
        for (int i = 0; i < table.length; i++)
            if (table[i] != null) {
                addBucketToTable(newTable, table[i]);
                table[i] = null;
            }
        table = newTable;
        max = (int) (table.length * loadFactor);
    }

    /**
     * Linked bucket class for storing glyph indices.
     * <p>
     * Entry links are sorted numerically in ascending order, per index.
     * For example, a bucket of entries might be sorted as:
     * <pre>
     *     "a", "ab", "ac", "ca", "cb", "cd", "d"
     * </pre>
     * While it looks like lexicographical ordering, it is not; a character
     * such as {@code 'X'} has a lower value (and therefore precedes) the
     * character {@code 'a'}. There are also some alphabets with characters
     * from multiple Unicode blocks, where a character that appears in the
     * middle of the alphabet would be sorted as the last entry if all
     * characters were put in the same bucket.
     */
    private static final class Entry {

        /**
         * The entry key.
         * <p>
         * The character(s) represented by the glyph that has the index stored in
         * this entry.
         */
        final char[] chars;
        /**
         * The entry value.
         * <p>
         * The index used to get the glyph that represents this entry's key
         * ({@link #chars}).
         */
        final int index;
        /**
         * The next entry, can be {@code null}.
         * <p>
         * The key of the following entry (if not {@code null}) and all
         * subsequent entries will have a higher numerical value than the key
         * of this entry, compared per index.
         */
        @Nullable
        Entry next = null;

        /**
         * Constructs a new entry with the specified characters and glyph
         * index.
         *
         * @param chars the entry key
         * @param index the entry value
         */
        @Contract(pure = true)
        Entry(final char[] chars, final int index) {
            this.chars = chars;
            this.index = index;
        }
    }
}
