package vp.com.watchrooms.async;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.android.gms.drive.internal.ad;

import org.springframework.web.client.RestTemplate;

import vp.com.watchrooms.adapters.BuildingsRecycleViewAdapter;
import vp.com.watchrooms.models.BuildingList;

import static java.lang.String.format;

/**
 * Created by vinaypothnis on 2015-01-04.
 */
public class AsyncBuildingsTask extends AsyncTask<Void, Void, BuildingList> {

    private static final String TAG = AsyncBuildingsTask.class.getSimpleName();

    private Long totalBuildingCount;

    private int currentPage = 1;
    private int pageSize = 10;
    private BuildingList currentDataSet = null;
    private BuildingsRecycleViewAdapter adapter = null;
    private RecyclerView recyclerView;

    public AsyncBuildingsTask(BuildingsRecycleViewAdapter adapter, RecyclerView recyclerView) {
        this.adapter = adapter;
        this.recyclerView = recyclerView;
    }

    @Override
    protected BuildingList doInBackground(Void... params) {
        try {
            Log.v(TAG, "Trying to load the buildings in background");

            String url = format("http://10.0.2.2:9000/v1/buildings?page=%s&size=%s", currentPage, pageSize);
            RestTemplate template = new RestTemplate();
            currentDataSet = template.getForObject(url, BuildingList.class);
            Log.v(TAG, format("Total building count: [%s] Number of Buildings in page [%s] = [%s]", currentDataSet.getCount(), currentPage, currentDataSet.getBuildings().size()));
        } catch (Exception e) {
            Log.e(TAG, "Unable to load buildings from backend server", e);
        }
        return currentDataSet;
    }

    @Override
    protected void onPostExecute(BuildingList buildingList) {
        super.onPostExecute(buildingList);
        adapter.setBuildingList(buildingList);
        recyclerView.setAdapter(adapter);
    }
}
