package org.smartregister.chw.sync;


import static org.smartregister.chw.anc.util.Constants.EVENT_TYPE.DELETE_EVENT;
import static org.smartregister.chw.hivst.util.Constants.EVENT_TYPE.HIVST_MOBILIZATION;

import android.content.Context;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.CoreLibrary;
import org.smartregister.chw.anc.util.NCUtils;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.dao.EventDao;
import org.smartregister.chw.core.sync.CoreClientProcessor;
import org.smartregister.chw.core.utils.CoreConstants;
import org.smartregister.chw.dao.PmtctDao;
import org.smartregister.chw.fp.util.FamilyPlanningConstants;
import org.smartregister.chw.schedulers.ChwScheduleTaskExecutor;
import org.smartregister.chw.service.ChildAlertService;
import org.smartregister.chw.util.Constants;
import org.smartregister.domain.Event;
import org.smartregister.domain.Obs;
import org.smartregister.domain.db.EventClient;
import org.smartregister.domain.jsonmapping.ClientClassification;
import org.smartregister.domain.jsonmapping.Table;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.sync.ClientProcessorForJava;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import timber.log.Timber;

public class ChwClientProcessor extends CoreClientProcessor {

    private ChwClientProcessor(Context context) {
        super(context);
    }

    public static ClientProcessorForJava getInstance(Context context) {
        if (instance == null) {
            instance = new ChwClientProcessor(context);
        }
        return instance;
    }

    @Override
    public void processEvents(ClientClassification clientClassification, Table vaccineTable, Table serviceTable, EventClient eventClient, Event event, String eventType) throws Exception {
        if (eventClient != null && eventClient.getEvent() != null) {
            String baseEntityID = eventClient.getEvent().getBaseEntityId();

            switch (eventType) {
                case CoreConstants.EventType.REMOVE_FAMILY:
                    ChwApplication.getInstance().getScheduleRepository().deleteSchedulesByFamilyEntityID(baseEntityID);
                case CoreConstants.EventType.REMOVE_MEMBER:
                    ChwApplication.getInstance().getScheduleRepository().deleteSchedulesByEntityID(baseEntityID);
                case CoreConstants.EventType.REMOVE_CHILD:
                    if (!CoreLibrary.getInstance().isPeerToPeerProcessing() && !SyncStatusBroadcastReceiver.getInstance().isSyncing()) {
                        ChwApplication.getInstance().getScheduleRepository().deleteSchedulesByEntityID(baseEntityID);
                    }
                    break;
                default:
                    break;
            }
        }

        super.processEvents(clientClassification, vaccineTable, serviceTable, eventClient, event, eventType);
        if (eventClient != null && eventClient.getEvent() != null) {
            String baseEntityID = eventClient.getEvent().getBaseEntityId();
            switch (eventType) {
                case CoreConstants.EventType.CHILD_HOME_VISIT:
                case CoreConstants.EventType.CHILD_VISIT_NOT_DONE:
                case CoreConstants.EventType.CHILD_REGISTRATION:
                case CoreConstants.EventType.UPDATE_CHILD_REGISTRATION:
                    if (!CoreLibrary.getInstance().isPeerToPeerProcessing() && !SyncStatusBroadcastReceiver.getInstance().isSyncing()) {
                        ChildAlertService.updateAlerts(baseEntityID);
                    }
                    break;
                case Constants.Events.CBHS_FOLLOWUP:
                case Constants.Events.MOTHER_CHAMPION_FOLLOWUP:
                case Constants.Events.ANC_FIRST_FACILITY_VISIT:
                case Constants.Events.ANC_RECURRING_FACILITY_VISIT:
                case Constants.Events.AGYW_STRUCTURAL_SERVICES:
                case Constants.Events.AGYW_BEHAVIORAL_SERVICES:
                case Constants.Events.AGYW_BIO_MEDICAL_SERVICES:
                case Constants.Events.KVP_PREP_FOLLOWUP_VISIT:
                case Constants.Events.MOTHER_CHAMPION_SBCC_SESSIONS:
                case HIVST_MOBILIZATION:
                case org.smartregister.chw.malaria.util.Constants.EVENT_TYPE.ICCM_SERVICES_VISIT:
                case org.smartregister.chw.sbc.util.Constants.EVENT_TYPE.SBC_FOLLOW_UP_VISIT:
                case org.smartregister.chw.sbc.util.Constants.EVENT_TYPE.SBC_HEALTH_EDUCATION_MOBILIZATION:
                case org.smartregister.chw.sbc.util.Constants.EVENT_TYPE.SBC_MONTHLY_SOCIAL_MEDIA_REPORT:
                case FamilyPlanningConstants.EVENT_TYPE.FP_CBD_FOLLOW_UP_VISIT:
                case org.smartregister.chw.cecap.util.Constants.EVENT_TYPE.CECAP_HOME_VISIT:
                case org.smartregister.chw.cecap.util.Constants.EVENT_TYPE.CECAP_HEALTH_EDUCATION_MOBILIZATION:
                case org.smartregister.chw.asrh.util.Constants.EVENT_TYPE.ASRH_FOLLOW_UP_VISIT:
                    if (eventClient.getEvent() == null) {
                        return;
                    }
                    processVisitEvent(eventClient);
                    processEvent(eventClient.getEvent(), eventClient.getClient(), clientClassification);
                    break;

                case org.smartregister.chw.ld.util.Constants.EVENT_TYPE.VOID_EVENT:
                case DELETE_EVENT:
                    processDeleteEvent(eventClient.getEvent());
                default:
                    break;
            }
        }

        if (!CoreLibrary.getInstance().isPeerToPeerProcessing() && !SyncStatusBroadcastReceiver.getInstance().isSyncing()) {
            ChwScheduleTaskExecutor.getInstance().execute(event.getBaseEntityId(), event.getEventType(), event.getEventDate().toDate());
        }
    }

    private void processVisitEvent(EventClient eventClient) {
        try {
            NCUtils.processHomeVisit(eventClient);
        } catch (Exception e) {
            String formID = (eventClient != null && eventClient.getEvent() != null) ? eventClient.getEvent().getFormSubmissionId() : "no form id";
            Timber.e("Form id " + formID + ". " + e.toString());
        }
    }


    @Override
    protected String getHumanReadableConceptResponse(String value, Object object) {
        try {
            if (StringUtils.isBlank(value) || (object != null && !(object instanceof Obs))) {
                return value;
            }
            // Skip human readable values and just get values which would aid in translations
            final String VALUES = "values";
            List values = new ArrayList();

            Object valueObject = getValue(object, VALUES);
            if (valueObject instanceof List) {
                values = (List) valueObject;
            }
            if (object == null || values.isEmpty()) {
                return value;
            }

            return values.size() == 1 ? values.get(0).toString() : values.toString();

        } catch (Exception e) {
            Timber.e(e);
        }
        return value;
    }

    @Override
    public void processDeleteEvent(Event event) {
        try {
            List<String> followupTables = Arrays.asList("ec_cecap_visit");
            if (event.getDetails().containsKey(org.smartregister.chw.anc.util.Constants.JSON_FORM_EXTRA.DELETE_FORM_SUBMISSION_ID)) {
                // delete from vaccine table
                EventDao.deleteVaccineByFormSubmissionId(event.getDetails().get(org.smartregister.chw.anc.util.Constants.JSON_FORM_EXTRA.DELETE_FORM_SUBMISSION_ID));
                // delete from visit table
                EventDao.deleteVisitByFormSubmissionId(event.getDetails().get(org.smartregister.chw.anc.util.Constants.JSON_FORM_EXTRA.DELETE_FORM_SUBMISSION_ID));
                // delete from recurring service table
                EventDao.deleteServiceByFormSubmissionId(event.getDetails().get(org.smartregister.chw.anc.util.Constants.JSON_FORM_EXTRA.DELETE_FORM_SUBMISSION_ID));

                //delete from all  Case Based Management tables that use formSubmissionIds as primaryKeys
                for (String tableName : followupTables) {
                    try {
                        PmtctDao.deleteEntryFromTableByFormSubmissionId(tableName, event.getDetails().get(org.smartregister.chw.anc.util.Constants.JSON_FORM_EXTRA.DELETE_FORM_SUBMISSION_ID));
                    } catch (Exception e) {
                        Timber.e(e);
                    }
                }
            } else {
                super.processDeleteEvent(event);
                //delete from all PMTCT Case Based Management tables that use formSubmissionIds as primaryKeys
                for (String tableName : followupTables) {
                    try {
                        PmtctDao.deleteEntryFromTableByFormSubmissionId(tableName, event.getFormSubmissionId());
                    } catch (Exception e) {
                        Timber.e(e);
                    }
                }
            }

            Timber.d("Ending processDeleteEvent: %s", event.getEventId());
        } catch (Exception e) {
            Timber.e(e);
        }
    }
}
