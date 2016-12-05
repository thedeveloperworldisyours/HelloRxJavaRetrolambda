package com.thedeveloperworldisyours.hellorxjava;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import java.util.List;
import java.util.concurrent.Callable;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AsynchronousActivity extends AppCompatActivity {
    private Subscription mTvShowSubscription;

    @BindView(R.id.asynchronous_act_tv_show_list)
    public RecyclerView mTvShowListView;

    @BindView(R.id.asynchronous_act_loader)
    public ProgressBar mProgressBar;

    private SimpleStringAdapter mSimpleStringAdapter;
    private RestClient mRestClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mRestClient = new RestClient(this);
        configureLayout();
        createObservable();
    }

    /**
     * If we use it with Observable.just(),
     * mRestClient.getFavoriteTvShows() will be evaluated immediately and block the UI thread.
     * Enter the Observable.fromCallable() method. It gives us two important things:
     * The code for creating the emitted value is not run until someone subscribes to the Observer.
     * The creation code can be run on a different thread.
     */
    private void createObservable() {

        Observable<List<String>> tvShowObservable = Observable.fromCallable(() -> mRestClient.getFavoriteTvShows());
        mTvShowSubscription = tvShowObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
                (List<String> tvShows) -> displayTvShows(tvShows),
                (error) -> {},
                () -> {});
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mTvShowSubscription != null && !mTvShowSubscription.isUnsubscribed()) {
            mTvShowSubscription.unsubscribe();
        }
    }

    private void displayTvShows(List<String> tvShows) {
        mSimpleStringAdapter.setStrings(tvShows);
        mProgressBar.setVisibility(View.GONE);
        mTvShowListView.setVisibility(View.VISIBLE);
    }

    private void configureLayout() {
        setContentView(R.layout.asynchronous_act);
        ButterKnife.bind(this);

        mTvShowListView.setLayoutManager(new LinearLayoutManager(this));
        mSimpleStringAdapter = new SimpleStringAdapter(this);
        mTvShowListView.setAdapter(mSimpleStringAdapter);
    }
}
