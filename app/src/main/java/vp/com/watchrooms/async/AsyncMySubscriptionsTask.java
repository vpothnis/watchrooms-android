package vp.com.watchrooms.async;

import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import vp.com.watchrooms.adapters.MySubscriptionsRecycleViewAdapter;
import vp.com.watchrooms.models.RoomList;
import vp.com.watchrooms.models.User;

import static java.lang.String.format;

/**
 * Created by vinaypothnis on 2015-01-04.
 */
public class AsyncMySubscriptionsTask extends AsyncTask<Void, Void, RoomList> {

    private static final String TAG = AsyncMySubscriptionsTask.class.getSimpleName();

    private RoomList currentDataSet = null;
    private MySubscriptionsRecycleViewAdapter adapter = null;
    private RecyclerView recyclerView;
    private User currentUser = null;

    public AsyncMySubscriptionsTask(MySubscriptionsRecycleViewAdapter adapter, RecyclerView recyclerView, String currentUserJson) {
        this.adapter = adapter;
        this.recyclerView = recyclerView;
        if (currentUserJson != null) {
            try {
                currentUser = new ObjectMapper().readValue(currentUserJson, User.class);
            } catch (IOException e) {
                Log.e(TAG, "Unable to parse current user json", e);
            }
        }
    }

    @Override
    protected RoomList doInBackground(Void... params) {
        try {
            Log.v(TAG, "Trying to load the user subscriptions in background");

            // get the logged in user's subscriptions (list of rooms that he has subscribed to)
            if (currentUser != null) {
                String currentUserId = currentUser.getUserId();
                String url = format("http://10.0.2.2:9000/v1/users/%s/subscriptions", currentUserId);
                RestTemplate template = new RestTemplate();
                currentDataSet = template.getForObject(url, RoomList.class);
                Log.v(TAG, format("Total subscription count: [%s]", currentDataSet.getCount()));
            } else {
                Log.e(TAG, "Logged in user information not available. Unable to load user subscriptions");
            }
        } catch (Exception e) {
            Log.e(TAG, "Unable to load user subscriptions from backend server", e);
        }
        return currentDataSet;
    }

    @Override
    protected void onPostExecute(RoomList roomList) {
        super.onPostExecute(roomList);
        adapter.setRoomList(roomList);
        recyclerView.setAdapter(adapter);
    }
}
