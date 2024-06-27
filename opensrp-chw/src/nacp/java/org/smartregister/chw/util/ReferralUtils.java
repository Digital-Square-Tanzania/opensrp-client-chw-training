package org.smartregister.chw.util;

import org.joda.time.DateTime;
import org.json.JSONObject;
import org.smartregister.chw.anc.util.NCUtils;
import org.smartregister.chw.core.application.CoreChwApplication;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.core.utils.CoreReferralUtils;
import org.smartregister.clientandeventmodel.Event;
import org.smartregister.clientandeventmodel.Obs;
import org.smartregister.domain.Task;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.BaseRepository;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class ReferralUtils extends CoreReferralUtils {

    public static void processReferral(String facilitySelectionForm, String baseEntityId, String referralType, String dangerSignsForm) throws Exception {


        assert dangerSignsForm != null;
        JSONObject dangerSignsJsonObject = new JSONObject(dangerSignsForm);

        String referralProblems = JsonFormUtils.getCheckBoxValue(dangerSignsJsonObject, "danger_signs_present");
        String selectedFacility = JsonQ.fromJson(facilitySelectionForm).get("step1.fields[?(@.key=='chw_referral_hf')].value").toString();

        AllSharedPreferences allSharedPreferences = org.smartregister.util.Utils.getAllSharedPreferences();

        final Event baseEvent = org.smartregister.chw.anc.util.JsonFormUtils.processJsonForm(allSharedPreferences, setEntityId(facilitySelectionForm, baseEntityId), CoreConstants.TABLE_NAME.REFERRAL);

        addReferralDetails(baseEvent, referralType, referralProblems);

        NCUtils.processEvent(baseEvent.getBaseEntityId(), new JSONObject(org.smartregister.chw.anc.util.JsonFormUtils.gson.toJson(baseEvent)));

        createReferralTask(allSharedPreferences, baseEntityId, baseEvent.getFormSubmissionId(), referralProblems, selectedFacility);

    }

    private static void addReferralDetails(Event baseEvent, String referralType, String referralProblems){
        if(baseEvent==null) return;

        long referralDate = System.currentTimeMillis();
        Map<String, Object> obsMap = new HashMap<>();
        obsMap.put("referral_status", "PENDING");
        obsMap.put("chw_referral_service", referralType);
        obsMap.put("referral_type", "community_to_facility_referral");
        obsMap.put("referral_date", referralDate);
        obsMap.put("referral_time",new SimpleDateFormat("HH:mm:ss.SSS", Locale.ENGLISH).format(referralDate));
        obsMap.put("problem", referralProblems);

        for (String key:obsMap.keySet()) {
            List<Object> value = Collections.singletonList(obsMap.get(key));
            baseEvent.addObs(new Obs("concept", "text",key, "", value, value, "",key));
        }

    }

    private static void createReferralTask(AllSharedPreferences allSharedPreferences, String baseEntityId, String formSubmissionId, String referralProblems, String selectedFacility) {

        Task task = new Task();
        task.setIdentifier(UUID.randomUUID().toString());
        task.setPlanIdentifier(CoreConstants.REFERRAL_PLAN_ID);
        task.setGroupIdentifier(selectedFacility);
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

}
