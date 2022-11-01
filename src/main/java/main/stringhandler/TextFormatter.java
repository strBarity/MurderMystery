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
    }

    public enum StringStyle {
        Obfuscated('k'), Bold('l'), Strikethrough('m'), Underlined('n'), Italic('o'), None('r');

        public final char value;

        StringStyle(char value) {
            this.value = value;
        }
    }

    public String string;
    StringColor preColor;
    StringColor postColor;
    StringStyle preStyle;
    StringStyle postStyle;
    boolean usePreColor = false;
    boolean usePostColor = false;
    boolean usePreStyle = false;
    boolean usePostStyle = false;

    public TextFormatter(String string)
    {
        this.string = string;
    }

    public TextFormatter(String string, StringColor preColor)
    {
        this.string = string;
        this.preColor = preColor;
        usePreColor = true;
    }

    public TextFormatter(String string, StringStyle preStyle)
    {
        this.string = string;
        this.preStyle = preStyle;
        usePreStyle = true;
    }

    public TextFormatter(String string, StringColor preColor, StringStyle preStyle)
    {
        this.string = string;
        this.preColor = preColor;
        this.preStyle = preStyle;
        usePreColor = true;
        usePreStyle = true;
    }

    public TextFormatter(String string, StringColor preColor, StringStyle preStyle, StringColor postColor, StringStyle postStyle)
    {
        this.string = string;
        this.preColor = preColor;
        this.preStyle = preStyle;
        this.postColor = postColor;
        this.postStyle = postStyle;
        usePreColor = true;
        usePreStyle = true;
        usePostColor = true;
        usePostStyle = true;
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
