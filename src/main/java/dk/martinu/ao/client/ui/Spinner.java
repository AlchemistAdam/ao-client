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
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;

import dk.martinu.ao.client.util.ZUtil;

public class Spinner<E> extends Container {

    protected final ArrayList<E> elements = new ArrayList<>();
    protected @Nullable E element = null;
    protected int index = -1;
    protected final Button buttonLeft = new Button("<");
    protected final Button buttonRight = new Button(">");
    protected final Label label = new Label();
    protected boolean cyclic = true;
    protected int buttonWidth = 30;
    protected int buttonHeight = 30;

    @SuppressWarnings("unchecked")
    public Spinner() {
        this((E[]) new Object[0]);
    }

    public Spinner(final E[] array) {
        super(3);

        setElements(array);

        buttonLeft.setFocusTraverseDown(buttonRight);
        buttonLeft.setFocusTraverseRight(buttonRight);
        buttonRight.setFocusTraverseLeft(buttonLeft);
        buttonRight.setFocusTraverseUp(buttonLeft);
        addComponent(label);
        addComponent(buttonLeft);
        addComponent(buttonRight);
        setLayout(c -> {
            final Size s = getSize();

            buttonLeft.setSize(buttonWidth, buttonHeight);
            buttonRight.setSize(buttonWidth, buttonHeight);
            label.setSize(s.width - 2 * buttonWidth, s.height);

            int x = getX();
            buttonLeft.setX(x);
            x += buttonWidth;
            label.setX(x);
            x += label.getWidth();
            buttonRight.setX(x);

            int y = getY();
            label.setY(y);
            y += (s.height - buttonHeight) / 2;
            buttonLeft.setY(y);
            buttonRight.setY(y);
        });

        buttonLeft.addAction((target, response, event) -> { selectPreviousElement(); });
        buttonRight.addAction((target, response, event) -> { selectNextElement(); });
    }

    public int getButtonHeight() {
        return buttonHeight;
    }

    public int getButtonWidth() {
        return buttonWidth;
    }

    public @Nullable E getElement() {
        return element;
    }

    public int getElementsSize() {
        return elements.size();
    }

    public int getIndex() {
        return index;
    }

    public @NotNull Label getLabel() {
        return label;
    }

    public @NotNull Button getLeftButton() {
        return buttonLeft;
    }

    public @NotNull Button getRightButton() {
        return buttonRight;
    }

    public void selectNextElement() {
        if (elements.isEmpty())
            return;
        if (++index >= elements.size())
            if (cyclic)
                index = 0;
            else
                index = elements.size() - 1;
        element = elements.get(index);
        label.setText(element.toString());
    }

    public void selectPreviousElement() {
        if (elements.isEmpty())
            return;
        if (--index < 0)
            if (cyclic)
                index = elements.size() - 1;
            else
                index = 0;
        element = elements.get(index);
        label.setText(element.toString());
    }

    public void setButtonHeight(final int buttonHeight) {
        this.buttonHeight = buttonHeight;
    }

    public void setButtonWidth(final int buttonWidth) {
        this.buttonWidth = buttonWidth;
    }

    public void setElements(final E @Nullable [] array) {
        if (array == null)
            throw new NullPointerException("array must not be null");
        if (ZUtil.contains(null, array))
            throw new IllegalArgumentException("array must not contain null elements");
        elements.clear();
        for (final E element : array)
            elements.add(element);
        elements.trimToSize();
        if (!elements.isEmpty()) {
            element = elements.get(index = 0);
            label.setText(element.toString());
        }
        else {
            element = null;
            index = -1;
            label.setText("");
        }
    }

    @Override
    public void setEnabled(final boolean enabled) {
        super.setEnabled(enabled);
        buttonLeft.setEnabled(enabled);
        buttonRight.setEnabled(enabled);
        label.setEnabled(enabled);
    }

    public boolean setSelectedElement(final @Nullable E element) {
        if (element == null)
            throw new NullPointerException("element must not be null");
        final Iterator<E> iterator = elements.iterator();
        E next = null;
        int index = -1;
        while (iterator.hasNext()) {
            next = iterator.next();
            index++;
            if (next.equals(element)) {
                this.element = element;
                this.index = index;
                label.setText(element.toString());
                return true;
            }
        }
        return false;
    }

    public void setSelectedElement(final int index) {
        if (index < 0 || index >= elements.size())
            throw new IndexOutOfBoundsException("index " + index + " is out of bounds");
        element = elements.get(this.index = index);
        label.setText(element.toString());
    }

    @Override
    public void setVisible(final boolean visible) {
        super.setVisible(visible);
        buttonLeft.setVisible(visible);
        buttonRight.setVisible(visible);
        label.setVisible(visible);
    }
}
