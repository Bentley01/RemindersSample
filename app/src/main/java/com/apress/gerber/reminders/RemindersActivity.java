package com.apress.gerber.reminders;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class RemindersActivity extends AppCompatActivity {

    private ListView mListView;
    private RemindersDbAdapter mDbAdapter;
    private RemindersSimpleCursorAdapter mCursorAdapter;

    // test comment

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        // get id reference to the ListView
        mListView = (ListView) findViewById(R.id.reminders_list_view);
        mListView.setDivider(null);
        // create an ArrayAdapter with context (this), layout (reminders_row) and row id (row_text),
        // and stub data to display (String[])
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.reminders_row,
                R.id.row_text, new String[] {"first record", "second record", "third record"});


        mDbAdapter = new RemindersDbAdapter(this);
        mDbAdapter.open();

        Cursor cursor = mDbAdapter.fetchAllReminders();

        // create from columns array
        String[] from  = new String[] { RemindersDbAdapter.COL_CONTENT };

        // create to - the ids of the views in the layout
        int[] to = new int[] { R.id.row_text };

        // create a new simple cursor adapter to map from data to the to items in the reminders_row layout
        mCursorAdapter = new RemindersSimpleCursorAdapter(RemindersActivity.this, R.layout.reminders_row, cursor, from, to, 0);


        // set the ListView to use the adapter
        //mListView.setAdapter(arrayAdapter);

        // set the adapter to the SQL adapter, which updates the listview with the db data
        mListView.setAdapter(mCursorAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reminders, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {

            case R.id.action_new:
                // create a new reminder
                Log.d(getLocalClassName(), "create new Reminder");
                return true;

            case R.id.action_exit:
                // exit program
                finish();
                return true;
            default:
                return false;
        }
    }
}
