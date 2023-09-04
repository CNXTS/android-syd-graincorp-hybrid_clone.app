package com.webling.graincorp.data.firebase;

import com.webling.graincorp.GrainCorpAppComponent;
import com.webling.graincorp.util.scopes.GraincorpScope;

import dagger.Component;

@GraincorpScope
@Component(dependencies = GrainCorpAppComponent.class)
public interface FirebaseServiceComponent {
    void inject(GrainCorpFirebaseMessagingService service);
}
