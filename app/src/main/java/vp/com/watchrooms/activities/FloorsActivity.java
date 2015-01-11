package vp.com.watchrooms.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import vp.com.watchrooms.CommonConstants;
import vp.com.watchrooms.R;
import vp.com.watchrooms.adapters.FloorsRecycleViewAdapter;
import vp.com.watchrooms.async.AsyncFloorsTask;

import static java.lang.String.format;

public class FloorsActivity extends Activity implements FloorsRecycleViewAdapter.FloorsClickListener {

    private static final String TAG = FloorsActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private String currentUserJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floors);

        // set the title
        getWindow().setTitle("Choose A Floor");

        mRecyclerView = (RecyclerView) findViewById(R.id.floors_recycler_view);

        // use this setting to improve performance if you know that changes in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new FloorsRecycleViewAdapter(this);

        // get the selected building id
        Intent callingIntent = getIntent();
        String buildingId = callingIntent.getStringExtra(CommonConstants.EXTRA_BUILDING_ID);
        currentUserJson = callingIntent.getStringExtra(CommonConstants.EXTRA_CURRENT_USER);

        // The Async Task will set the adapter to the recylcer view after it has downloaded the initial data
        AsyncFloorsTask floorsTask = new AsyncFloorsTask((FloorsRecycleViewAdapter) mAdapter, mRecyclerView, buildingId);
        floorsTask.execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_buildings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onFloorSelected(String floorId) {
        Log.v(TAG, format("Trying to forward to Rooms Activity with floor id: [%s] selected", floorId));

        // start the Floors Activity
        Intent intent = new Intent(getApplicationContext(), RoomsActivity.class);
        intent.putExtra(CommonConstants.EXTRA_FLOOR_ID, floorId);
        intent.putExtra(CommonConstants.EXTRA_CURRENT_USER, currentUserJson);
        startActivity(intent);
    }
}
