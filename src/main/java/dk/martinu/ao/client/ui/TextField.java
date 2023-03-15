package dk.martinu.ao.client.ui;

import org.jetbrains.annotations.*;

import java.awt.event.KeyEvent;
import java.util.Arrays;

import dk.martinu.ao.client.text.Font;

public class TextField extends Component {

    /**
     * Constant for empty text;
     */
    private static final char[] EMPTY_TEXT = new char[0];

    /**
     * The text of this text field as an array of {@code char}
     * values.
     */
    protected char[] text = EMPTY_TEXT;
    /**
     * The font used to render and size the text
     */
    @Nullable
    protected Font font = null;
    /**
     * {@code true} if the text can be edited, otherwise {@code false}.
     */
    protected boolean editable = false;
    /**
     * {@code true} if the text can be selected, otherwise {@code false}.
     */
    protected boolean selectable = false;

    /**
     * Returns the text as an array of {@code char} values.
     */
    @Contract(value = "-> !null", pure = true)
    public char[] getText() {
        return text;
    }

    /**
     * Returns {@code true} if the text can be edited, otherwise {@code false}.
     */
    @Contract(pure = true)
    public boolean isEditable() {
        return editable;
    }

    /**
     * Returns {@code true} if the text can be selected, otherwise
     * {@code false}.
     */
    @Contract(pure = true)
    public boolean isSelectable() {
        return selectable;
    }

    public void onKeyTyped(@NotNull final KeyEvent event) {

    }

    /**
     * Sets the editable state of this text field.
     */
    public void setEditable(final boolean editable) {
        this.editable = editable;
    }

    /**
     * Sets the selectable state of this text field.
     */
    public void setSelectable(final boolean selectable) {
        this.selectable = selectable;
    }

    /**
     * Replaces the text of this text field with the specified text. If
     * {@code text} is {@code null}, then the text will be replaced with an
     * empty array.
     */
    public void setText(final char[] text) {
        if (text == null || text.length == 0) {
            if (this.text != EMPTY_TEXT)
                this.text = EMPTY_TEXT;
        }
        else if (!Arrays.equals(this.text, text))
            this.text = text;
    }
}
