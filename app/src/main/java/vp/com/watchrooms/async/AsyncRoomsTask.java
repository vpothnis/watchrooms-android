package vp.com.watchrooms.async;

import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import vp.com.watchrooms.adapters.RoomsRecycleViewAdapter;
import vp.com.watchrooms.models.RoomList;
import vp.com.watchrooms.models.User;

import static java.lang.String.format;

/**
 * Created by vinaypothnis on 2015-01-04.
 */
public class AsyncRoomsTask extends AsyncTask<Void, Void, RoomList> {

    private static final String TAG = AsyncRoomsTask.class.getSimpleName();

    private int currentPage = 1;
    private int pageSize = 10;
    private String currentFloorId = null;
    private RoomList currentDataSet = null;
    private RoomsRecycleViewAdapter adapter = null;
    private RecyclerView recyclerView;
    private User currentUser = null;

    public AsyncRoomsTask(RoomsRecycleViewAdapter adapter, RecyclerView recyclerView, String floorID, String currentUserJson) {
        this.adapter = adapter;
        this.recyclerView = recyclerView;
        this.currentFloorId = floorID;
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
            Log.v(TAG, "Trying to load the rooms in background");

            // If the user is logged in, we will try to merge his subscription information as well when we retrieve the room list
            String currentUserId = (currentUser != null ? currentUser.getUserId() : null);
            String url = format("http://10.0.2.2:9000/v1/rooms?floorId=%s&page=%s&size=%s&userId=%s", currentFloorId, currentPage, pageSize, currentUserId);
            RestTemplate template = new RestTemplate();
            currentDataSet = template.getForObject(url, RoomList.class);
            Log.v(TAG, format("Total rooms count: [%s] Number of Rooms in page [%s] = [%s]", currentDataSet.getCount(), currentPage, currentDataSet.getRooms().size()));
        } catch (Exception e) {
            Log.e(TAG, "Unable to load rooms from backend server", e);
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
