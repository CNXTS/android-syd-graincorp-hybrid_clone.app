package com.webling.graincorp.ui.home;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.webling.graincorp.GrainCorpApp;
import com.webling.graincorp.R;
import com.webling.graincorp.constants.Analytics;
import com.webling.graincorp.databinding.FragmentHomeBinding;
import com.webling.graincorp.model.Bid;
import com.webling.graincorp.provider.AnalyticsProvider;
import com.webling.graincorp.ui.decorators.HorizontalSpaceItemDecoration;
import com.webling.graincorp.ui.decorators.ItemClickSupport;
import com.webling.graincorp.ui.home.adapter.HomeAdapter;
import com.webling.graincorp.ui.main.MainActivity;
import com.webling.graincorp.ui.webview.WebviewFragment;
import com.webling.graincorp.util.AnimUtil;

import java.util.List;

import javax.inject.Inject;

public class HomeFragment extends Fragment implements HomeContract.View {

    @Inject
    HomeAdapter adapter;
    @Inject
    HomeContract.Presenter presenter;
    @Inject
    HomeContract.View view;
    @Inject
    AnalyticsProvider analyticsProvider;

    private FragmentHomeBinding binding;
    private CardView homeAddNow;
    private TextView homeNewBid;
    private TextView viewDelivery;
    private LinearLayout homeDeliverySummarySection;
    private RecyclerView homeRecyclerView;
    private ProgressBar homeProgressbar;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DaggerHomeComponent
                .builder()
                .grainCorpAppComponent(((GrainCorpApp) requireActivity().getApplication()).getComponent())
                .homeModule(new HomeModule(this, getActivity()))
                .build()
                .inject(this);
        analyticsProvider.setCurrentScreen(getActivity(), Analytics.ScreenNames.HOME);
    }

    @Override
    public void onStart() {
        super.onStart();
        homeRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.subscribe();
    }

    @Override
    public void onPause() {
        super.onPause();
        presenter.unsubscribe();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        initViews();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        homeAddNow.setOnClickListener(this::showAllBidsClicked);
        homeNewBid.setOnClickListener(this::showAllBidsClicked);
        viewDelivery.setOnClickListener(view1 -> onViewDeliveryClicked());

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        homeRecyclerView.setLayoutManager(layoutManager);
        homeRecyclerView.setHasFixedSize(true);

        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        int widthPixels = (int) (metrics.widthPixels * 0.1);
        homeRecyclerView.addItemDecoration(new HorizontalSpaceItemDecoration(widthPixels));

        homeRecyclerView.setNestedScrollingEnabled(true);

        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(homeRecyclerView);

        ItemClickSupport.addTo(homeRecyclerView).setOnItemClickListener((recyclerView, position, v) -> {
            if (position != RecyclerView.NO_POSITION) {
                Bid bid = adapter.getItems().get(position);
                presenter.getbidUrlAndNotify(bid);
            }
        });
    }

    private void initViews() {
        homeAddNow = binding.homeAddNow;
        homeNewBid = binding.homeNewBid;
        viewDelivery = binding.viewDelivery;
        homeDeliverySummarySection = binding.homeDeliverySummarySection;
        homeRecyclerView = binding.homeRecyclerView;
        homeProgressbar = binding.homeProgressbar;
    }

    private void showAllBidsClicked(View v) {
        if (v.getId() == R.id.home_add_now) {
            analyticsProvider.trackEvent(Analytics.CustomEvents.TAP_HOME_ADD_NOW);
        } else if (v.getId() == R.id.home_new_bid) {
            analyticsProvider.trackEvent(Analytics.CustomEvents.TAP_NEW_BID_PRICE_SEARCH);
        }
        presenter.getAllBidUrlAndNotify();
    }

    public void onViewDeliveryClicked() {
        analyticsProvider.trackEvent(Analytics.CustomEvents.TAP_VIEW_DELIVERIES);
        presenter.getDeliverySummaryUrlAndNotify();
    }

    @Override
    public void showSavedSearches(List<Bid> bids) {
        adapter.setItems(bids);
        adapter.notifyDataSetChanged();
        if (homeRecyclerView != null && homeAddNow != null) {
            if (bids.size() <= 0) {
                homeRecyclerView.setVisibility(View.GONE);
                homeAddNow.setVisibility(View.VISIBLE);
            } else {
                homeAddNow.setVisibility(View.GONE);
                homeRecyclerView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void showProgressBar() {
        AnimUtil.showProgress(true, null, homeProgressbar);
    }

    @Override
    public void hideProgressBar() {
        AnimUtil.showProgress(false, null, homeProgressbar);
    }

    @Override
    public void navigateToWebview(String url) {
        ((MainActivity) requireActivity()).navigateToFragmentAndSelectOtherTab(
                WebviewFragment.newInstance(url),
                R.id.tab_menu,
                false
        );
    }

    @Override
    public void showHideDeliverySummarySection(boolean show) {
        int visibility = show ? View.VISIBLE : View.GONE;
        homeDeliverySummarySection.setVisibility(visibility);
    }
}
