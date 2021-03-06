package com.openclassrooms.entrevoisins.ui.neighbour_list;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.openclassrooms.entrevoisins.R;
import com.openclassrooms.entrevoisins.di.DI;
import com.openclassrooms.entrevoisins.events.DeleteNeighbourEvent;
import com.openclassrooms.entrevoisins.events.DetailNeighbourEvent;
import com.openclassrooms.entrevoisins.events.FavoriteNeighbourEvent;
import com.openclassrooms.entrevoisins.model.Neighbour;
import com.openclassrooms.entrevoisins.service.NeighbourApiService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

public class NeighbourFragment extends Fragment {

    private static NeighbourApiService mApiService;
    private List<Neighbour> mNeighbours;
    private RecyclerView mRecyclerView;
    private boolean favoriteTab;
    private boolean isCurrentPage;

    /**
     * Create and return a new instance
     * @return @{@link NeighbourFragment}
     */
    public static NeighbourFragment newInstance(boolean favoriteTab) {
        NeighbourFragment fragment = new NeighbourFragment();
        fragment.favoriteTab=favoriteTab;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApiService = DI.getNeighbourApiService();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_neighbour_list, container, false);
        Context context = view.getContext();
        mRecyclerView = (RecyclerView) view;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        return view;
    }

    /**
     * Init the List of neighbours
     */

    private void initList() {
        if (favoriteTab) {
            mNeighbours = mApiService.getNeighboursFavorite();
        } else {
            mNeighbours = mApiService.getNeighbours();
        }
        mRecyclerView.setAdapter(new MyNeighbourRecyclerViewAdapter(mNeighbours));
    }

    @Override
    public void onResume() {
        super.onResume();
        initList();
    }

    @Override
    public void onStart() {
        super.onStart();
        //if (!favoriteTab){
            EventBus.getDefault().register(this);
        //}
    }

   @Override
    public void onStop() {
        super.onStop();
        //if (!favoriteTab){
            EventBus.getDefault().unregister(this);
        //}
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            isCurrentPage = true;
        } else
        {
            isCurrentPage = false;
        }
    }


    /**
     * Fired if the user clicks on a delete button
     * @param event
     */
    @Subscribe
    public void onDeleteNeighbour(DeleteNeighbourEvent event) {
        mApiService.deleteNeighbour(event.neighbour);
        initList();
    }

    /**
     * View neighbour details if the user clicks on a item
     * @param event
     */
    @Subscribe
    public void onDetailNeighbour(DetailNeighbourEvent event) {
        Neighbour n = event.neighbour;
        if (isCurrentPage) {
            Intent intent = new Intent(this.getContext(), DetailNeighbourActivity.class);
            intent.putExtra(DetailNeighbourActivity.NEIGHBOUR_KEY, n);
            ActivityCompat.startActivity(this.getContext(), intent, null);
        }
    }

}
