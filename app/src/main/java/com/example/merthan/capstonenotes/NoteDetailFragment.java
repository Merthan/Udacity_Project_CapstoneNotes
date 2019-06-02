package com.example.merthan.capstonenotes;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.merthan.capstonenotes.dummy.DummyContent;
import com.plattysoft.leonids.ParticleSystem;

import static com.example.merthan.capstonenotes.NoteListActivity.myRef;

/**
 * A fragment representing a single Note detail screen.
 * This fragment is either contained in a {@link NoteListActivity}
 * in two-pane mode (on tablets) or a {@link NoteDetailActivity}
 * on handsets.
 */
public class NoteDetailFragment extends Fragment {

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_NOTE_AS_ARRAY= "notearray";

    /**
     * The dummy content this fragment is presenting.
     */
    private Note mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public NoteDetailFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_NOTE_AS_ARRAY)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = new Note(getArguments().getStringArray(ARG_NOTE_AS_ARRAY));

            //Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = null;//(CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                //appBarLayout.setTitle(mItem.content);
                appBarLayout.setBackgroundColor(Color.GREEN);
            }

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.note_detail, container, false);


        if (mItem != null) {
            bodyEditText=((EditText) rootView.findViewById(R.id.note_detail));
            bodyEditText.setText(mItem.getBody());
        }





        return rootView;
    }
    EditText bodyEditText;
    EditText titleEditText;
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Drawable draw=getResources().getDrawable(R.drawable.ic_android_black_24dp);

        ParticleSystem ps=new ParticleSystem(getActivity(), 40, draw, 2000);
        ps
                .setSpeedRange(0.05f, 0.17f)
                .setFadeOut(500,new DecelerateInterpolator())
                .setRotationSpeedRange(0.02f,0.5f)
                .setInitialRotationRange(0,360)
                .emit(-50,500,6);


        Toolbar mToolbar = (Toolbar) getActivity().findViewById(R.id.toolbarDetail);
        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        titleEditText=getActivity().findViewById(R.id.note_detail_title_edittext);

        titleEditText.setText(mItem.getTitle());



        FloatingActionButton fab = getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own detail action", Snackbar.LENGTH_LONG).setAction("Action", null).show();

                final EditText input = new EditText(getContext());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                //alertDialog.setView(input); // uncomment this line

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Count amount of a certain word/text within this note");
                //builder.setMessage(mParentActivity.getString(R.string.are_you_sure_you_want_to_delete)+item.getTitle()+"\"?");
                builder.setView(input);
                builder.setPositiveButton(R.string.count, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new CountTextAsyncTask(new CountTextAsyncTask.OnFinishedListener() {
                            @Override
                            public void onFinished(String result) {
                                Toast.makeText(getContext(),getString(R.string.found)+" \""+input.getText().toString()+"\" "+result+" "+getString(R.string.times),Toast.LENGTH_SHORT).show();

                            }
                        },bodyEditText.getText().toString(),input.getText().toString()).execute();
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.show();

            }
        });

       // ps.updateEmitPoint(0,getActivity().getWindow().getDecorView().getHeight()/2);

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(bodyEditText!=null&&titleEditText!=null)
        myRef.child("NOTE_"+ mItem.getId()).setValue(new Note(titleEditText.getText().toString(),bodyEditText.getText().toString(),mItem.getId()));
    }
}
