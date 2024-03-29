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
        final Delegate delegate = DefaultDelegate.getInstance();
        final Size sizeButton = new Size(250, 30);

        final Scene main = new Scene();

        // MAIN SCENE
        final Label usernameLabel = new Label("Username");
        final TextField usernameText = new TextField();
        final Label passwordLabel = new Label("Password");
        final TextField passwordText = new TextField();
        final Button bLogIn = new Button("Log in");
        final Button bOptions = new Button("Options");
        final Button bExit = new Button("Exit");

        bLogIn.addAction(((src, event, c) -> {

        }));
        bLogIn.setSize(sizeButton);
        bLogIn.setDelegate(delegate);

        bOptions.setSize(sizeButton);
        bOptions.setDelegate(delegate);

        bExit.addAction((src, event, c) -> thread.shutdown());
        bExit.setSize(sizeButton);
        bExit.setDelegate(delegate);

        main.addComponent(usernameLabel);
        main.addComponent(usernameText);
        main.addComponent(passwordLabel);
        main.addComponent(passwordText);
        main.addComponent(bLogIn);
        main.addComponent(bOptions);
        main.addComponent(bExit);
        main.setLayout(new StackLayout());


        setScene(main);
    }
}
