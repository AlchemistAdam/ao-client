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
package dk.martinu.ao.client.core;

import java.io.IOException;
import java.nio.file.Path;

import dk.martinu.ao.client.targets.Login;
import dk.martinu.ao.client.util.Log;
import dk.martinu.kofi.Document;
import dk.martinu.kofi.codecs.KofiCodec;

/**
 * Main entry point for starting the Abaddon Online client.
 *
 * @author Adam Martinu
 * @version 1.0, 2023-02-06
 * @since 1.0
 */
public final class Main {

    public static final Path CLIENT_CONFIG_PATH = Path.of("client.kofi");

    public static void main(final String[] args) {
        Document config;
        try {
            config = KofiCodec.provider().readFile(CLIENT_CONFIG_PATH);
        }
        catch (IOException e) {
            Log.e("could not read configuration file", e);
            config = new Document();
        }

        final Launcher launcher = new Launcher(config);
        final Login login = new Login(launcher.thread);
        launcher.run(login);
    }
}
