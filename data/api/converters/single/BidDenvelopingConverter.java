package com.webling.graincorp.data.api.converters.single;

import androidx.annotation.NonNull;
import android.util.Log;

import com.webling.graincorp.data.api.model.response.SingleEnvelope;
import com.webling.graincorp.model.Bid;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Converter;

public class BidDenvelopingConverter implements Converter<ResponseBody, List<Bid>> {
    private final String TAG = getClass().getSimpleName();
    final Converter<ResponseBody, SingleEnvelope<Bid>> delegate;

    public BidDenvelopingConverter(Converter<ResponseBody, SingleEnvelope<Bid>> delegate) {
        this.delegate = delegate;
    }

    @Override
    public List<Bid> convert(@NonNull ResponseBody responseBody) {
        List<Bid> bids = new ArrayList<>();
        String boundaryParameter = "boundary=";
        String crlf = "\r\n";
        String boundary;
        try {
            MediaType mediaType = responseBody.contentType();
            String response = responseBody.string();
            int crlfLength = crlf.length();

            //Get the boundary from the header if present or directly from response body
            if (mediaType != null) {
                String contentType = mediaType.toString();
                boundary = "--" + contentType.substring(contentType.indexOf(boundaryParameter) + boundaryParameter.length());
            } else {
                boundary = response.substring(response.indexOf("--"), response.indexOf(crlf));
            }

            while (!response.isEmpty() && !response.equals(crlf)) { // There is a trailing crlf after the last boundary
                try {
                    int jsonObjBeginIndex = response.indexOf('{');
                    int boundaryIndexAfterJsonObj = response.indexOf(boundary, jsonObjBeginIndex);
                    String bidJsonString = response.substring(jsonObjBeginIndex, boundaryIndexAfterJsonObj);
                    SingleEnvelope<Bid> envelope = delegate.convert(ResponseBody.create(MediaType.parse("application/json"), bidJsonString));
                    if (envelope.getObject() != null) {
                        bids.add(envelope.getObject());
                    } else {
                        //Error json object
                        Bid bid = new Bid();
                        bid.setErrorState(true);
                        bids.add(bid);
                    }
                    // trim the response by using the boundary that we have just consumed
                    // + crlfLength also works for the last boundary's trailing "--"
                    response = response.substring(response.indexOf(boundary, response.indexOf('{')) + boundary.length() + crlfLength, response.length());
                } catch (IndexOutOfBoundsException e) {
                    Log.e(TAG, "Exception", e);
                }
            }
        } catch (IndexOutOfBoundsException | IOException e) {
            Log.e(TAG, "Exception", e);
        }
        return bids;
    }
}
