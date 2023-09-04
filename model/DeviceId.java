package com.webling.graincorp.model;

import com.raizlabs.android.dbflow.annotation.ConflictAction;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.annotation.UniqueGroup;
import com.raizlabs.android.dbflow.structure.BaseModel;
import com.webling.graincorp.data.source.local.GrainCorpDatabase;

import java.util.UUID;

@Table(database = GrainCorpDatabase.class,
        uniqueColumnGroups = {@UniqueGroup(groupNumber = 1, uniqueConflict = ConflictAction.REPLACE)})
public class DeviceId extends BaseModel {
    @PrimaryKey
    @Unique(unique = true, uniqueGroups = 1, onUniqueConflict = ConflictAction.REPLACE)
    public UUID uuid;

    public DeviceId() {
       //Required empty constructor
    }


    public DeviceId(UUID uuid) {
        this.uuid = uuid;
    }

}
