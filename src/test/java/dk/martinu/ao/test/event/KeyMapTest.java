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
package dk.martinu.ao.test.event;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import dk.martinu.ao.client.event.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("SpellCheckingInspection")
@DisplayName("KeyMap")
public class KeyMapTest {

    @DisplayName("capacity is power of 2")
    @ParameterizedTest
    @CsvSource({
            "20, 10",
            "30, 10",
            "40, 10",
            "60, 20",
            "80, 20",
            "100, 20"
    })
    void capacity(final int n_actions, final int prio_max) {
        final KeyMap map = createMap(n_actions, prio_max);
        final KeyMap.Handle handle = map.new Handle();
        assertEquals(1, Integer.bitCount(handle.table().length));
        assertEquals(0, handle.table().length & 1);
    }

    @DisplayName("has correct max")
    @ParameterizedTest
    @CsvSource({
            "20, 10",
            "30, 10",
            "40, 10",
            "60, 20",
            "80, 20",
            "100, 20"
    })
    void max(final int n_actions, final int prio_max) {
        final KeyMap map = createMap(n_actions, prio_max);
        final KeyMap.Handle handle = map.new Handle();
        int max = (int) (handle.table().length * handle.loadFactor());
        while (max < n_actions)
            max <<= 1;
        assertEquals(max, handle.max());
    }

    @DisplayName("has correct size")
    @ParameterizedTest
    @CsvSource({
            "20, 10",
            "30, 10",
            "40, 10",
            "60, 20",
            "80, 20",
            "100, 20"
    })
    void size(final int n_actions, final int prio_max) {
        final KeyMap map = createMap(n_actions, prio_max);
        assertEquals(n_actions, map.new Handle().size());
    }

    @Contract(value = "_, _ -> new", pure = true)
    @NotNull
    private KeyMap createMap(final int n_actions, final int prio_max) {
        // actions to add
        final KeyAction[] actions = new KeyAction[n_actions];

        // populate array with dummy actions
        final java.util.Random r = new java.util.Random();
        for (int i = 0; i < n_actions; i++)
            actions[i] = new OnPressKeyAction(r.nextInt(prio_max), (action, event) -> false);

        final int AZ_MAX = 'Z' - 'A';
        final KeyMap map = new KeyMap();
        for (KeyAction action : actions)
            map.put(r.nextInt(AZ_MAX) + 0x41, action); // 0x41 == VK_A

        return map;
    }
}
