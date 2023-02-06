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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.Toolkit;
import java.util.*;

import javax.swing.SwingUtilities;

import dk.martinu.ao.client.Log;
import dk.martinu.ao.client.targets.Intro;
import dk.martinu.ao.client.targets.Target;
import dk.martinu.kofi.Document;
import dk.martinu.kofi.Property;

/**
 * Launcher class for an Abaddon Online client. This class in itself does not
 * start a client, but serves as a scaffolding for doing so (see {@link Main}).
 *
 * @author Adam Martinu
 * @version 1.0, 2023-06-02
 * @see #Launcher(Document)
 * @see #run(Target)
 * @since 1.0
 */
class Launcher {

    /**
     * Enables or disables dynamic layout of top level containers.
     * <p>
     * The name of this option is {@code "dynamicLayout"}. Dynamic layout is
     * enabled if the argument (ignoring case), is equal to {@code "true"}, and
     * disabled if it is equal to {@code "false"}. Else the dynamic layout
     * property is not changed and retains its default value.
     * <p>
     * <b>NOTE:</b> This option does not guarantee whether dynamic layout is
     * enabled or disabled, it merely provides a hint to the underlying
     * platform. See {@link Toolkit#setDynamicLayout(boolean)} for more
     * details.
     */
    private static final Option OPTION_DYNAMIC_LAYOUT = (launcher, arg) -> {
        if ("true".equalsIgnoreCase(arg))
            Toolkit.getDefaultToolkit().setDynamicLayout(true);
        else if ("false".equalsIgnoreCase(arg))
            Toolkit.getDefaultToolkit().setDynamicLayout(false);
    };
    /**
     * Enables or disables the printing of the application's performance in the
     * thread loop.
     * <p>
     * The name of this option is {@code "printPerformance"}. Printing is
     * enabled if the argument (ignoring case) is equal to {@code "true"}, and
     * disabled if it is equal to {@code "false"}.
     *
     * @see GameThread#setPrintPerformance(boolean)
     */
    private static final Option OPTION_PRINT_PERFORMANCE = (launcher, arg) -> {
        if ("true".equalsIgnoreCase(arg))
            launcher.thread.setPrintPerformance(true);
        else if ("false".equalsIgnoreCase(arg))
            launcher.thread.setPrintPerformance(false);
    };
    /**
     * Option for skipping the intro cinematic at launch.
     * <p>
     * The name of this option is {@code "skipIntro"}. The intro is skipped  if
     * the argument (ignoring case) is equal to {@code "true"}, otherwise the
     * intro is not skipped.
     */
    private static final Option OPTION_SKIP_INTRO = (launcher, arg) -> {
        if ("true".equalsIgnoreCase(arg))
            launcher.skipIntro = true;
    };

    // unused
//    static {
//        System.setProperty("sun.awt.enableExtraMouseButtons", "true");
//    }

    /**
     * This launcher's game thread.
     */
    final GameThread thread;
    /**
     * {@code true} if the intro should be skipped.
     *
     * @see #OPTION_SKIP_INTRO
     */
    private boolean skipIntro = false;

    /**
     * Creates a new launcher with the specified configuration. The following
     * launcher options are available:
     * <ol>
     * <li>{@link #OPTION_DYNAMIC_LAYOUT}</li>
     * <li>{@link #OPTION_PRINT_PERFORMANCE}</li>
     * <li>{@link #OPTION_SKIP_INTRO}</li>
     * </ol>
     *
     * @param config The configuration for this launcher
     * @throws NullPointerException if {@code config} is {@code null}
     */
    Launcher(@NotNull final Document config) {
        Objects.requireNonNull(config, "config is null");

        /* default launcher options */
        //noinspection SpellCheckingInspection
        final Map<String, Option> options = Map.of(
                "dynamiclayout", OPTION_DYNAMIC_LAYOUT,
                "printperformance", OPTION_PRINT_PERFORMANCE,
                "skipintro", OPTION_SKIP_INTRO
        );

        /* configure launcher from config */
        final List<Property<String>> args = config.getProperties("launcher", String.class);
        if (args != null)
            for (final Property<String> arg : args) {
                final Option op = options.get(arg.key.toLowerCase(Locale.ROOT));
                if (op == null) {
                    Log.w("unknown launcher option [%s]", arg.key);
                    continue;
                }

                try {
                    op.configure(this, arg.value);
                    Log.i("using launcher option [%s=%s]", arg.key, arg.value);
                }
                catch (final Exception e) {
                    e.printStackTrace();
                    Log.e("launcher option failed [%s=%s]", e, arg.key, arg.value);
                }
            }

        /* create and configure thread from config */
        thread = new GameThread(config);
    }

    /**
     * Launches the Abaddon Online client. This method blocks until the
     * {@link #thread} is started.
     *
     * @param target The primary target
     * @throws NullPointerException  if {@code target} is {@code null}
     * @throws IllegalStateException if this launcher's thread has already been
     *                               started
     */
    final void run(@NotNull final Target target) throws IllegalStateException {
        Objects.requireNonNull(target, "target is null");
        if (thread.getState() != Thread.State.NEW)
            throw new IllegalStateException("thread has already been started");

        // initial target
        final Target t = skipIntro ? target : new Intro(thread, target);
        // launch client
        try {
            SwingUtilities.invokeAndWait(() ->
                    // TODO create and pass GraphicsConfiguration
                    thread.createFrame(null).setTarget(t).start());
            Log.i("launcher started thread successfully");
        }
        catch (final Exception e) {
            e.printStackTrace();
            Log.e("launcher terminated from exception", e);
            if (thread.frame != null)
                thread.frame.shutdown();
        }
    }

    /**
     * Options are used by {@link Launcher launchers} and are used to configure
     * the launcher itself and its {@link Launcher#thread thread}. a launcher
     * option is used by providing its name and argument as a
     * {@link Property Property} in the {@code [launcher]} section of the
     * {@link Document Document} when constructing the launcher.
     * <p>
     * The default {@code Launcher} implementation provides its own options and
     * adds them to the launcher when it is created.
     *
     * @see #Launcher(Document)
     */
    private interface Option {

        /**
         * Calls this option to configure the specified launcher with the
         * specified argument.
         */
        void configure(@NotNull final Launcher launcher, @Nullable final String arg);
    }
}
