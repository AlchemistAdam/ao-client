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
package dk.martinu.ao.client.event;

import org.jetbrains.annotations.NotNull;

import java.awt.event.KeyEvent;
import java.util.Arrays;

import dk.martinu.ao.client.targets.AbstractTarget;

/**
 * Resizable buffer class for storing and processing {@link KeyInput} objects
 * used by {@link AbstractTarget}. This implementation uses a double buffer,
 * such that input can be processed while new input is buffered.
 * <p>
 * <b>NOTE:</b> this implementation is only semi-threadsafe; multiple threads
 * can add key input to the buffer concurrently, but it must only be processed
 * by a single thread.
 *
 * @author Adam Martinu
 * @version 1.0, 2023-06-02
 * @see AbstractTarget#keyPressed(KeyEvent)
 * @see AbstractTarget#keyReleased(KeyEvent)
 * @since 1.0
 */
public final class KeyInputBuffer {

    /**
     * Processes all {@link KeyInput} in the specified buffer array. For a
     * given event, this method will process all key input for that event in
     * ascending order and check if the event was consumed by the key input. If
     * an event is consumed, then all subsequent key input for that event are
     * skipped.
     *
     * @see #processInput()
     * @see KeyInput#process()
     */
    private static void processInput(@NotNull final KeyInput[] buffer) {
        // current event, input added from the same event are in sequence
        KeyEvent event = null;
        // true if the input should be processed
        boolean process = false;

        for (int i = 0; i < buffer.length && buffer[i] != null; i++) {
            final KeyInput input = buffer[i];

            // new event signals a new sequence of input
            if (event != input.event()) {
                event = input.event();
                process = true;
            }

            if (process)
                process = input.process();
        }
    }

    /**
     * Reference to the buffer that is currently being filled.
     */
    @NotNull
    private KeyInput[] inputBuffer;
    /**
     * Current size of {@link #inputBuffer} and the index to insert the next
     * key input that is buffered.
     */
    private int index = 0;
    /**
     * The first buffer.
     */
    @NotNull
    private final KeyInput[] buffer1;
    /**
     * The second buffer
     */
    @NotNull
    private final KeyInput[] buffer2;

    /**
     * Constructs a new {@code KeyInputBuffer} with a default buffer capacity
     * of {@code 8}.
     */
    public KeyInputBuffer() {
        this(8);
    }

    /**
     * Constructs a new {@code KeyInputBuffer} with the specified buffer
     * capacity.
     *
     * @param capacity The initial capacity of the buffer
     * @throws IllegalArgumentException If {@code capacity} is not less than or
     *                                  equal to {@code 0}
     */
    public KeyInputBuffer(final int capacity) {
        if (capacity <= 0)
            throw new IllegalArgumentException("capacity must be greater than 0");
        inputBuffer = buffer1 = new KeyInput[capacity];
        buffer2 = new KeyInput[capacity];
    }

    /**
     * Adds all {@link KeyInput} objects in the specified array to this
     * buffer.
     * <p>
     * <b>NOTE:</b> this method does not check for null pointers. The array and
     * all its elements must not be null, otherwise it might result in an
     * exception.
     *
     * @param inputArray Array of key input to add
     */
    public synchronized void add(@NotNull final KeyInput[] inputArray) {
        // expand capacity if necessary
        if (index + inputArray.length > inputBuffer.length)
            inputBuffer = Arrays.copyOf(inputBuffer, index + inputArray.length);
        System.arraycopy(inputArray, 0, inputBuffer, index, inputArray.length);
        index += inputArray.length;
    }

    /**
     * Processes all {@link KeyInput} objects added to this buffer.
     *
     * @see #add(KeyInput[])
     * @see KeyInput#process()
     */
    public void processInput() {
        // the buffer that will be processed (inputBuffer)
        final KeyInput[] processBuffer;
        // the buffer that will be filled
        final KeyInput[] fillBuffer;

        // swap buffer arrays
        synchronized (this) {
            if (inputBuffer == buffer1) {
                processBuffer = buffer1;
                fillBuffer = buffer2;
            }
            else {
                processBuffer = buffer2;
                fillBuffer = buffer1;
            }
            // insert null mark if size is less than length
            if (index < processBuffer.length)
                processBuffer[index] = null;
            inputBuffer = fillBuffer;
            index = 0;
        }

        processInput(processBuffer);
    }
}
