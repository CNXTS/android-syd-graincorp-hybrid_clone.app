package com.webling.graincorp.ui.home;

import com.webling.graincorp.model.Bid;
import com.webling.graincorp.ui.base.BasePresenter;
import com.webling.graincorp.ui.base.BaseView;

import java.util.List;

public interface HomeContract {

    interface View extends BaseView<HomeContract.Presenter> {

        void showSavedSearches(List<Bid> bids);

        void showProgressBar();

        void hideProgressBar();

        void navigateToWebview(String url);

        void showHideDeliverySummarySection(boolean show);
    }

    interface Presenter extends BasePresenter {
        void loadSavedSearches();

        void getAllBidUrlAndNotify();

        void getDeliverySummaryUrlAndNotify();

        void getbidUrlAndNotify(Bid bid);
    }
}
