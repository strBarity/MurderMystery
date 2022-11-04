package main.stringhandler;

public class TextFormatter {
    public enum StringColor {
        Black(0), Dark_Blue(1), Dark_Green(2), Dark_Aqua(3), Dark_Red(4), Dark_Purple(5),
        Gold(6), Gray(7), Dark_Gray(8), Blue(9), Green(0xa), Aqua(0xb), Red(0xc), Light_Purple(0xd),
        Yellow(0xe), White(0xf);

        public final int value;

        StringColor(int value) {
            this.value = value;
        }

        public String toString() {
            return "§" + Integer.toHexString(value);
        }
    }

    public enum StringStyle {
        Obfuscated('k'), Bold('l'), Strikethrough('m'), Underlined('n'), Italic('o'), None('r');

        public final char value;

        StringStyle(char value) {
            this.value = value;
        }

        public String toString() {
            return "§" + value;
        }
    }

    public String string;
    private StringColor preColor;
    private StringColor postColor;
    private StringStyle preStyle;
    private StringStyle postStyle;
    private boolean usePreColor = false;
    private boolean usePostColor = false;
    private boolean usePreStyle = false;
    private boolean usePostStyle = false;

    public StringColor getPreColor() {
        return preColor;
    }

    public StringColor getPostColor() {
        return postColor;
    }

    public StringStyle getPreStyle() {
        return preStyle;
    }

    public StringStyle getPostStyle() {
        return postStyle;
    }

    public TextFormatter(String string) {
        this(string, null, null, null, null);
    }

    public TextFormatter(String string, StringColor preColor) {
        this(string, preColor, null, null, null);
    }

    public TextFormatter(String string, StringStyle preStyle) {
        this(string, null, preStyle, null, null);
    }

    public TextFormatter(String string, StringColor preColor, StringStyle preStyle) {
        this(string, preColor, preStyle, null, null);
    }

    public TextFormatter(String string, StringColor preColor, StringStyle preStyle, StringColor postColor, StringStyle postStyle) {
        this.string = string;
        this.preColor = preColor;
        this.preStyle = preStyle;
        this.postColor = postColor;
        this.postStyle = postStyle;
        usePreColor = preColor != null;
        usePreStyle = preStyle != null;
        usePostColor = postColor != null;
        usePostStyle = postStyle != null;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if(usePreColor)
            sb.append("§").append(Integer.toHexString(preColor.value));
        if(usePreStyle)
            sb.append("§").append(preStyle.value);
        sb.append(string);
        if(usePostColor)
            sb.append("§").append(Integer.toHexString(postColor.value));
        if(usePostStyle)
            sb.append("§").append(postStyle.value);
        return sb.toString();
    }
}
