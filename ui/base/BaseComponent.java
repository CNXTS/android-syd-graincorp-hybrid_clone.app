package com.webling.graincorp.ui.base;

import com.webling.graincorp.GrainCorpAppComponent;
import com.webling.graincorp.util.scopes.GraincorpScope;

import dagger.Component;

@GraincorpScope
@Component(dependencies = GrainCorpAppComponent.class)
public interface BaseComponent {
    void inject(BaseActivity baseActivity);
}