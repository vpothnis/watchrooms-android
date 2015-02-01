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
import vp.com.watchrooms.adapters.MySubscriptionsRecycleViewAdapter;
import vp.com.watchrooms.async.AsyncMySubscriptionsTask;
import vp.com.watchrooms.async.AsyncSubscriptionTask;
import vp.com.watchrooms.models.Room;
import vp.com.watchrooms.models.RoomList;
import vp.com.watchrooms.models.User;

import static java.lang.String.format;

public class MySubscriptionsActivity extends Activity implements MySubscriptionsRecycleViewAdapter.SubscriptionToggleClickListener {

    private static final String TAG = MySubscriptionsActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private String currentUserJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_subscriptions);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_subscriptions_recycler_view);

        // use this setting to improve performance if you know that changes in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        Intent callingIntent = getIntent();
        currentUserJson = callingIntent.getStringExtra(CommonConstants.EXTRA_CURRENT_USER);
        mAdapter = new MySubscriptionsRecycleViewAdapter(this, currentUserJson);

        if (!mRecyclerView.isInEditMode()) {
            // The Async Task will set the adapter to the recycler view after it has downloaded the initial data
            AsyncMySubscriptionsTask subscriptionsTask = new AsyncMySubscriptionsTask((MySubscriptionsRecycleViewAdapter) mAdapter, mRecyclerView, currentUserJson);
            subscriptionsTask.execute();
        } else {
            // set dummy data
            RoomList dummyData = new RoomList();
            dummyData.setCount(10L);
            dummyData.addRoom(new Room("roomId", "floorId", "buildingId", "male", Room.RoomStatus.AVAILABLE));
            ((MySubscriptionsRecycleViewAdapter) mAdapter).setRoomList(dummyData);
            mRecyclerView.setAdapter(mAdapter);
        }
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


    @Override
    public void onSubscriptionStatusChanged(String roomId, boolean subscribed) {

        // subscribe or unsubscribe the user for the status related to the room
        try {
            User currentUser = new ObjectMapper().readValue(currentUserJson, User.class);
            AsyncSubscriptionTask subscriptionTask = new AsyncSubscriptionTask(currentUser.getUserId(), roomId, subscribed);
            subscriptionTask.execute();
        } catch (IOException e) {
            Log.e(TAG, String.format("unable to change subscription status on room: [%s]", roomId));
        }
    }
}
