import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

public class NumberDocument extends PlainDocument {
    @Override
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        if (str == null) {
            return;
        }

        // Ensure the input is a digit between 1 and 9
        if (str.length() == 1 && Character.isDigit(str.charAt(0)) && !"0".equals(str)) {
            super.insertString(offs, str, a);
        }
    }
}
