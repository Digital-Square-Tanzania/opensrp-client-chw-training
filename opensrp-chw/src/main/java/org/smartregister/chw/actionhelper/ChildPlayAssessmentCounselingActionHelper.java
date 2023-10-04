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

public class ChildPlayAssessmentCounselingActionHelper extends HomeVisitActionHelper {
    private final Context context;

    private final String visitId;

    private final Map<String, Boolean> visitNumberMap = new HashMap<>();

    private final ServiceWrapper serviceWrapper;

    private String jsonString;

    public ChildPlayAssessmentCounselingActionHelper(Context context, String visitId, ServiceWrapper serviceWrapper) {
        this.context = context;
        this.visitId = visitId;
        this.serviceWrapper = serviceWrapper;
    }

    @Override
    public void onPayloadReceived(String s) {

    }

    @Override
    public void onJsonFormLoaded(String jsonString, Context context, Map<String, List<VisitDetail>> details) {
        this.jsonString = jsonString;
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
    public String evaluateSubTitle() {
        return null;
    }

    @Override
    public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
        return BaseAncHomeVisitAction.Status.COMPLETED;
    }

    private void populateVisitNumber() {
        int visitNumber = visitNumber();
        if (visitNumber == 2) {
            visitNumberMap.put("visit_2_visit_25", true);
        } else if (visitNumber >= 3 && visitNumber <= 16) {
            visitNumberMap.put("visit_3_visit_16", true);
            visitNumberMap.put("visit_3_visit_17", true);
            visitNumberMap.put("visit_2_visit_25", true);
            visitNumberMap.put("visit_3_visit_25", true);
        } else if (visitNumber == 17) {
            visitNumberMap.put("visit_3_visit_17", true);
            visitNumberMap.put("visit_2_visit_25", true);
            visitNumberMap.put("visit_3_visit_25", true);
            visitNumberMap.put("visit_17_visit_27", true);
        } else if (visitNumber > 17 && visitNumber <= 25) {
            visitNumberMap.put("visit_2_visit_25", true);
            visitNumberMap.put("visit_3_visit_25", true);
            visitNumberMap.put("visit_17_visit_27", true);
        } else if (visitNumber < 25) {
            visitNumberMap.put("visit_17_visit_27", true);
        }
    }

    private int visitNumber() {
        if (this.visitId != null) {
            return getPncHomeVisitNumber();
        } else {
            return getChildHomeVisitNumber();
        }
    }

    private int getPncHomeVisitNumber() {
        switch (this.visitId) {
            case "1":
                return 1;
            case "3":
                return 2;
            case "8":
                return 3;
            case "21 - 27":
                return 4;
            case "35 - 41":
                return 5;
            default:
                return 0;
        }
    }

    private int getChildHomeVisitNumber() {
        final Pattern lastIntPattern = Pattern.compile("[^0-9]+([0-9]+)$");
        Matcher matcher = lastIntPattern.matcher(serviceWrapper.getName());
        if (matcher.find()) {
            String someNumberStr = matcher.group(1);
            if(someNumberStr != null){
                return Integer.parseInt(someNumberStr);
            }
        }
        return 0;
    }
}
