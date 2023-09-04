package com.webling.graincorp.data.api.converters.nested;

import androidx.annotation.Nullable;

import com.google.gson.reflect.TypeToken;
import com.webling.graincorp.data.api.model.response.NestedEnvelope;
import com.webling.graincorp.data.api.model.response.notificationsSettings.Group;
import com.webling.graincorp.data.api.model.response.notificationsSettings.Item;
import com.webling.graincorp.model.NotificationSettingsGroup;
import com.webling.graincorp.model.NotificationSettingsItem;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NotificationSettingsDenvelopingConverter extends Converter.Factory {

    private GsonConverterFactory gsonConverterFactory;

    public NotificationSettingsDenvelopingConverter(GsonConverterFactory gsonConverterFactory) {
        this.gsonConverterFactory = gsonConverterFactory;
    }

    @Nullable
    @Override
    public Converter<ResponseBody, List<NotificationSettingsGroup>> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {

        Type envelopedType = TypeToken.getParameterized(NestedEnvelope.class, Group.class).getType();

        Converter<ResponseBody, ?> gsonResponseBodyConverter =
                gsonConverterFactory.responseBodyConverter(envelopedType, annotations, retrofit);
        return value -> {
            NestedEnvelope<Group> envelope = (NestedEnvelope<Group>) gsonResponseBodyConverter.convert(value);

            List<NotificationSettingsGroup> notificationSettingsGroups = new ArrayList<>();

            List<Group> settingGroups = envelope.getObject().getResults();
            for (Group settingGroup : settingGroups) {
                NotificationSettingsGroup group = new NotificationSettingsGroup();

                group.setUserDeviceId(settingGroup.getUserDeviceId());
                group.setPnGroupId(settingGroup.getPnGroupId());
                group.setPnGroupName(settingGroup.getPnGroupName());
                group.setMasterPnId(settingGroup.getMasterPnId());

                List<Item> settingitems = settingGroup.getSettingsGroupItemSet().getResults();
                List<NotificationSettingsItem> notificationSettingsItems = new ArrayList<>();
                for (Item settingItem : settingitems) {
                    NotificationSettingsItem item = new NotificationSettingsItem();
                    item.setUserDeviceID(settingItem.getUserDeviceId());
                    item.setPnGroupId(settingItem.getPnGroupId());
                    item.setActive(settingItem.isActive());
                    item.setPnConfigUrl(settingItem.getPnConfigUrl());
                    item.setPnDescription(settingItem.getPnDescription());
                    item.setPnId(settingItem.getPnId());
                    item.setPnPhoneNo(settingItem.getPnPhoneNo());
                    item.setPnTitle(settingItem.getPnTitle());
                    item.setMasterPnId(settingGroup.getMasterPnId());
                    notificationSettingsItems.add(item);
                }
                group.setNotificationSettingsItems(notificationSettingsItems);
                notificationSettingsGroups.add(group);
            }
            return notificationSettingsGroups;
        };
    }
}

