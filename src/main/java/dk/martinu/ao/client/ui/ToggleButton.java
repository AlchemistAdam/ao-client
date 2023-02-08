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
package dk.martinu.ao.client.ui;

import org.jetbrains.annotations.NotNull;

import java.awt.event.InputEvent;

import dk.martinu.ao.client.targets.Target;

public class ToggleButton extends Button {

    protected boolean toggled;

    public ToggleButton() {
        this("", false);
    }

    public ToggleButton(final boolean isToggled) {
        this("", isToggled);
    }

    public ToggleButton(final String text) {
        this(text, false);
    }

    public ToggleButton(final String text, final boolean isToggled) {
        super(text);
        setToggled(isToggled);
    }

    @Override
    public void doActions(final @NotNull Target source, final @NotNull InputEvent event) {
        setToggled(!isToggled());
        super.doActions(source, event);
    }

    public boolean isToggled() {
        return toggled;
    }

    public void setToggled(final boolean b) {
        toggled = b;
    }
}
