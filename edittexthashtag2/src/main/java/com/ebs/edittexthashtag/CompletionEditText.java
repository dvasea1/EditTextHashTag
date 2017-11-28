package com.ebs.edittexthashtag;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by barbaros.vasile on 12/30/2016.
 */

public class CompletionEditText {
    private FilteredArrayAdapter<String> adapterFilter;
    private List<String> list;
    private ActiveHashTag editTextTag;

    public void initEditText(MultiAutoCompleteTextView edit,int color,char[] tagChars,Context context, final int layout){
        list = new ArrayList<>();

        char[] additionalSymbols = new char[]{'_'};

        for (int i = 0; i < tagChars.length; i++) {
            editTextTag = ActiveHashTag.Factory.create(color, tagChars[i], null, additionalSymbols);
            editTextTag.operate(edit);
        }

        /*editTextTag = ActiveHashTag.Factory.create(color, tagChar, null, additionalSymbols);
        editTextTag.operate(edit);*/

        adapterFilter  = new FilteredArrayAdapter<String>(context, layout, list) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {

                    LayoutInflater l = (LayoutInflater)getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                    convertView = l.inflate(layout, parent, false);
                }

                String hastag = getItem(position);
                ((TextView)convertView.findViewById(R.id.text)).setText(hastag);

                return convertView;
            }

            @Override
            protected boolean keepObject(String hastag, String mask) {
                String myMask =  editTextTag.getCurrentText().toLowerCase();
              //  myMask = myMask.substring(1,myMask.length());
            //    System.out.println("")
                System.out.println("myMask "+myMask+" hashtag "+hastag+" contains "+ hastag.toLowerCase().contains(myMask.toLowerCase()));
                return hastag.toLowerCase().contains(myMask.toLowerCase());
            }
        };
        edit.setAdapter(adapterFilter);
        edit.setThreshold(1);
        edit.setTokenizer(new SpaceTokenizer());
    }

   /* public void initTextView(TextView edit, int color, char tagChar, ActiveHashTag.OnHashTagClickListener listener){
        char[] additionalSymbols = new char[]{'_'};
        editTextTag = ActiveHashTag.Factory.create(color, tagChar, listener, additionalSymbols);
        editTextTag.operate(edit);
    }*/

    public void initTextView(TextView edit, char[] chars,int color, ActiveHashTag.OnHashTagClickListener listener){
        char[] additionalSymbols = new char[]{'_'};

        for (int i = 0; i < chars.length; i++) {
            editTextTag = ActiveHashTag.Factory.create(color, chars[i], listener, additionalSymbols);
            editTextTag.operate(edit);
        }


        /*editTextTag = ActiveHashTag.Factory.create(color, Constant.DOLLAR, listener, additionalSymbols);
        editTextTag.operate(edit);

        editTextTag = ActiveHashTag.Factory.create(color, Constant.SHARP, listener, additionalSymbols);
        editTextTag.operate(edit);*/
    }

    public ActiveHashTag getEditTextTag() {
        return editTextTag;
    }

    public void addToFilter(List<String>tagsStrings ){
        System.out.println("tags "+tagsStrings);
        adapterFilter.clear();
        adapterFilter.addAll(tagsStrings);
        adapterFilter.notifyDataSetChanged();
    }
    public void removeFilter(){
        adapterFilter.clear();
        adapterFilter.notifyDataSetChanged();
    }
}
