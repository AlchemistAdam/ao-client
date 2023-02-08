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

import org.jetbrains.annotations.Nullable;

import java.awt.Color;

public class Button extends Component {

    public static final Color DEFAULT_BACKGROUND = new Color(70, 70, 70);

    // text on button
    protected @Nullable String text;

    public Button() {
        this("");
    }

    public Button(final String text) {
        setFocusable(true);
        setText(text);
    }

    public @Nullable String getText() {
        return text;
    }

    public void setText(final @Nullable String text) {
        if (text == null)
            throw new NullPointerException("text must not be null");
        this.text = text;
    }
}
