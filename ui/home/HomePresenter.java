package com.webling.graincorp.ui.home;

import com.webling.graincorp.data.exception.MaintenanceModeException;
import com.webling.graincorp.data.source.GrainCorpRepository;
import com.webling.graincorp.model.Bid;
import com.webling.graincorp.model.GlobalSettings;
import com.webling.graincorp.model.UserInfo;
import com.webling.graincorp.rxbus.BehaviourRxBus;
import com.webling.graincorp.rxbus.RxBus;
import com.webling.graincorp.rxbus.event.EventTypeDef;
import com.webling.graincorp.rxbus.event.GlobalEvent;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;

import static com.webling.graincorp.util.RxUtil.applyFlowableSchedulers;
import static com.webling.graincorp.util.RxUtil.applySchedulers;
import static com.webling.graincorp.util.Util.checkNotNull;

public class HomePresenter implements HomeContract.Presenter {
    final GrainCorpRepository repository;
    private RxBus<GlobalEvent> globalEventRxBus;
    private BehaviourRxBus<GlobalEvent> globalEventBehaviourRxBus;
    final HomeContract.View view;
    private final CompositeDisposable compositeDisposable;
    private volatile boolean isRegistered;

    @Inject
    public HomePresenter(GrainCorpRepository repository, HomeContract.View view, RxBus<GlobalEvent> globalEventRxBus, BehaviourRxBus<GlobalEvent> globalEventBehaviourRxBus) {
        this.view = checkNotNull(view);
        this.repository = checkNotNull(repository);
        this.globalEventRxBus = globalEventRxBus;
        this.globalEventBehaviourRxBus = globalEventBehaviourRxBus;
        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public void subscribe() {
        compositeDisposable.add(repository.getUserInfofromDBAsObservable()
                .compose(applySchedulers())
                .subscribeWith(new DisposableObserver<UserInfo>() {
                    @Override
                    public void onNext(@NonNull UserInfo userInfo) {
                        view.showHideDeliverySummarySection(!userInfo.isFreightProviderOnly());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                }));


        compositeDisposable.add(globalEventRxBus.registerAsFlowable(EventTypeDef.TYPE_OFFLINE_DIALOG_RETRIED)
                .compose(applyFlowableSchedulers())
                .subscribe(event -> loadSavedSearches(), err -> {
                }));

        compositeDisposable.add(globalEventRxBus.registerAsFlowable(EventTypeDef.TYPE_MAINTENANCE_MODE_DIALOG_DISMISS)
                .compose(applyFlowableSchedulers())
                .subscribe(event -> loadSavedSearches(), err -> {
                }));

        loadSavedSearches();
    }

    @Override
    public void unsubscribe() {
        compositeDisposable.clear();
    }

    @Override
    public void loadSavedSearches() {
        if(repository.getSearchFilterListSize()) {
            compositeDisposable.add(Observable.just(true) //bubble the errors here
                    .flatMap(aBool -> repository.getBids(), true)
                    .compose(applySchedulers(true))
                    .doOnSubscribe(disposable -> view.showProgressBar())
                    .doFinally(view::hideProgressBar)
                    .subscribeWith(new DisposableObserver<List<Bid>>() {
                        @Override
                        public void onNext(@NonNull List<Bid> bids) {
                            view.showSavedSearches(bids);
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            if (e instanceof MaintenanceModeException) {
                                globalEventRxBus.send(new GlobalEvent(EventTypeDef.TYPE_MAINTENANCE_MODE_DIALOG_SHOW));
                            } else
                                globalEventRxBus.send(new GlobalEvent(EventTypeDef.TYPE_SHOW_OFFLINE_DIALOG));
                        }

                        @Override
                        public void onComplete() {
                            registerSearchFiltersSavedEvent();
                        }
                    }));
        }
    }

    private void registerSearchFiltersSavedEvent() {
        if (!isRegistered) { // Avoid infinite loop, since its called in onNext of loadSavedSearches... which is called here and also this method is also called by
            compositeDisposable.add(globalEventBehaviourRxBus.registerAsFlowable(EventTypeDef.SEARCH_FILTERS_SAVED)
                    .doOnSubscribe(d -> isRegistered = true)
                    .compose(applyFlowableSchedulers())
                    .subscribe(event -> {
                                if (!repository.isSavingSearchFilters()) {
                                    loadSavedSearches();
                                }
                            },
                            err -> {
                            }));
        }
    }

    @Override
    public void getAllBidUrlAndNotify() {
        compositeDisposable.add(repository.getGlobalSettingsAsObservable().compose(applySchedulers()).subscribeWith(new DisposableObserver<GlobalSettings>() {
            @Override
            public void onNext(@NonNull GlobalSettings globalSettings) {
                view.navigateToWebview("https://" + globalSettings.getCcDomain() + globalSettings.getAllBidUrl());
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        }));
    }

    @Override
    public void getDeliverySummaryUrlAndNotify() {
        compositeDisposable.add(repository.getGlobalSettingsAsObservable().compose(applySchedulers()).subscribeWith(new DisposableObserver<GlobalSettings>() {
            @Override
            public void onNext(@NonNull GlobalSettings globalSettings) {
                view.navigateToWebview("https://" + globalSettings.getCcDomain() + globalSettings.getGrowerDeliverySummaryUrl());
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        }));
    }

    @Override
    public void getbidUrlAndNotify(Bid bid) {
        compositeDisposable.add(repository.getGlobalSettingsAsObservable().compose(applySchedulers()).subscribeWith(new DisposableObserver<GlobalSettings>() {
            @Override
            public void onNext(@NonNull GlobalSettings globalSettings) {
                view.navigateToWebview("https://" + globalSettings.getCcDomain() + globalSettings.getAllBidUrl() + String.format("?site=%s&grade=%s", bid.getSiteNo(), bid.getGrade()));
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        }));
    }
}
