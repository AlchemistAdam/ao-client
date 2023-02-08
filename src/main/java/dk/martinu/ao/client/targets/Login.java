package dk.martinu.ao.client.targets;

import org.jetbrains.annotations.NotNull;

import dk.martinu.ao.client.core.GameThread;
import dk.martinu.ao.client.ui.*;

public class Login extends UITarget {

    public Login(@NotNull final GameThread thread) {
        super(thread);
        initUiComponents();
    }

    void initUiComponents() {
        final ComponentUI componentUI = DefaultComponentUI.getInstance();
        final Size sizeButton = new Size(250, 30);

        final Scene main = new Scene();

        // MAIN SCENE
        final Button bNewGame = new Button("New Game");
        final Button bContinue = new Button("Continue");
        final Button bOptions = new Button("Options");
        final Button bExit = new Button("Exit");

        bNewGame.addAction(((src, event, c) -> {

        }));
        bNewGame.setSize(sizeButton);
        bNewGame.setComponentUI(componentUI);
        bNewGame.setFocusTraverseV(bExit, bContinue);

        bContinue.setEnabled(false);
        bContinue.setSize(sizeButton);
        bContinue.setComponentUI(componentUI);
        bContinue.setFocusTraverseV(bNewGame, bOptions);

        bOptions.setSize(sizeButton);
        bOptions.setComponentUI(componentUI);
        bOptions.setFocusTraverseV(bContinue, bExit);

        bExit.addAction((src, event, c) -> thread.shutdown());
        bExit.setSize(sizeButton);
        bExit.setComponentUI(componentUI);
        bExit.setFocusTraverseV(bOptions, bNewGame);

        main.addComponent(bNewGame);
        main.addComponent(bContinue);
        main.addComponent(bOptions);
        main.addComponent(bExit);
        main.setLayout(new StackLayout());
        main.setDefaultFocusComponent(bNewGame);
        main.setFocusTraversable(true);


        setScene(main);
    }
}
