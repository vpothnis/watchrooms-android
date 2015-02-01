package vp.com.watchrooms.async;

import android.os.AsyncTask;
import android.util.Log;

import org.springframework.web.client.RestTemplate;

import vp.com.watchrooms.models.Room;
import vp.com.watchrooms.models.SubscriptionList;

import static java.lang.String.format;

/**
 * Created by vinaypothnis on 2015-01-04.
 */
public class AsyncUpdateRoomStatusTask extends AsyncTask<Void, Void, Boolean> {

    private static final String TAG = AsyncUpdateRoomStatusTask.class.getSimpleName();

    private Room.RoomStatus roomStatus;
    private String roomId;

    public AsyncUpdateRoomStatusTask(String roomId, Room.RoomStatus status) {
        this.roomStatus = status;
        this.roomId = roomId;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            Log.v(TAG, format("Trying to update status of room [%s] to [%s]", roomId, roomStatus));
            String url = format("http://10.0.2.2:9000/v1/rooms/%s/status/%s", roomId, roomStatus);
            RestTemplate template = new RestTemplate();
            template.put(url, null, SubscriptionList.class);
            Log.v(TAG, format("Room status change for room [%s] and status [%s] successful", roomId, roomStatus));
            return true;
        } catch (Exception e) {
            Log.e(TAG, format("Unable to change room status for room [%s] and status [%s]", roomId, roomStatus), e);
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {

    }
}
