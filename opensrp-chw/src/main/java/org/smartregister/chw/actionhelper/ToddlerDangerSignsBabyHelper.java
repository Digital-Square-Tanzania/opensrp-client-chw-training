package org.smartregister.chw.actionhelper;

import static org.smartregister.chw.util.JsonFormUtils.getCheckBoxValue;

import android.content.Context;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.actionhelper.HomeVisitActionHelper;
import org.smartregister.chw.anc.model.BaseAncHomeVisitAction;
import org.smartregister.chw.util.FnInterfaces;
import org.smartregister.domain.Alert;

import java.text.MessageFormat;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import timber.log.Timber;

public class ToddlerDangerSignsBabyHelper extends HomeVisitActionHelper {
    private String dangerSignPresentChild;
    private final FnInterfaces.Action<Boolean> nonDangerSignsEvaluator;

    private final Context context;

    private final Alert alert;


    public ToddlerDangerSignsBabyHelper(Context context, Alert alert, FnInterfaces.Action<Boolean> evaluator) {
        this.context = context;
        this.alert = alert;
        nonDangerSignsEvaluator=evaluator;
    }

    @Override
    public BaseAncHomeVisitAction.ScheduleStatus getPreProcessedStatus() {
        return isOverDue() ? BaseAncHomeVisitAction.ScheduleStatus.OVERDUE : BaseAncHomeVisitAction.ScheduleStatus.DUE;
    }

    private boolean isOverDue() {
        return new LocalDate().isAfter(new LocalDate(alert.startDate()).plusDays(14));
    }

    @Override
    public void onPayloadReceived(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            dangerSignPresentChild = getCheckBoxValue(jsonObject, "toddler_danger_signs_present");
        } catch (JSONException e) {
            Timber.e(e);
        }
    }

    @Override
    public String evaluateSubTitle() {
        return MessageFormat.format("{0}: {1}", context.getString(R.string.child_danger_signs_baby_task), dangerSignPresentChild);
    }

    @Override
    public String postProcess(String jsonPayload) {
        boolean noDangerSigns=dangerSignPresentChild.matches("(?i)none|hakuna");
            try {nonDangerSignsEvaluator.apply(noDangerSigns);}
            catch (Exception e) {Timber.e(e);}
        return super.postProcess(jsonPayload);
    }

    @Override
    public BaseAncHomeVisitAction.Status evaluateStatusOnPayload() {
        if (StringUtils.isBlank(dangerSignPresentChild)) {
            return BaseAncHomeVisitAction.Status.PENDING;
        }

        if (StringUtils.isNotBlank(dangerSignPresentChild)) {
            return BaseAncHomeVisitAction.Status.COMPLETED;
        } else {
            return BaseAncHomeVisitAction.Status.PARTIALLY_COMPLETED;
        }
    }

}
