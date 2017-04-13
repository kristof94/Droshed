package kristof.fr.droshed;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

import org.w3c.dom.Attr;

/**
 * Created by kristof
 * on 4/13/17.
 */

public class CustomTextView extends TextView {

    public static final String ANDROID_SCHEMA = "http://schemas.android.com/apk/res/android";

    public CustomTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setTypeFaceCustom(context,attrs);
    }

    public CustomTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setTypeFaceCustom(context,attrs);
    }

    private void setTypeFaceCustom(Context context, AttributeSet attrs){
        int textStyle = attrs.getAttributeIntValue(ANDROID_SCHEMA, "textStyle", Typeface.NORMAL);
        Typeface customFont = selectTypeface(context, textStyle);
        setTypeface(customFont);
    }

    private Typeface selectTypeface(Context context, int textStyle) {
        switch (textStyle) {
            case Typeface.BOLD: // bold
                return FontCache.getFont(context,context.getString(R.string.font));

            case Typeface.ITALIC: // italic
                return FontCache.getFont(context,context.getString(R.string.font));

            case Typeface.BOLD_ITALIC: // bold italic
                return FontCache.getFont(context,context.getString(R.string.font));
            case Typeface.NORMAL: // regular
                return FontCache.getFont(context,context.getString(R.string.font));
            default:
                return FontCache.getFont(context,context.getString(R.string.font));
        }
    }

    }
