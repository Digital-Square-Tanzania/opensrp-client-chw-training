package org.smartregister.chw.actionhelper;

import android.content.Context;

import com.vijay.jsonwizard.constants.JsonFormConstants;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.chw.anc.actionhelper.HomeVisitActionHelper;
import org.smartregister.chw.anc.domain.VisitDetail;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;
import org.smartregister.chw.util.JsonFormUtils;
import org.smartregister.domain.Alert;
import org.smartregister.immunization.domain.ServiceWrapper;

import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class ChildCommunicationAssessmentCounselingActionHelper extends HomeVisitActionHelper {
    private final int ageInMonth;

    private String jsonPayload;

    private String communicatesWithChild = "";

    private String communicatesWithChildObservation = "";

    private Alert alert;
    private ServiceWrapper serviceWrapper;

    public ChildCommunicationAssessmentCounselingActionHelper(int ageInMonth, Alert alert, ServiceWrapper serviceWrapper) {
        this.ageInMonth = ageInMonth;
        this.serviceWrapper = serviceWrapper;
        this.alert = alert;
    }

    public ChildCommunicationAssessmentCounselingActionHelper(int ageInMonth) {
        this.ageInMonth = ageInMonth;
    }

    @Override
    public void onJsonFormLoaded(String jsonString, Context context, Map<String, List<VisitDetail>> details) {
        this.jsonPayload = jsonString;
    }

    @Override
    public String getPreProcessed() {
        try {
            JSONObject jsonObject = new JSONObject(jsonPayload);
            JSONArray fields = JsonFormUtils.fields(jsonObject);

            JSONObject child_age_in_month = JsonFormUtils.getFieldJSONObject(fields, "child_age_in_months");
            assert child_age_in_month != null;
            child_age_in_month.remove(JsonFormConstants.VALUE);
            child_age_in_month.put(JsonFormConstants.VALUE, ageInMonth);

            return jsonObject.toString();

        } catch (Exception e) {
            Timber.e(e);
        }
        return null;
    }

    @Override
    public void onPayloadReceived(String jsonPayload) {
        try {
            JSONObject jsonObject = new JSONObject(jsonPayload);
            communicatesWithChild = JsonFormUtils.getValue(jsonObject, "communication_with_child");
            communicatesWithChildObservation = JsonFormUtils.getValue(jsonObject, "child_communication_observation");
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    @Override
    public String evaluateSubTitle() {
        return null;
    }

    @Override
    public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
        if(communicatesWithChild.equalsIgnoreCase("no") || communicatesWithChildObservation.contains("chk_force_smile") || communicatesWithChildObservation.contains("chk_child_asleep")){
            return BaseAncHomeVisitAction.Status.PARTIALLY_COMPLETED;
        }else if((communicatesWithChild.equalsIgnoreCase("yes") && communicatesWithChildObservation.contains("chk_sounds_and_gestures")) || (communicatesWithChild.equalsIgnoreCase("yes") && communicatesWithChildObservation.contains("chk_looks_into_eyes"))){
            return BaseAncHomeVisitAction.Status.COMPLETED;
        }else{
            return BaseAncHomeVisitAction.Status.PENDING;
        }
    }
}
