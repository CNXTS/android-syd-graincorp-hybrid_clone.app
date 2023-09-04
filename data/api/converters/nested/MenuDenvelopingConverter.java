package com.webling.graincorp.data.api.converters.nested;

import androidx.annotation.Nullable;

import com.google.gson.reflect.TypeToken;
import com.webling.graincorp.data.api.model.response.NestedEnvelope;
import com.webling.graincorp.data.api.model.response.menu.Child;
import com.webling.graincorp.data.api.model.response.menu.Parent;
import com.webling.graincorp.model.MenuChildItem;
import com.webling.graincorp.model.MenuParentGroup;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MenuDenvelopingConverter extends Converter.Factory {

    private GsonConverterFactory gsonConverterFactory;

    public MenuDenvelopingConverter(GsonConverterFactory gsonConverterFactory) {
        this.gsonConverterFactory = gsonConverterFactory;
    }

    @Nullable
    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        Type envelopedType = TypeToken.getParameterized(NestedEnvelope.class, Parent.class).getType();

        Converter<ResponseBody, ?> gsonResponseBodyConverter =
                gsonConverterFactory.responseBodyConverter(envelopedType, annotations, retrofit);
        return value -> {
            NestedEnvelope<Parent> envelope = (NestedEnvelope<Parent>) gsonResponseBodyConverter.convert(value);

            List<MenuParentGroup> menuParentGroups = new ArrayList<>();

            List<Parent> parentGroups = envelope.getObject().getResults();
            for (Parent parentGroup : parentGroups) {
                MenuParentGroup group = new MenuParentGroup();

                group.setParentId(parentGroup.getParentId());
                group.setAccountNo(parentGroup.getAccountNo());
                group.setTitle(parentGroup.getTitle());
                group.setUserType(parentGroup.getUserType());

                List<Child> childItems = parentGroup.getMenuChildSet().getResults();
                List<MenuChildItem> menuChildItems = new ArrayList<>();
                for (Child childItem : childItems) {
                    MenuChildItem item = new MenuChildItem();
                    item.setTitle(childItem.getTitle());
                    item.setParentId(childItem.getParentId());
                    item.setId(childItem.getId());
                    item.setIntExtInd(childItem.getIntExtInd());
                    item.setUrl(childItem.getUrl());

                    menuChildItems.add(item);
                }
                group.setMenuChildItems(menuChildItems);
                menuParentGroups.add(group);
            }
            return menuParentGroups;
        };
    }
}

