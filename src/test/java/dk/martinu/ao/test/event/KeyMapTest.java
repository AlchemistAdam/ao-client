package dk.martinu.ao.test.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import dk.martinu.ao.client.event.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("KeyMap")
public class KeyMapTest {

    KeyMap createMap(final int n_actions, final int prio_max) {
        // actions to add
        final KeyAction[] actions = new KeyAction[n_actions];

        // populate array with dummy actions
        final java.util.Random r = new java.util.Random();
        for (int i = 0; i < n_actions; i++)
            actions[i] = new OnPressKeyAction(r.nextInt(prio_max), (action, event) -> false);

        final int AZ_MAX = 'Z' - 'A';
        final KeyMap map = new KeyMap();
        for (KeyAction action : actions)
            map.insert(action, r.nextInt(AZ_MAX) + 0x41); // 0x41 == VK_A

        return map;
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
}
