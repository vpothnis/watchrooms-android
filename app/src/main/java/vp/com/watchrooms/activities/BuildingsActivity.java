package vp.com.watchrooms.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import vp.com.watchrooms.CommonConstants;
import vp.com.watchrooms.R;
import vp.com.watchrooms.adapters.BuildingsRecycleViewAdapter;
import vp.com.watchrooms.async.AsyncBuildingsTask;

import static java.lang.String.format;

public class BuildingsActivity extends Activity implements BuildingsRecycleViewAdapter.BuildingsClickListener {

    private static final String TAG = BuildingsActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private String currentUserJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buildings);

        mRecyclerView = (RecyclerView) findViewById(R.id.buildings_recycler_view);

        // use this setting to improve performance if you know that changes in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new BuildingsRecycleViewAdapter(this);

        // get the current user details
        Intent callingIntent = getIntent();
        currentUserJson = callingIntent.getStringExtra(CommonConstants.EXTRA_CURRENT_USER);

        // The Async Task will set the adapter to the recycler view after it has downloaded the initial data
        AsyncBuildingsTask buildingsTask = new AsyncBuildingsTask((BuildingsRecycleViewAdapter) mAdapter, mRecyclerView);
        buildingsTask.execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_common, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_my_subscriptions) {
            Log.v(TAG, format("Trying to forward to My Subscriptions Activity"));
            Intent intent = new Intent(getApplicationContext(), MySubscriptionsActivity.class);
            intent.putExtra(CommonConstants.EXTRA_CURRENT_USER, currentUserJson);
            startActivity(intent);
        } else if (id == R.id.action_signout) {
            // signout
        } else if (id == R.id.action_home) {
            Log.v(TAG, format("Trying to forward to Buildings Activity"));
            Intent intent = new Intent(getApplicationContext(), BuildingsActivity.class);
            intent.putExtra(CommonConstants.EXTRA_CURRENT_USER, currentUserJson);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


    // forward to the floors activity
    @Override
    public void onBuildingSelect(String buildingId) {
        Log.v(TAG, format("Trying to forward to Floors Activity with building id: [%s] selected", buildingId));
        // start the Floors Activity
        Intent intent = new Intent(getApplicationContext(), FloorsActivity.class);
        intent.putExtra(CommonConstants.EXTRA_BUILDING_ID, buildingId);
        intent.putExtra(CommonConstants.EXTRA_CURRENT_USER, currentUserJson);
        startActivity(intent);
    }
}
