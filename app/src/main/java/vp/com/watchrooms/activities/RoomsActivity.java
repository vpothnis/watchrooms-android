package vp.com.watchrooms.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import vp.com.watchrooms.CommonConstants;
import vp.com.watchrooms.R;
import vp.com.watchrooms.adapters.RoomsRecycleViewAdapter;
import vp.com.watchrooms.async.AsyncRoomsTask;
import vp.com.watchrooms.async.AsyncSubscriptionTask;
import vp.com.watchrooms.models.Room;
import vp.com.watchrooms.models.RoomList;
import vp.com.watchrooms.models.User;

public class RoomsActivity extends Activity implements RoomsRecycleViewAdapter.SubscriptionToggleClickListener {

    private static final String TAG = RoomsActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private String currentUserJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rooms);

        mRecyclerView = (RecyclerView) findViewById(R.id.rooms_recycler_view);

        // use this setting to improve performance if you know that changes in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // get the selected floor id
        Intent callingIntent = getIntent();
        String floorID = callingIntent.getStringExtra(CommonConstants.EXTRA_FLOOR_ID);
        currentUserJson = callingIntent.getStringExtra(CommonConstants.EXTRA_CURRENT_USER);

        mAdapter = new RoomsRecycleViewAdapter(this, currentUserJson);

        if (!mRecyclerView.isInEditMode()) {
            // The Async Task will set the adapter to the recylcer view after it has downloaded the initial data
            AsyncRoomsTask buildingsTask = new AsyncRoomsTask((RoomsRecycleViewAdapter) mAdapter, mRecyclerView, floorID, currentUserJson);
            buildingsTask.execute();
        } else {
            // set dummy data
            RoomList dummyData = new RoomList();
            dummyData.setCount(10L);
            dummyData.addRoom(new Room("roomId", "floorId", "buildingId", "male", Room.RoomStatus.AVAILABLE));
            ((RoomsRecycleViewAdapter) mAdapter).setRoomList(dummyData);
            mRecyclerView.setAdapter(mAdapter);
        }
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
    public void onSubscriptionStatusChanged(String roomId, boolean subscribed) {

        // subscribe or unscubscribe the user for the status related to the room
        try {
            User currentUser = new ObjectMapper().readValue(currentUserJson, User.class);
            AsyncSubscriptionTask subscriptionTask = new AsyncSubscriptionTask(currentUser.getUserId(), roomId, subscribed);
            subscriptionTask.execute();
        } catch (IOException e) {
            Log.e(TAG, String.format("unable to change subscription status on room: [%s]", roomId));
        }
    }
}
