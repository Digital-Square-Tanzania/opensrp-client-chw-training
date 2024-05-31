package org.smartregister.chw.interactor;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.anc.actionhelper.HomeVisitActionHelper;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.domain.VisitDetail;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.CoreReferralUtils;
import org.smartregister.chw.util.JsonFormUtilsFlv;
import org.smartregister.family.util.Utils;

import java.util.List;
import java.util.Map;

import timber.log.Timber;

public class FacilitySelectionActionHelper extends HomeVisitActionHelper {
    private Context context;
    private String baseEntityId;
    private final String  referralTable;
    private JSONObject  payload;
    private String referralPayload;
    private Map<String, List<VisitDetail>> details;
    FacilitySelectionActionHelper(String referralForm, Map<String, List<VisitDetail>> details, String referralTableName, MemberObject memberObject){
        referralTable=referralTableName;
        this.referralPayload=referralForm;
        this.baseEntityId=memberObject.getBaseEntityId();
        this.details=details;
    }

    @Override
    public void onJsonFormLoaded(String jsonString, Context context, Map<String, List<VisitDetail>> details) {
        super.onJsonFormLoaded(jsonString, context, details);
        this.context=context;
    }

    @Override
    public void onPayloadReceived(String jsonPayload) {
      //implement
        try {
            payload = new JSONObject(jsonPayload);
            JSONObject referralProblem=copyReferralProblem(referralPayload);
            payload.getJSONObject("step1").getJSONArray("fields").put(referralProblem);
            createReferralTask();
        }
        catch (JSONException e) {Timber.e(e);}
//        return super.postProcess(payload.toString());
    }

    @Override
    public String evaluateSubTitle() {
        return "";
    }


    @Override
    public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
        return BaseAncHomeVisitAction.Status.COMPLETED;
    }


    private void createReferralTask(){
        try {
            CoreReferralUtils.createReferralEvent(
                    Utils.getAllSharedPreferences(),
                    payload.toString(),
                    CoreConstants.TABLE_NAME.ANC_REFERRAL, baseEntityId
            );
            Utils.showToast(context, referralTable);
        } catch (Exception e) {
            Timber.e(e, "AncHomeVisitInteractorFlv --> createReferralTask");
        }
    }

    private JSONObject copyReferralProblem(String referralPayload) throws JSONException {
        JSONObject problem=JsonFormUtilsFlv.getQuestion("danger_signs_present",new JSONObject(referralPayload));
        problem.put("key","problem");
        JSONArray options=problem.getJSONArray("options");

        List<String> value = JsonFormUtilsFlv.fromJsonArray(problem.getJSONArray("value"), Object::toString);
        for(int i=0,len=options.length();i<len;i++) {
            JSONObject option=options.getJSONObject(i);
            if(value.contains(option.optString("key"))){
                option.put("value",true);
            }
        }
        return problem;
    }

}
