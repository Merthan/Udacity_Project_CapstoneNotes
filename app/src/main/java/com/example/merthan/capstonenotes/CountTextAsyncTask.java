package com.example.merthan.capstonenotes;

import android.os.AsyncTask;

public class CountTextAsyncTask extends AsyncTask<Void,Void,Void> {

    private OnFinishedListener onFinishedListener;
    String text;
    private String searchingFor;

    private String result;

    public CountTextAsyncTask(OnFinishedListener listener,String text,String searchingFor){
        this.onFinishedListener=listener;
        this.text=text;
        this.searchingFor=searchingFor;
    }


    @Override
    protected Void doInBackground(Void... voids) {

        try {
            int counter=0;


            //Some ineffectient code i made to count the amount of the searchterm within string
            for(int index=0;index<text.length();){
                int indexOf=text.indexOf(searchingFor);
                if(indexOf==-1)break;
                counter++;
                if(indexOf<text.length())
                    text=text.substring(indexOf+1);
            }
            result=counter+"";

        }catch (Exception E){
            result="ERROR";
            E.printStackTrace();
        }



        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        onFinishedListener.onFinished(result);
    }

    public interface OnFinishedListener{
        public void onFinished(String result);
    }




}
