package com.webling.graincorp.ui.login.emailpasswordlogin;

import dagger.Module;
import dagger.Provides;

/**
 * @author Artaza Aziz
 */
@Module
public class EmailPasswordLoginModule {
    private EmailPasswordLoginContract.View view;

    public EmailPasswordLoginModule() {
    }

    EmailPasswordLoginModule(EmailPasswordLoginContract.View view) {
        this.view = view;
    }

    @Provides
    EmailPasswordLoginContract.View providesView() {
        return view;
    }

    @Provides
    EmailPasswordLoginContract.Presenter providesPresenter(EmailPasswordLoginPresenter presenter) {
        return presenter;
    }
}
