package com.webling.graincorp.ui.login.emailpasswordlogin;

import com.webling.graincorp.ui.base.BasePresenter;
import com.webling.graincorp.ui.base.BaseView;

public interface EmailPasswordLoginContract {

    interface View extends BaseView<Presenter> {

        void showProgress(boolean show);

        void showInvalidDetailsError();

        void showMaxPinFailAttemptsError();

        void showPasswordChangedError();

        void hideError();

        void setEmailViewText(String savedEmail);

        void login(String email, String password);
    }

    interface Presenter extends BasePresenter {

        void onSignIn(String email, String password);

        void onShowMaxPinFailAttemptsError(boolean showMaxPinFailAttemptsError);

        void onPasswordChangedError(boolean passwordChangedError);
    }
}
