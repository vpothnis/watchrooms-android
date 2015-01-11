package vp.com.watchrooms.async;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.springframework.web.client.RestTemplate;

import vp.com.watchrooms.adapters.FloorsRecycleViewAdapter;
import vp.com.watchrooms.models.FloorList;

import static java.lang.String.format;

/**
 * Created by vinaypothnis on 2015-01-04.
 */
public class AsyncFloorsTask extends AsyncTask<Void, Void, FloorList> {

    private static final String TAG = AsyncFloorsTask.class.getSimpleName();

    private int currentPage = 1;
    private int pageSize = 10;
    private String currentBuildingId = null;
    private FloorList currentDataSet = null;
    private FloorsRecycleViewAdapter adapter = null;
    private RecyclerView recyclerView;

    public AsyncFloorsTask(FloorsRecycleViewAdapter adapter, RecyclerView recyclerView, String buildingId) {
        this.adapter = adapter;
        this.recyclerView = recyclerView;
        this.currentBuildingId = buildingId;
    }

    @Override
    protected FloorList doInBackground(Void... params) {
        try {
            Log.v(TAG, "Trying to load the floors in background");

            String url = format("http://10.0.2.2:9000/v1/floors?buildingId=%s&page=%s&size=%s", currentBuildingId, currentPage, pageSize);
            RestTemplate template = new RestTemplate();
            currentDataSet = template.getForObject(url, FloorList.class);
            Log.v(TAG, format("Total floor count: [%s] Number of Floors in page [%s] = [%s]", currentDataSet.getCount(), currentPage, currentDataSet.getFloors().size()));
        } catch (Exception e) {
            Log.e(TAG, "Unable to load floors from backend server", e);
        }
        return currentDataSet;
    }

    @Override
    protected void onPostExecute(FloorList floorList) {
        super.onPostExecute(floorList);
        adapter.setFloorList(floorList);
        recyclerView.setAdapter(adapter);
    }
}
