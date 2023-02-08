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

public class Label extends Component {

    public static final float TOP = 0.0F;
    public static final float LEFT = 0.0F;
    public static final float CENTER = 0.5F;
    public static final float BOTTOM = 1.0F;
    public static final float RIGHT = 1.0F;

    protected @Nullable String text;
    protected float horizontalAlignment = CENTER;
    protected float verticalAlignment = CENTER;

    public Label() {
        this("");
    }

    public Label(final String text) {
        setText(text);
    }

    public float getHorizontalAlignment() {
        return horizontalAlignment;
    }

    @Nullable
    public String getText() {
        return text;
    }

    public float getVerticalAlignment() {
        return verticalAlignment;
    }

    public void setHorizontalAlignment(final float alignment) {
        if (alignment < 0.0F || alignment > 1.0F)
            throw new IllegalArgumentException("alignment must be between 0.0F and 1.0F inclusive");
        horizontalAlignment = alignment;
    }

    public void setText(@Nullable final String text) {
        this.text = text;
    }

    public void setVerticalAlignment(final float alignment) {
        if (alignment < 0.0F || alignment > 1.0F)
            throw new IllegalArgumentException("alignment must be between 0.0F and 1.0F inclusive");
        verticalAlignment = alignment;
    }
}
