package org.smartregister.chw.activity;

import static org.smartregister.util.JsonFormUtils.generateRandomUUIDString;

import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.Context;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;
import org.smartregister.chw.anc.presenter.BaseAncHomeVisitPresenter;
import org.smartregister.chw.anc.util.AppExecutors;
import org.smartregister.chw.core.activity.CoreChildHomeVisitActivity;
import org.smartregister.chw.core.interactor.CoreChildHomeVisitInteractor;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.interactor.ChildHomeVisitInteractorFlv;
import org.smartregister.chw.util.Constants;
import org.smartregister.chw.util.JsonFormUtils;
import org.smartregister.chw.util.ReferralUtils;

import java.util.LinkedHashMap;
import java.util.Map;

import timber.log.Timber;

public class ChildHomeVisitActivity extends CoreChildHomeVisitActivity {
    @Override
    protected void registerPresenter() {
        presenter = new BaseAncHomeVisitPresenter(memberObject, this, new CoreChildHomeVisitInteractor(new ChildHomeVisitInteractorFlv()));
    }

    @Override
    public void submittedAndClose() {
        super.submittedAndClose();
        ChildProfileActivity.startMe(this, memberObject, ChildProfileActivity.class);
    }

    @Override
    public void submitVisit() {
        super.submitVisit();

        Map<String, BaseAncHomeVisitAction> actions = this.getAncHomeVisitActions();
        if (actions !=  null) {

            BaseAncHomeVisitAction facilitySelectionAction = actions.get(this.getString(R.string.home_visit_facility_referral));
            if (facilitySelectionAction != null){
                String facilitySelectionForm = facilitySelectionAction.getJsonPayload();
                BaseAncHomeVisitAction dangerSignsActions = actions.get(this.getString(R.string.child_danger_signs_baby));
                try {

                    assert dangerSignsActions != null;
                    String dangerSignsForm = dangerSignsActions.getJsonPayload();
                    assert dangerSignsForm != null;
                    JSONObject dangerSignsJsonObject = new JSONObject(dangerSignsForm);

                    String referralProblems = JsonFormUtils.getCheckBoxValue(dangerSignsJsonObject, "toddler_danger_signs_present");
                    ReferralUtils.processReferral(facilitySelectionForm, this.memberObject.getBaseEntityId(), CoreConstants.TASKS_FOCUS.SICK_CHILD, referralProblems);
                } catch (Exception e) {
                    Timber.e(e);
                }
            }

            String childMinorAilmentString = this.getResources().getString(R.string.child_minor_ailments);
            BaseAncHomeVisitAction minorAilmentAction;

            //Check for Child Minor Ailment action
            for (Map.Entry<String, BaseAncHomeVisitAction> entry : actions.entrySet()){
                String key = entry.getKey();
                BaseAncHomeVisitAction value = entry.getValue();
                if (key.contains(childMinorAilmentString)){
                    minorAilmentAction = value;
                    String childMinorAilmentForm = minorAilmentAction.getJsonPayload();
                    if (childMinorAilmentForm != null){
                        try {
                            JSONObject minorAilmentObject = new JSONObject(childMinorAilmentForm);
                            String childAilments = JsonFormUtils.getCheckBoxValue(minorAilmentObject, "child_minor_ailment").toLowerCase();
                            ReferralUtils.createLinkageTask(Context.getInstance().allSharedPreferences(),
                                    memberObject.getBaseEntityId(), generateRandomUUIDString(), childAilments, Constants.AddoLinkage.CHILD_TASK_FOCUS);
                            Toast.makeText(getContext(), getContext().getString(R.string.linked_to_addo_message), Toast.LENGTH_LONG).show();
                        }catch (JSONException jsonException){
                            Timber.e(jsonException);
                        }
                    }
                }
            }
        }

    }

    @Override
    public void initializeActions(LinkedHashMap<String, BaseAncHomeVisitAction> map) {
        actionList.clear();
        actionList.putAll(map);

        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
        displayProgressBar(false);
    }
}
