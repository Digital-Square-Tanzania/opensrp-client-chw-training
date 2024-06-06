package org.smartregister.chw.interactor;

import static org.smartregister.chw.core.utils.CoreReferralUtils.setEntityId;

import android.content.Context;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.anc.actionhelper.HomeVisitActionHelper;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.domain.VisitDetail;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;
import org.smartregister.chw.anc.util.NCUtils;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.referral.util.Constants;
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
import java.util.List;
import java.util.Map;
import java.util.UUID;

import timber.log.Timber;

public class FacilitySelectionActionHelper extends HomeVisitActionHelper {
    private Context context;
    private String baseEntityId;
    private final String  referralTable;
    private JSONObject  payload;
    private String referralPayload;

    private String referralProblems;
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

            // Implement a logic to check if the process has been completed
            // If it has been completed, create a referral task
            // Maybe we can put this to the save module?
            payload.getJSONObject("step1").getJSONArray("fields").put(referralProblem);
            createReferralEvent(
                    Utils.getAllSharedPreferences(),
                    payload.toString(),
                    CoreConstants.TABLE_NAME.REFERRAL, baseEntityId
            );
        }
        catch (JSONException e) {Timber.e(e);} catch (Exception e) {
            throw new RuntimeException(e);
        }
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

    private void createReferralEvent(AllSharedPreferences allSharedPreferences, String jsonString, String referralTable, String entityId) throws Exception {
        final Event baseEvent = org.smartregister.chw.anc.util.JsonFormUtils.processJsonForm(allSharedPreferences, setEntityId(jsonString, entityId), referralTable);

        // Other obs needed for referral
        addReferralDetails(baseEvent);

        NCUtils.processEvent(baseEvent.getBaseEntityId(), new JSONObject(org.smartregister.chw.anc.util.JsonFormUtils.gson.toJson(baseEvent)));
        createReferralTask(baseEvent.getBaseEntityId(), allSharedPreferences, referralProblems, baseEvent.getFormSubmissionId());
    }

    private void addReferralDetails(Event baseEvent) {
        if (baseEvent != null) {
            baseEvent.addObs(new Obs("concept", "text", "referral_status", "",
                    Collections.singletonList("PENDING"), Collections.singletonList("PENDING"), "", "referral_status"));
            baseEvent.addObs(new Obs("concept", "text", "chw_referral_service", "",
                    Collections.singletonList(Constants.ReferralServiceType.ANC_DANGER_SIGNS),
                    Collections.singletonList(Constants.ReferralServiceType.ANC_DANGER_SIGNS), "", "chw_referral_service"));


            baseEvent.addObs(new Obs("concept", "text", "referral_type", "",
                    Collections.singletonList("community_to_facility_referral"),
                    Collections.singletonList("community_to_facility_referral"), "", "referral_type"));

            long referralDate = System.currentTimeMillis();
            baseEvent.addObs(new Obs("concept", "text", "referral_date", "",
                    Collections.singletonList(referralDate),
                    Collections.singletonList(referralDate), "", "referral_date"));

            String referralTime = new SimpleDateFormat("HH:mm:ss.SSS").format(referralDate);
            baseEvent.addObs(new Obs("concept", "text", "referral_time", "",
                    Collections.singletonList(referralTime),
                    Collections.singletonList(referralTime), "", "referral_time"));
        }
    }

    private void createReferralTask(String baseEntityId, AllSharedPreferences allSharedPreferences, String referralProblems, String formSubmissionId) {
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

    private JSONObject copyReferralProblem(String referralPayload) throws JSONException {
        JSONObject problem=JsonFormUtilsFlv.getQuestion("danger_signs_present",new JSONObject(referralPayload));
        problem.put("key","problem");
        problem.put("openmrs_entity_id", "problem");
        JSONArray options=problem.getJSONArray("options");

        List<String> value = JsonFormUtilsFlv.fromJsonArray(problem.getJSONArray("value"), Object::toString);
        List<String> referralProblemsList = new ArrayList<>();
        for(int i=0,len=options.length();i<len;i++) {
            JSONObject option=options.getJSONObject(i);
            if(value.contains(option.optString("key"))){
                option.put("value",true);
                referralProblemsList.add(option.optString("text"));
            }
        }
        if (!referralProblemsList.isEmpty())
            referralProblems = String.join(", ", referralProblemsList);
        return problem;
    }

}
