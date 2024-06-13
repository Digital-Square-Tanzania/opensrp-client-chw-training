package org.smartregister.chw.interactor;

import static org.smartregister.chw.anc.model.BaseAncHomeVisitAction.Status.COMPLETED;
import static org.smartregister.chw.anc.model.BaseAncHomeVisitAction.Status.PENDING;
import static org.smartregister.chw.core.utils.CoreReferralUtils.setEntityId;
import static org.smartregister.chw.util.JsonFormUtils.getCheckBoxValue;
import static org.smartregister.chw.util.JsonFormUtilsFlv.getQuestion;

import android.content.Context;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.anc.actionhelper.HomeVisitActionHelper;
import org.smartregister.chw.anc.domain.VisitDetail;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;
import org.smartregister.chw.anc.util.NCUtils;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.FormUtils;
import org.smartregister.chw.referral.util.LocationUtils;
import org.smartregister.chw.util.JsonFormUtils;
import org.smartregister.chw.util.JsonFormUtilsFlv;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.clientandeventmodel.Obs;
import org.smartregister.domain.Task;
import org.smartregister.family.util.Utils;
import org.smartregister.location.helper.LocationHelper;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.BaseRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import timber.log.Timber;

public class FacilitySelectionActionHelper extends HomeVisitActionHelper {
    private JSONObject  payload;
    private final List<ReferralHelperInfo> referralsInfo=new ArrayList<>();

    FacilitySelectionActionHelper(JSONObject referralProblem, String referralType, String baseEntityId){
        ReferralHelperInfo info=new ReferralHelperInfo(referralType,baseEntityId,referralProblem);
        referralsInfo.add(info);
    }

    void addInfo( ReferralHelperInfo info){
        referralsInfo.add(info);
    }
    FacilitySelectionActionHelper(List<ReferralHelperInfo> info){
        referralsInfo.addAll(info);
    }

    @Override
    public void onJsonFormLoaded(String jsonString, Context context, Map<String, List<VisitDetail>> details) {
        super.onJsonFormLoaded(jsonString, context, details);
        this.context=context;
    }

    @Override
    public String getPreProcessed() {
        try{
            Map<String,String> facilityOptions = LocationUtils.INSTANCE.getFacilitiesKeyAndName();
            String formName="referral_facility_selection";
            JSONObject jsonForm = FormUtils.getFormUtils().getFormJson(formName);
            jsonForm.put("count",referralsInfo.size());
            String step1=jsonForm.getJSONObject("step1").toString();

            for(int i=0,len=referralsInfo.size();i<len;i++){
                ReferralHelperInfo info=referralsInfo.get(i);
                JSONObject step=new JSONObject(step1);
                JsonFormUtilsFlv.overwriteQuestionOptions("chw_referral_hf",facilityOptions, step);

                step.put("title",info.stepName);
                step.put("baseEntityId",info.baseEntityId);
                jsonForm.put("step"+(i+1),step);

                if(i+1<len){
                    step.put("next","step"+(i+2));
                }
            }
            return jsonForm.toString();
        }
        catch (JSONException e){Timber.e(e);}
        return "";
    }

    private BaseAncHomeVisitAction.Status evaluateStatus(JSONObject form){
        String facility=getQuestion("chw_referral_hf",form).optString("value");
        String date=getQuestion("referral_appointment_date",form).optString("value");
        boolean notFilled=facility.isEmpty()||date.isEmpty();
       return notFilled||status==PENDING?PENDING:COMPLETED;
    }

    private BaseAncHomeVisitAction.Status status;
    @Override
    public void onPayloadReceived(String jsonPayload) {
        try {
            String formName="referral_facility_selection";
            JSONObject jsonForm = FormUtils.getFormUtils().getFormJson(formName);

            payload = new JSONObject(jsonPayload);
            payload.put("count",referralsInfo.size());
            int i=1;
            for(ReferralHelperInfo info:referralsInfo){
                JSONObject step=payload.getJSONObject("step"+(i++));
                step.getJSONArray("fields").put(info.problem);
                status=evaluateStatus(step);
                jsonForm.put("step1",step);
                createReferralEvent(
                        Utils.getAllSharedPreferences(),
                        jsonForm.toString(),
                        info);
            }
        }
        catch (Exception e) {Timber.e(e);}
    }

    @Override
    public String evaluateSubTitle() {return "";}

    @Override
    public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
        return status!=null?status:PENDING;
    }

    private void createReferralEvent(AllSharedPreferences allSharedPreferences, String jsonString, ReferralHelperInfo info) throws Exception {
        final Event baseEvent = org.smartregister.chw.anc.util.JsonFormUtils.processJsonForm(allSharedPreferences, setEntityId(jsonString, info.baseEntityId), CoreConstants.TABLE_NAME.REFERRAL);

        // Other obs needed for referral
        addReferralDetails(baseEvent,info.type);

        NCUtils.processEvent(baseEvent.getBaseEntityId(), new JSONObject(org.smartregister.chw.anc.util.JsonFormUtils.gson.toJson(baseEvent)));
        createReferralTask(baseEvent.getBaseEntityId(), baseEvent.getFormSubmissionId(),info);
    }


    private void addReferralDetails(Event baseEvent,String referralType){
        if(baseEvent==null) return;

        long referralDate = System.currentTimeMillis();
        Map<String, Object> obsMap = new HashMap<>();
        obsMap.put("referral_status", "PENDING");
        obsMap.put("chw_referral_service", referralType);
        obsMap.put("referral_type", "community_to_facility_referral");
        obsMap.put("referral_date", referralDate);
        obsMap.put("referral_time",new SimpleDateFormat("HH:mm:ss.SSS", Locale.ENGLISH).format(referralDate));

        for (String key:obsMap.keySet()) {
            List<Object> value = Collections.singletonList(obsMap.get(key));
            baseEvent.addObs(new Obs("concept", "text",key, "", value, value, "",key));
        }
    }

    private void createReferralTask(String baseEntityId, String formSubmissionId,ReferralHelperInfo info) {
        AllSharedPreferences allSharedPreferences= Utils.getAllSharedPreferences();

        String referralProblems = String.join(", ",JsonFormUtilsFlv.column(info.problem.optJSONArray("option"),"text"));

        Task task = new Task();
        task.setIdentifier(UUID.randomUUID().toString());
        task.setPlanIdentifier(CoreConstants.REFERRAL_PLAN_ID);
        LocationHelper locationHelper = LocationHelper.getInstance();
        task.setGroupIdentifier(locationHelper.getOpenMrsLocationId(locationHelper.generateDefaultLocationHierarchy(CoreChwApplication.getInstance().getAllowedLocationLevels()).get(0)));
        task.setStatus(Task.TaskStatus.READY);
        task.setBusinessStatus(CoreConstants.BUSINESS_STATUS.REFERRED);
        task.setPriority(3);
        task.setCode(CoreConstants.JsonAssets.REFERRAL_CODE);
        task.setDescription(referralProblems);
        task.setFocus(CoreConstants.TASKS_FOCUS.ANC_DANGER_SIGNS);
        task.setForEntity(baseEntityId);
        DateTime now = new DateTime();
        task.setExecutionStartDate(now);
        task.setAuthoredOn(now);
        task.setLastModified(now);
        task.setOwner(allSharedPreferences.fetchRegisteredANM());
        task.setSyncStatus(BaseRepository.TYPE_Created);
        task.setReasonReference(formSubmissionId);
        task.setRequester(allSharedPreferences.getANMPreferredName(allSharedPreferences.fetchRegisteredANM()));
        task.setLocation(allSharedPreferences.fetchUserLocalityId(allSharedPreferences.fetchRegisteredANM()));
        CoreChwApplication.getInstance().getTaskRepository().addOrUpdate(task);
    }

    public static JSONObject copyReferralProblem(String referralForm,String dangerSignQnKey) {
        try {return copyReferralProblem(new JSONObject(referralForm),dangerSignQnKey);}
        catch (JSONException e) {
            Timber.e(e);
           return new JSONObject();
        }
    }
    public static JSONObject copyReferralProblem(JSONObject referralForm,String dangerSignQnKey) {
        try {
            JSONObject problem = JsonFormUtilsFlv.getQuestion(dangerSignQnKey, referralForm,new JSONObject());
            problem.put("key", "problem");
            problem.put("openmrs_entity_id", "problem");
            JSONArray options = problem.getJSONArray("options");

            List<String> value = JsonFormUtilsFlv.fromJsonArray(problem.getJSONArray("value"), Object::toString);
            for (int i = 0, len = options.length(); i < len; i++) {
                JSONObject option = options.getJSONObject(i);
                if (value.contains(option.optString("key"))) {
                    option.put("value", true);
                }
            }
            return problem;
        } catch (JSONException e) {
            Timber.e(e);
        }
        return new JSONObject();
    }

    boolean noInfo() {
        return referralsInfo.isEmpty();
    }

    static class ReferralHelperInfo{
        ReferralHelperInfo(String type,String baseEntityId,JSONObject problem){
            this.type=type;
            this.baseEntityId=baseEntityId;
            this.problem=problem;
            this.stepName="";
        }
        ReferralHelperInfo(String type,String baseEntityId,JSONObject problem,String stepName){
            this(type,baseEntityId,problem);
            this.stepName=stepName;
        }
        String stepName;
        String type;
        String baseEntityId;
        JSONObject problem;
    }
}
