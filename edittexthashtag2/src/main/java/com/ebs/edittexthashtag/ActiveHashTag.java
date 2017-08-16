package com.ebs.edittexthashtag;

import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class ActiveHashTag implements ClickableColorSpan.OnHashTagClickListener {
    private final String TAG = ActiveHashTag.class.getSimpleName();
    private OnHashTagClickListener onHashTagClickListener;
    private TextChangerListener textChangerListener;
    private String currentText="";

    public interface OnHashTagClickListener {
        void onHashTagClicked(String hashTag, String tag);
    }

    private final List<Character> hashTagCharsList;
    private TextView textView;
    private int hashTagColor;
    private char tagChar;

    public static final class Factory {

        private Factory() {
        }

        public static ActiveHashTag create(int color, char tagChar, OnHashTagClickListener listener) {
            return new ActiveHashTag(color,tagChar, listener, null);
        }

        public static ActiveHashTag create(int color, char tagChar, OnHashTagClickListener listener, char... additionalHashTagChars) {
            return new ActiveHashTag(color,tagChar, listener, additionalHashTagChars);
        }
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence text, int start, int before, int count) {
            if (text.length() > 0) {
                resetColorizeText(text);
            }

            String textString = text.toString();

          /*  for (int i = 0; i < getAllHashTags(true).size() ; i++) {
                textString = textString.replace(getAllHashTags(true).get(i),"");
            }*/
            String[] separated = textString.split(" ");
            if(separated.length > 0){
                textString = separated[separated.length-1];
            }
            currentText = textString;
            if(textChangerListener!=null)textChangerListener.onTextChanged(textString);
            //System.out.println("text "+textString);
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    private ActiveHashTag(int color,char tagChar, OnHashTagClickListener listener, char... additionalHashTagCharacters) {
        hashTagColor = color;
        onHashTagClickListener = listener;
        hashTagCharsList = new ArrayList<>();
        this.tagChar = tagChar;

        if (null != additionalHashTagCharacters) {
            for (char additionalChar : additionalHashTagCharacters) {
                hashTagCharsList.add(additionalChar);
            }
        }
    }

    public void operate(TextView textView) {
        operate(textView, true);
//        if (null == this.textView) {
//            this.textView = textView;
//            textView.addTextChangedListener(textWatcher);
//
//            textView.setText(textView.getText(), TextView.BufferType.SPANNABLE);
//
//            if (null != onHashTagClickListener) {
//                textView.setMovementMethod(LinkMovementMethod.getInstance());
////                textView.setHighlightColor(Color.TRANSPARENT);
//            } else {
//                // hash tags are not clickable
//            }
//
//            setColorsHashTags(textView.getText());
//        } else {
//            throw new RuntimeException("TextView is not null.");
//        }

    }

    public void operate(TextView textView, boolean textWatcherEnable) {

        if (null == this.textView) {
            this.textView = textView;

            if (textWatcherEnable) {
                textView.addTextChangedListener(textWatcher);
            }

            textView.setText(textView.getText(), TextView.BufferType.SPANNABLE);

            if (null != onHashTagClickListener) {
                if (textWatcherEnable || null == textView.getMovementMethod()) {
                    textView.setMovementMethod(LinkMovementMethod.getInstance());
                }

//                textView.setHighlightColor(Color.TRANSPARENT);
            } else {
                // hash tags are not clickable
            }

            setColorsHashTags(textView.getText());
        } else {
            throw new RuntimeException("TextView is not null.");
        }
    }


    private void resetColorizeText(CharSequence text) {

        Spannable spannable = ((Spannable) textView.getText());

        CharacterStyle[] spans = spannable.getSpans(0, text.length(), CharacterStyle.class);
        for (CharacterStyle span : spans) {
            //spannable.removeSpan(span);
        }

        setColorsHashTags(text);
    }

    private void setColorsHashTags(CharSequence text) {

        int startHash;

        int index = 0;
        while (index < text.length() - 1) {
            char sign = text.charAt(index);
            int nextNotLetter = index + 1;

            if (tagChar == sign) {
                startHash = index;
                nextNotLetter = searchNextValidHashTagChar(text, startHash);

                if (startHash == nextNotLetter) {
                    nextNotLetter++;
                } else {
                    setColorEndHashTag(startHash, nextNotLetter);
                }
//                setColorEndHashTag(startHash, nextNotLetter);
            }
            index = nextNotLetter;
        }
    }

    private int searchNextValidHashTagChar(CharSequence text, int start) {

        int nextNotLetter = -1; // skip '#"
        for (int index = start + 1; index < text.length(); index++) {
            char prevLetter = text.charAt(index - 1);
            char sign = text.charAt(index);

            if (tagChar == prevLetter) {
                if ((tagChar == sign) || (Constant.SPACE == sign)) {
                    nextNotLetter = start;
                    break;
                }
            }

            boolean isValidSign = Character.isLetterOrDigit(sign) || hashTagCharsList.contains(sign);
            if (!isValidSign) {
                nextNotLetter = index;
                break;
            }
        }
        if (-1 == nextNotLetter) {
            //end of text
            nextNotLetter = text.length();
        }
        return nextNotLetter;
    }

    private void setColorEndHashTag(int startIndex, int nextNotLetter) {
        try {
           // Spannable s = (Spannable) textView.getText();

            SpannableStringBuilder sb = (SpannableStringBuilder) textView.getText();


            CharacterStyle span;

            if (onHashTagClickListener != null) {
                span = new ClickableColorSpan(hashTagColor, this);
            } else {
                span = new ForegroundColorSpan(hashTagColor);
            }

            if (-1 < startIndex && -1 < nextNotLetter && startIndex < nextNotLetter) {
                sb.setSpan(span, startIndex, nextNotLetter, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        } catch (Exception e) {
            Log.e(TAG, " setColorEndHashTag e " + e.getMessage() + " startIndex:: " + startIndex + " nextNotLetter:: " + nextNotLetter);
        }
    }

    public List<String> getAllHashTags(boolean withHashes) {

        String text = textView.getText().toString();
        Spannable spannable = (Spannable) textView.getText();

        Set<String> hashTags = new LinkedHashSet<>();

        for (CharacterStyle span : spannable.getSpans(0, text.length(), CharacterStyle.class)) {
            hashTags.add(
                    text.substring(!withHashes ? spannable.getSpanStart(span) + 1/*skip "#" sign*/
                                    : spannable.getSpanStart(span),
                            spannable.getSpanEnd(span)));
        }

        if (null != hashTags) Log.d(TAG, " getAllHashTags hashTags result " + hashTags);

        return new ArrayList<>(hashTags);
    }

    public List<String> getAllHashTags() {
        return getAllHashTags(false);
    }

    @Override
    public void onHashTagClicked(String hashTag,String tag) {
        onHashTagClickListener.onHashTagClicked(hashTag,tag);
    }

    public String getCurrentText() {
        return currentText;
    }

    public void setTextChangerListener(TextChangerListener textChangerListener) {
        this.textChangerListener = textChangerListener;
    }

    public interface TextChangerListener{
        public void onTextChanged(String text);
    }
}
