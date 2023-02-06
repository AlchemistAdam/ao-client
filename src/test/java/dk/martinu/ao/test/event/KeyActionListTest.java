package dk.martinu.ao.test.event;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import dk.martinu.ao.client.event.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("KeyActionList")
public class KeyActionListTest {

    @DisplayName("is sorted")
    @ParameterizedTest
    @ValueSource(ints = {20, 40, 60, 80, 100})
    void sort(final int n_actions) {
        // actions to add
        final KeyAction[] actions = new KeyAction[n_actions];

        // populate array with dummy actions
        final java.util.Random r = new java.util.Random();
        for (int i = 0; i < n_actions; i++)
            actions[i] = new OnPressKeyAction(r.nextInt(1000), (action, event) -> false);

        // init list
        final KeyActionList list = new KeyActionList(0, actions[0]);
        for (int i = 1; i < n_actions; i++)
            list.add(actions[i]);

        final KeyAction[] listActions = list.new Handle().actions();
        int priority = listActions[0].priority;
        for (int i = 1; i < n_actions; i++) {
            assertTrue(priority >= listActions[i].priority);
            priority = listActions[i].priority;
        }
    }
}
