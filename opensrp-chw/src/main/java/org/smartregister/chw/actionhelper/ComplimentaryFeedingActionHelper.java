package org.smartregister.chw.actionhelper;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.anc.actionhelper.HomeVisitActionHelper;
import org.smartregister.chw.anc.domain.VisitDetail;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;
import org.smartregister.chw.anc.util.JsonFormUtils;
import org.smartregister.immunization.domain.ServiceWrapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import timber.log.Timber;

public class ComplimentaryFeedingActionHelper extends HomeVisitActionHelper {
    ServiceWrapper serviceWrapper;
    private final Map<String, Boolean> visitNumberMap = new HashMap<>();
    private String jsonString;


    public ComplimentaryFeedingActionHelper(ServiceWrapper serviceWrapper) {
        this.serviceWrapper = serviceWrapper;
    }

    @Override
    public String getPreProcessed() {
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray fields = JsonFormUtils.fields(jsonObject);
            populateVisitNumber();
            for (Map.Entry<String, Boolean> entry : visitNumberMap.entrySet()) {
                if (entry.getValue()) {
                    JsonFormUtils.getFieldJSONObject(fields, entry.getKey()).put("value", "true");
                }
            }
            return jsonObject.toString();
        } catch (JSONException e) {
            Timber.e(e);
        }
        return super.getPreProcessed();
    }

    @Override
    public void onPayloadReceived(String jsonPayload) {
        //Update the form before showing to the user
    }

    @Override
    public void onJsonFormLoaded(String jsonString, Context context, Map<String, List<VisitDetail>> details) {
        this.jsonString = jsonString;
    }

    @Override
    public String evaluateSubTitle() {
        return "";
    }

    @Override
    public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
        return BaseAncHomeVisitAction.Status.COMPLETED;
    }

    private int getChildHomeVisitNumber() {
        final Pattern lastIntPattern = Pattern.compile("[^0-9]+([0-9]+)$");
        Matcher matcher = lastIntPattern.matcher(serviceWrapper.getName());
        if (matcher.find()) {
            String someNumberStr = matcher.group(1);
            if (someNumberStr != null) {
                return Integer.parseInt(someNumberStr);
            }
        }
        return 0;
    }

    private void populateVisitNumber() {
        int visitNumber = getChildHomeVisitNumber();
        if (visitNumber >= 8 && visitNumber <= 12) {
            visitNumberMap.put("visit_8_visit_12", true);
        }
    }
}
