package vp.com.watchrooms.async;

import android.os.AsyncTask;
import android.util.Log;

import org.springframework.web.client.RestTemplate;

import vp.com.watchrooms.models.SubscriptionList;

import static java.lang.String.format;

/**
 * Created by vinaypothnis on 2015-01-04.
 */
public class AsyncSubscriptionTask extends AsyncTask<Void, Void, Boolean> {

    private static final String TAG = AsyncSubscriptionTask.class.getSimpleName();

    private String userId;
    private String roomId;
    private boolean subscribe;

    public AsyncSubscriptionTask(String userId, String roomId, boolean subscribe) {
        this.userId = userId;
        this.roomId = roomId;
        this.subscribe = subscribe;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            String subscriptionString = subscribe ? "subscribe" : "unsubscribe";
            Log.v(TAG, format("Trying to change the %s user[%s] from status updates of room [%s]", subscriptionString, userId, roomId));
            String url = format("http://10.0.2.2:9000/v1/rooms/%s/%s/%s", roomId, subscriptionString, userId);
            RestTemplate template = new RestTemplate();
            SubscriptionList result = template.postForObject(url, null, SubscriptionList.class);
            Log.v(TAG, format("Subscription status change for user [%s] and room [%s] successful", userId, roomId));
            return true;
        } catch (Exception e) {
            Log.e(TAG, format("Unable to change subscription status for user [%s] and room [%s]", userId, roomId), e);
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {

    }
}
