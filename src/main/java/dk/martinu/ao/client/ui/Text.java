package dk.martinu.ao.client.ui;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.awt.event.KeyEvent;
import java.util.Arrays;

public class Text extends Component {

    private static final char[] EMPTY_TEXT = new char[0];

    protected char[] text = EMPTY_TEXT;
    protected boolean editable = false;
    protected boolean selectable = false;

    @Contract(value = "-> !null", pure = true)
    public char[] getText() {
        return text;
    }

    @Contract(pure = true)
    public boolean isEditable() {
        return editable;
    }

    @Contract(pure = true)
    public boolean isSelectable() {
        return selectable;
    }

    public void onKeyTyped(@NotNull final KeyEvent event) {

    }

    public void setEditable(final boolean editable) {
        this.editable = editable;
    }

    public void setSelectable(final boolean selectable) {
        this.selectable = selectable;
    }

    public void setText(char[] text) {
        if (text == null) {
            if (this.text != EMPTY_TEXT)
                this.text = EMPTY_TEXT;
        }
        else if (!Arrays.equals(this.text, text))
            this.text = text;
    }
}
