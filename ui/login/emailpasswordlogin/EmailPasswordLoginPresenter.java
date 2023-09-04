package com.webling.graincorp.ui.login.emailpasswordlogin;

import android.text.TextUtils;

import com.webling.graincorp.data.source.GrainCorpRepository;
import com.webling.graincorp.util.Util;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

import static com.webling.graincorp.util.Util.checkNotNull;

/**
 * @author Artaza Aziz
 */
public class EmailPasswordLoginPresenter implements EmailPasswordLoginContract.Presenter {

    private final EmailPasswordLoginContract.View view;
    private final GrainCorpRepository repository;
    private CompositeDisposable disposable = new CompositeDisposable();

    @Inject
    public EmailPasswordLoginPresenter(EmailPasswordLoginContract.View view, GrainCorpRepository repository) {
        this.view = checkNotNull(view);
        this.repository = checkNotNull(repository);
    }

    @Override
    public void subscribe() {
        String savedEmail = repository.getEmail();
        if (!savedEmail.isEmpty()) {
            view.setEmailViewText(savedEmail);
        }
    }

    @Override
    public void unsubscribe() {
        disposable.clear();
    }

    @Override
    public void onSignIn(String email, String password) {
        if (!Util.isValidEmail(email) || TextUtils.isEmpty(password)) {
            view.showInvalidDetailsError();
        } else {
            view.login(email, password);
        }
    }

    @Override
    public void onShowMaxPinFailAttemptsError(boolean showMaxPinFailAttemptsError) {
        if (showMaxPinFailAttemptsError) {
            view.showMaxPinFailAttemptsError();
        }
    }

    @Override
    public void onPasswordChangedError(boolean passwordChangedError) {
        if (passwordChangedError) {
            view.showPasswordChangedError();
        }
    }
}