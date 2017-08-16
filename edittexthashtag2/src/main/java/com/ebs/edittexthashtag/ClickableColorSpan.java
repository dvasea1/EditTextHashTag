package com.ebs.edittexthashtag;


import android.support.annotation.ColorInt;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

public class ClickableColorSpan extends ClickableSpan {

    private OnHashTagClickListener onHashTagClickListener;

    public interface OnHashTagClickListener {
        void onHashTagClicked(String hashTag, String tag);
    }

    private final int color;

    public ClickableColorSpan(@ColorInt int color, OnHashTagClickListener listener) {
        this.color = color;

        onHashTagClickListener = listener;

        if (onHashTagClickListener == null) {
            throw new RuntimeException("click listener is null");
        }

    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(color);
        //ds.bgColor = Color.GRAY;
    }

    @Override
    public void onClick(View widget) {
        CharSequence text = ((TextView) widget).getText();

        Spanned spanned = (Spanned) text;
        int start = spanned.getSpanStart(this);
        int end = spanned.getSpanEnd(this);

        onHashTagClickListener.onHashTagClicked(text.subSequence(start + 1/*skip "#" sign*/, end).toString(),text.subSequence(start, start + 1).toString());

    }
}
