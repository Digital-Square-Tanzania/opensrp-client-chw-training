package org.smartregister.chw.fragment;

import static org.smartregister.chw.util.FnInterfaces.KeyValue;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.vijay.jsonwizard.customviews.CheckBox;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.anc.contract.BaseAncHomeVisitContract;
import org.smartregister.chw.anc.domain.VaccineDisplay;
import org.smartregister.chw.anc.domain.VisitDetail;
import org.smartregister.chw.anc.util.JsonFormUtils;
import org.smartregister.chw.anc.util.NCUtils;
import org.smartregister.chw.util.FnList;
import org.smartregister.chw.util.UtilsFlv;
import org.smartregister.view.customcontrols.CustomFontTextView;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class BaseHomeVisitImmunizationFragmentFlv extends DefaultBaseHomeVisitImmunizationFragment {
    private View root;
    public static BaseHomeVisitImmunizationFragmentFlv getInstance(final BaseAncHomeVisitContract.VisitView view, String baseEntityID, Map<String, List<VisitDetail>> details, List<VaccineDisplay> vaccineDisplays) {
        return getInstance(view, baseEntityID, details, vaccineDisplays, true);
    }

    public static BaseHomeVisitImmunizationFragmentFlv getInstance(final BaseAncHomeVisitContract.VisitView view, String baseEntityID, Map<String, List<VisitDetail>> details, List<VaccineDisplay> vaccineDisplays, boolean defaultChecked) {
        BaseHomeVisitImmunizationFragmentFlv fragment = new BaseHomeVisitImmunizationFragmentFlv();
        fragment.visitView = view;
        fragment.baseEntityID = baseEntityID;
        fragment.details = details;
        fragment.vaccinesDefaultChecked = defaultChecked;
        for (VaccineDisplay vaccineDisplay : vaccineDisplays) {
            fragment.vaccineDisplays.put(vaccineDisplay.getVaccineWrapper().getName(), vaccineDisplay);
        }

        if (details != null && !details.isEmpty()) {
            fragment.jsonObject = NCUtils.getVisitJSONFromVisitDetails(view.getMyContext(), baseEntityID, details, vaccineDisplays);
            JsonFormUtils.populateForm(fragment.jsonObject, details);
        }
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }


    private  void init(View view){
        root=view;
        root.findViewById(R.id.save_btn).setOnClickListener(this::save);
        root.findViewById(R.id.add_date_separately).setOnClickListener(datePickerHelper::toggleMultiMode);
        listenForVaccineSelection();
        createViewOptionForNoVaccines();

        CheckBox noVaccine=root.findViewById(R.id.checkbox_no_vaccination).findViewById(R.id.select);
        noVaccine.setOnClickListener(ignore->{});
        noVaccine.setOnCheckedChangeListener((checkbox, b) -> onNoVaccineSelected(b));
    }

    private void createViewOptionForNoVaccines(){
        ViewGroup parent = root.findViewById(R.id.reasons_no_vaccines);
        FnList.from(root.getResources().getStringArray(R.array.reason_no_vaccine))
                .map(KeyValue::create)
                .forEachItem(reason->{
                    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_vaccine_name_check,parent,false);
                    CustomFontTextView label = view.findViewById(R.id.vaccine);
                    CheckBox checkBox = view.findViewById(R.id.select);
                    checkBox.setChecked(false);
                    checkBox.setTag(reason.key);
                    label.setText(reason.value);
                    parent.addView(view);
                });
    }
    private void onVaccineSelectStatusChange(View ignore){
        int allVaccineCount =((ViewGroup)root.findViewById(R.id.vaccination_name_layout)).getChildCount();
        int selectedVaccineCount = FnList.from(getVaccineValues()).filter(v->v.selected).list().size();

        boolean allSelected = allVaccineCount == selectedVaccineCount && selectedVaccineCount>0;
        boolean fewSelected = !allSelected && selectedVaccineCount>0;
        boolean noneSelected = selectedVaccineCount==0;

        if(allSelected) onAllVaccineSelected();
        if(fewSelected) onFewVaccineSelected();
        if(noneSelected) onNoVaccineSelected(false);
    }
    private void onAllVaccineSelected(){
        ViewGroup reasonsView = root.findViewById(R.id.reasons_no_vaccines);
        FnList.from(reasonsView)
                .forEachItem(v-> ((CheckBox) v.findViewById(R.id.select)).setChecked(false));
        reasonsView.setVisibility(View.GONE);
        root.findViewById(R.id.why_no_vaccine).setVisibility(View.GONE);
        root.findViewById(R.id.congratulate_has_all_vaccine).setVisibility(View.VISIBLE);
        root.findViewById(R.id.multiple_vaccine_date_pickerview).setVisibility(View.VISIBLE);
        root.findViewById(R.id.single_vaccine_add_layout).setVisibility(View.VISIBLE);
        root.findViewById(R.id.vaccination_name_layout).setVisibility(View.VISIBLE);
        datePickerHelper.showDateForSelectedVaccines();
    }

    private void onFewVaccineSelected(){
        root.findViewById(R.id.reasons_no_vaccines).setVisibility(View.VISIBLE);
        root.findViewById(R.id.why_no_vaccine).setVisibility(View.VISIBLE);
        root.findViewById(R.id.congratulate_has_all_vaccine).setVisibility(View.GONE);
        root.findViewById(R.id.multiple_vaccine_date_pickerview).setVisibility(View.VISIBLE);
        root.findViewById(R.id.single_vaccine_add_layout).setVisibility(View.VISIBLE);
        datePickerHelper.showDateForSelectedVaccines();
    }
    private void onNoVaccineSelected(boolean showReasons){
        root.findViewById(R.id.multiple_vaccine_date_pickerview).setVisibility(showReasons?View.GONE:View.VISIBLE);
        root.findViewById(R.id.single_vaccine_add_layout).setVisibility(showReasons?View.GONE:View.VISIBLE);
        root.findViewById(R.id.vaccination_name_layout).setVisibility(showReasons?View.GONE:View.VISIBLE);
        root.findViewById(R.id.why_no_vaccine).setVisibility(showReasons?View.VISIBLE:View.GONE);
        root.findViewById(R.id.congratulate_has_all_vaccine).setVisibility(View.GONE);
        datePickerHelper.clearDates();

        ViewGroup reasonsView = root.findViewById(R.id.reasons_no_vaccines);
        reasonsView.setVisibility(showReasons?View.VISIBLE:View.GONE);
        FnList.from(reasonsView)
                .forEachItem(v-> ((CheckBox) v.findViewById(R.id.select)).setChecked(false));
        FnList.from(root,R.id.vaccination_name_layout)
                .forEachItem(ch-> ((CheckBox)ch).setChecked(false));
    }


    private void listenForVaccineSelection(){
        FnList.from(root,R.id.vaccination_name_layout)
                .map(v->(CheckBox)v.findViewById(R.id.select))
                .forEachItem(this::onVaccineSelectStatusChange);
    }
    private List<VaccineValue> getVaccineValues(){
        ViewGroup datesView = root.getRootView().findViewById(R.id.single_vaccine_add_layout);
        ViewGroup vaccineView = root.getRootView().findViewById(R.id.vaccination_name_layout);
        List<VaccineValue> vaccineV = FnList.from(vaccineView).map(v->new VaccineValue(v,this)).list();

        if(datesView.getVisibility() == View.VISIBLE) {
            FnList.from(datesView)
                    .forEachItem(view -> FnList.from(vaccineV).forEachItem(vc -> vc.setDateFromMultiMode(view)));
        }
        return vaccineV;
    }


    protected FnList<String> getSelectedReasonsNoVaccines() {
        return FnList.from(root,R.id.reasons_no_vaccines)
                .map(v->(CheckBox)v.findViewById(R.id.select))
                .filter(CompoundButton::isChecked)
                .map(ch->ch.getTag().toString());
    }

    private void save(View view) {
        super.onClick(view);

        List<String> selected=getSelectedReasonsNoVaccines().list();
        if(selected.isEmpty())return;

        JSONObject field = FnList.from( new String[]{
                        "key: reasons_no_vaccination",
                        "openmrs_entity_parent: vaccine",
                        "openmrs_entity: concept",
                        "type: select",
                        "openmrs_entity_id: reasons_no_vaccination"
                })
                .map(KeyValue::create)
                .reduce(new JSONObject(),(map,p)->{
                    map.put(p.key,p.value);
                    return map;
                });

        UtilsFlv.ex(()->field.put("value",selected));
        super.visitView.onDialogOptionUpdated(addField(field));
    }


    private String addField(JSONObject value){
        JSONObject json=super.getJsonObject();
        JSONArray fields= UtilsFlv.coalesce(UtilsFlv.jsonGet(json,"step1.fields"),new JSONArray());
        fields.put(value);
        return json.toString();
    }

    private static class VaccineValue {
        String name;
        Date date;
        boolean selected;
        VaccineValue(View view,BaseHomeVisitImmunizationFragmentFlv fg){
            CustomFontTextView tv = view.findViewById(R.id.vaccine);
            DatePicker dp = view.getRootView().findViewById(R.id.earlier_date_picker);
            CheckBox cb = view.findViewById(R.id.select);
            cb.setOnClickListener(fg::onVaccineSelectStatusChange);

            this.name = tv.getText().toString();
            this.selected = cb.isChecked();
            this.date = dp == null ? new Date() : UtilsFlv.getDateFromDatePicker(dp);
        }
        void setDateFromMultiMode(View view) {
            DatePicker dt = view.findViewById(R.id.earlier_date_picker);
            CustomFontTextView tx = view.findViewById(R.id.vaccines_given_when_title_question);
            if(tx.getText().toString().contains(name)){
                this.date = UtilsFlv.getDateFromDatePicker(dt);
            }
        }
    }

    private final DatePickerHelper datePickerHelper=new DatePickerHelper(this);
    @Override
    public void setRelaxedDates(boolean relaxedDates) {
        datePickerHelper.relaxedDates = relaxedDates;}
    @Override
    public void setMinimumDate(Date minimumDate) {
        datePickerHelper.minimumDate = minimumDate;
    }

    private static class DatePickerHelper{
        private DatePickerHelper(BaseHomeVisitImmunizationFragmentFlv b){
            base=b;
        }
        boolean isMultiMode=false;
        BaseHomeVisitImmunizationFragmentFlv base;
        private Date minimumDate=new Date();
        private  boolean relaxedDates=false;
        private void toggleMultiMode(View ignore){
           isMultiMode=!isMultiMode;
           showDateForSelectedVaccines();
        }
        private void showDateForSelectedVaccines() {
            if(!isMultiMode)return;
            View root=base.root;
            ViewGroup parent=root.findViewById(R.id.single_vaccine_add_layout);
            parent.removeAllViews();
            FnList.from(base.getVaccineValues())
                    .filter(v->v.selected)
                    .forEachItem(vaccineView->{
                        View layout = LayoutInflater.from(root.getContext()).inflate(R.layout.custom_single_vaccine_view, parent,false);
                        TextView question = layout.findViewById(R.id.vaccines_given_when_title_question);
                        DatePicker datePicker = layout.findViewById(R.id.earlier_date_picker);
                        String translatedVaccineName = NCUtils.getStringResourceByName(vaccineView.name.toLowerCase().replace(" ", "_"), root.getContext());
                        question.setText(root.getContext().getString(R.string.when_vaccine, translatedVaccineName));

                        VaccineDisplay vaccineDisplay = base.vaccineDisplays.get(vaccineView.name);
                        if (vaccineDisplay != null)
                            initializeDatePicker(datePicker, vaccineDisplay);
                        parent.addView(layout);
                    });
        }

        private void clearDates(){
            ViewGroup parent=base.root.findViewById(R.id.single_vaccine_add_layout);
            parent.removeAllViews();
        }

        private void initializeDatePicker(@NotNull DatePicker datePicker, @NotNull VaccineDisplay vaccineDisplay) {
            Date startDate = vaccineDisplay.getStartDate();
            Date endDate = (vaccineDisplay.getEndDate() != null && vaccineDisplay.getEndDate().before(new Date())) ?
                    vaccineDisplay.getEndDate() : new Date();

            Date minDate = (startDate.after(endDate) ? endDate : startDate);
            datePicker.setMinDate(relaxedDates ? minimumDate.getTime() : minDate.getTime());
            datePicker.setMaxDate((relaxedDates ? new Date() : endDate).getTime());
        }
    }
}


