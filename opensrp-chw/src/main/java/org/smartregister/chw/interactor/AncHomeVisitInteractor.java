package org.smartregister.chw.interactor;

import android.content.Context;
import android.widget.Toast;

import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.contract.BaseAncHomeVisitContract;
import org.smartregister.chw.anc.domain.MemberObject;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;
import org.smartregister.chw.anc.util.VisitUtils;
import org.smartregister.chw.core.interactor.CoreAncHomeVisitInteractor;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.util.JsonFormUtils;
import org.smartregister.chw.util.ReferralUtils;

import java.util.LinkedHashMap;
import java.util.Map;

import timber.log.Timber;

public class AncHomeVisitInteractor extends CoreAncHomeVisitInteractor {

    Context context;

    @Override
    public void calculateActions(BaseAncHomeVisitContract.View view, MemberObject memberObject, BaseAncHomeVisitContract.InteractorCallBack callBack) {
        // update the local database incase of manual date adjustment

        final Runnable runnable = () -> {

            try {
                VisitUtils.processVisits(memberObject.getBaseEntityId());
            } catch (Exception e) {
                Timber.e(e);
            }

            final LinkedHashMap<String, BaseAncHomeVisitAction> actionList = new LinkedHashMap<>();

            try {
                for (Map.Entry<String, BaseAncHomeVisitAction> entry : getFlavor().calculateActions(view, memberObject, callBack).entrySet()) {
                    actionList.put(entry.getKey(), entry.getValue());
                }
            } catch (BaseAncHomeVisitAction.ValidationException e) {
                Timber.e(e);
            }

            appExecutors.mainThread().execute(() -> callBack.preloadActions(actionList));
        };

        appExecutors.diskIO().execute(runnable);
    }

    public AncHomeVisitInteractor(Context context) {
        this.context = context;
        setFlavor(new AncHomeVisitInteractorFlv());
    }

    @Override
    protected void submitVisit(boolean editMode, String memberID, Map<String, BaseAncHomeVisitAction> map, String parentEventType) throws Exception {
        if (map != null && !editMode) {
            BaseAncHomeVisitAction facilitySelectionAction = map.get(context.getString(R.string.home_visit_facility_referral));
            if (facilitySelectionAction != null) {
                String facilitySelectionForm = facilitySelectionAction.getJsonPayload();
                if (facilitySelectionForm != null) {

                    BaseAncHomeVisitAction dangerSignsActions = map.get(context.getString(R.string.anc_home_visit_danger_signs));

                    try {

                        assert dangerSignsActions != null;
                        String dangerSignsForm = dangerSignsActions.getJsonPayload();
                        assert dangerSignsForm != null;
                        JSONObject dangerSignsJsonObject = new JSONObject(dangerSignsForm);

                        String referralProblems = JsonFormUtils.getCheckBoxValue(dangerSignsJsonObject, "danger_signs_present");

                        // Here pass the  dangerSignsSelectedObject to the processReferral method and then add the problems to the referralProblems
                        ReferralUtils.processReferral(facilitySelectionForm, memberID, CoreConstants.TASKS_FOCUS.ANC_DANGER_SIGNS, referralProblems);
                        Toast.makeText(context, R.string.referral_submitted, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }
        super.submitVisit(editMode, memberID, map, parentEventType);
    }

}
