package com.example.merthan.capstonenotes;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.format.DateFormat;
import android.util.ArraySet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.merthan.capstonenotes.dummy.DummyContent;
import com.firebase.client.Firebase;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.plattysoft.leonids.ParticleSystem;

import android.provider.Settings.Secure;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * An activity representing a list of Notes. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link NoteDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class NoteListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    public static String convertDate(String dateInMilliseconds,String dateFormat) {
        return DateFormat.format(dateFormat, Long.parseLong(dateInMilliseconds)).toString();
    }

    public void licensesOnClick(View view){
        Toast.makeText(this, R.string.sadtext,Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.option1:licensesOnClick(null);return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.list_fab_options, menu);
        return true;
    }

    private boolean mTwoPane;
    public static DatabaseReference myRef;

    public static Set<Note> noteLocalList=new LinkedHashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);

        if(savedInstanceState==null){

            //ONLY CALLED AT FIRST START (APPSTART)

            // Sample AdMob app ID
            MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");

            FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        }


        Toast.makeText(this,getString(R.string.warning),Toast.LENGTH_LONG).show();

        final ProgressBar progressBar=findViewById(R.id.listLoadingBar);

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        final RecyclerView recyclerView = findViewById(R.id.note_list);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                long id =System.currentTimeMillis();

                myRef.child("NOTE_"+id).setValue(new Note(getString(R.string.title)+" "+convertDate(id+"","hh:mm  dd/MM"),"",id+""));
                setupRecyclerView((RecyclerView) recyclerView);

                new ParticleSystem(NoteListActivity.this, 200, R.drawable.ic_create_black_24dp, 2000,R.id.noteListParent)
                        .setSpeedRange(0.02f, 0.07f)
                        .setFadeOut(500,new DecelerateInterpolator())
                        .setRotationSpeedRange(0.02f,0.5f)
                        .setInitialRotationRange(0,360)
                        .oneShot(fab, 50);
            }
        });

        if (findViewById(R.id.note_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;
        }






        String userId= Secure.getString(getContentResolver(),Secure.ANDROID_ID);

        Firebase.getDefaultConfig().setPersistenceEnabled(true);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("Notes_"+userId);
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                noteLocalList.add(dataSnapshot.getValue(Note.class));
            }
            @Override//Removes old Note and readds new Note
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                //Removes the Note with the given id using an Object with the same id
                noteLocalList.remove(new Note("","",(dataSnapshot.getValue(Note.class)).getId()));
                noteLocalList.add(dataSnapshot.getValue(Note.class));
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                //Removes the Note with the given id using an Object with the same id
                noteLocalList.remove(new Note("","",(dataSnapshot.getValue(Note.class)).getId()));
            }

            @Override public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }
            @Override public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        myRef.addValueEventListener(new ValueEventListener() {//For initial loading
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot datas: dataSnapshot.getChildren()){
                    Log.d("DSLog",datas.toString()+">>");
                    noteLocalList.add(datas.getValue(Note.class));
                    //noteLocalList.add(datas.getChildren().iterator().next().getChildren().iterator().next().getValue(Note.class));
                }
                progressBar.setVisibility(View.GONE);
                setupRecyclerView((RecyclerView) recyclerView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        setupRecyclerView((RecyclerView) recyclerView);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {

        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(this, new ArrayList<Note>(noteLocalList), mTwoPane,this));

        //Updating widget datasource (SP)
        PreferenceManager.getDefaultSharedPreferences(this).edit().putString(CreateNoteWidget.WIDGET_PREFERENCE,noteLocalList.size()+"").apply();

        int[] ids = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), CreateNoteWidget.class));
        final CreateNoteWidget NOTE_WIDGET = new CreateNoteWidget();
        NOTE_WIDGET.onUpdate(this, AppWidgetManager.getInstance(this),ids);
        //int count= recyclerView.getAdapter().getItemCount();

        //if(count!=0)Toast.makeText(this,count+" Notes loaded",Toast.LENGTH_SHORT).show();
    }

    public static class SimpleItemRecyclerViewAdapter extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final NoteListActivity mParentActivity;
        private final List<Note> mValues;
        private final boolean mTwoPane;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Note item = (Note) view.getTag();
                if (mTwoPane) {
                    Bundle arguments = new Bundle();
                    //arguments.putString(NoteDetailFragment.ARG_ITEM_ID, item.id);
                    arguments.putStringArray(NoteDetailFragment.ARG_NOTE_AS_ARRAY,item.asStringArray());
                    NoteDetailFragment fragment = new NoteDetailFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.note_detail_container, fragment)
                            .commit();
                } else {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, NoteDetailActivity.class);
                    intent.putExtra(NoteDetailFragment.ARG_NOTE_AS_ARRAY,item.asStringArray());

                    context.startActivity(intent);
                }
            }
        };
        private final View.OnLongClickListener mOnLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {
                final Note item = (Note) view.getTag();
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle(R.string.delete_note);
                builder.setMessage(mParentActivity.getString(R.string.are_you_sure_you_want_to_delete)+item.getTitle()+"\"?");
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        myRef.child("NOTE_"+item.getId()).removeValue();

                        new ParticleSystem(activity, 200, R.drawable.ic_delete_black_24dp, 2000,R.id.noteListParent)
                                .setSpeedRange(0.02f, 0.17f)
                                .setFadeOut(500,new DecelerateInterpolator())
                                .setRotationSpeedRange(0.02f,0.5f)
                                .setInitialRotationRange(0,360)
                                .oneShot(view, 20);
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(mParentActivity,"Deleting cancelled",Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();

                return true;
            }
        };

        Activity activity;

        SimpleItemRecyclerViewAdapter(NoteListActivity parent, List<Note> items, boolean twoPane,Activity activity2) {
            mValues = items;
            mParentActivity = parent;
            mTwoPane = twoPane;
            activity=activity2;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mIdView.setText(mValues.get(position).getTitle());
            //holder.mContentView.setText(mValues.get(position).content);
            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
            holder.itemView.setOnLongClickListener(mOnLongClickListener);
        }

        @Override
        public int getItemCount() {
            return mValues==null?0:mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mIdView;
            //final TextView mContentView;

            ViewHolder(View view) {
                super(view);
                mIdView = (TextView) view.findViewById(R.id.id_text);
               // mContentView = (TextView) view.findViewById(R.id.content);
            }
        }
    }
}
