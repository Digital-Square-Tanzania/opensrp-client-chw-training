package org.smartregister.chw.fragment;

import static com.vijay.jsonwizard.utils.FormUtils.fields;
import static com.vijay.jsonwizard.utils.FormUtils.getFieldJSONObject;
import static org.smartregister.util.JsonFormUtils.ENTITY_ID;
import static org.smartregister.util.JsonFormUtils.STEP1;
import static org.smartregister.util.JsonFormUtils.VALUE;
import static org.smartregister.util.JsonFormUtils.generateRandomUUIDString;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.vijay.jsonwizard.utils.FormUtils;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.chw.R;
import org.smartregister.chw.adapter.SbcMobilizationRegisterAdapter;
import org.smartregister.chw.adapter.SbcMonthlySocialMediaReportsRegisterAdapter;
import org.smartregister.chw.application.ChwApplication;
import org.smartregister.chw.core.custom_views.NavigationMenu;
import org.smartregister.chw.dao.ChwSbcDao;
import org.smartregister.chw.model.SbcMobilizationSessionModel;
import org.smartregister.chw.model.SbcMobilizationSessionRegisterFragmentModel;
import org.smartregister.chw.model.SbcMonthlySocialMediaReportModel;
import org.smartregister.chw.presenter.SbcMonthlySocialMediaReportRegisterFragmentPresenter;
import org.smartregister.chw.provider.SbccRegisterProvider;
import org.smartregister.chw.sbc.fragment.BaseSbcRegisterFragment;
import org.smartregister.chw.sbc.util.Constants;
import org.smartregister.configurableviews.model.View;
import org.smartregister.cursoradapter.RecyclerViewPaginatedAdapter;
import org.smartregister.family.util.JsonFormUtils;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.view.activity.BaseRegisterActivity;
import org.smartregister.view.customcontrols.CustomFontTextView;

import java.util.List;
import java.util.Set;

import timber.log.Timber;

public class SbcMonthlySocialMediaReportRegisterFragment extends BaseSbcRegisterFragment {

    private android.view.View view;

    @Override
    public void initializeAdapter(Set<View> visibleColumns) {
        SbccRegisterProvider sbcMobilizationRegisterProvider = new SbccRegisterProvider(getActivity(), paginationViewHandler, registerActionHandler, visibleColumns);
        List<SbcMonthlySocialMediaReportModel> sbcMonthlySocialMediaReportModels = ChwSbcDao.getSbcMonthlySocialMediaReport();
        clientAdapter = new RecyclerViewPaginatedAdapter(null, sbcMobilizationRegisterProvider, null);
        clientAdapter.setTotalcount(0);
        clientAdapter.setCurrentlimit(20);
        if (sbcMonthlySocialMediaReportModels != null && !sbcMonthlySocialMediaReportModels.isEmpty()) {
            clientsView.setAdapter(new SbcMonthlySocialMediaReportsRegisterAdapter(sbcMonthlySocialMediaReportModels, requireActivity()));
        }
    }

    @Override
    public void setupViews(android.view.View view) {
        initializePresenter();
        super.setupViews(view);
        this.view = view;

        Toolbar toolbar = view.findViewById(org.smartregister.R.id.register_toolbar);
        toolbar.setContentInsetsAbsolute(0, 0);
        toolbar.setContentInsetsRelative(0, 0);
        toolbar.setContentInsetStartWithNavigation(0);

        try {
            NavigationMenu.getInstance(getActivity(), null, toolbar);
        } catch (NullPointerException e) {
            Timber.e(e);
        }
        android.view.View navbarContainer = view.findViewById(org.smartregister.chw.core.R.id.register_nav_bar_container);
        navbarContainer.setFocusable(false);

        CustomFontTextView titleView = view.findViewById(org.smartregister.chw.core.R.id.txt_title_label);
        if (titleView != null) {
            titleView.setText(getString(R.string.sbc_monthly_social_media_report));
            titleView.setPadding(0, titleView.getTop(), titleView.getPaddingRight(), titleView.getPaddingBottom());
        }

        android.view.View searchBarLayout = view.findViewById(org.smartregister.chw.core.R.id.search_bar_layout);
        searchBarLayout.setVisibility(android.view.View.GONE);

        android.view.View topLeftLayout = view.findViewById(org.smartregister.chw.core.R.id.top_left_layout);
        topLeftLayout.setVisibility(android.view.View.GONE);

        android.view.View topRightLayout = view.findViewById(org.smartregister.chw.core.R.id.top_right_layout);
        topRightLayout.setVisibility(android.view.View.VISIBLE);

        android.view.View sortFilterBarLayout = view.findViewById(org.smartregister.chw.core.R.id.register_sort_filter_bar_layout);
        sortFilterBarLayout.setVisibility(android.view.View.GONE);

        android.view.View filterSortLayout = view.findViewById(org.smartregister.chw.core.R.id.filter_sort_layout);
        filterSortLayout.setVisibility(android.view.View.GONE);

        android.view.View dueOnlyLayout = view.findViewById(org.smartregister.chw.core.R.id.due_only_layout);
        dueOnlyLayout.setVisibility(android.view.View.GONE);
        dueOnlyLayout.setOnClickListener(registerActionHandler);
        if (getSearchView() != null) {
            getSearchView().setVisibility(android.view.View.GONE);
        }
    }


    @Override
    protected void initializePresenter() {
        if (getActivity() == null) {
            return;
        }
        String viewConfigurationIdentifier = null;
        try {
            viewConfigurationIdentifier = ((BaseRegisterActivity) getActivity()).getViewIdentifiers().get(0);
        } catch (NullPointerException e) {
            Timber.e(e);
        }
        presenter = new SbcMonthlySocialMediaReportRegisterFragmentPresenter(this, new SbcMobilizationSessionRegisterFragmentModel(), viewConfigurationIdentifier);
    }

    @Override
    public void onViewCreated(@NonNull android.view.View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (clientsView.getAdapter() != null) {
            clientsView.getAdapter().notifyDataSetChanged();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        Toolbar toolbar = view.findViewById(org.smartregister.R.id.register_toolbar);
        toolbar.setContentInsetsAbsolute(0, 0);
        toolbar.setContentInsetsRelative(0, 0);
        toolbar.setContentInsetStartWithNavigation(0);
        NavigationMenu.getInstance(getActivity(), null, toolbar);

        try {
            new Handler(Looper.getMainLooper()).postDelayed(this::updateTheList, 2000);
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private void updateTheList() {
        if (clientsView.getAdapter() != null) {
            clientsView.getAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public void countExecute() {
        Cursor c = null;
        try {

            String query = "select count(*) from " + presenter().getMainTable() + " where " + presenter().getMainCondition();

            if (StringUtils.isNotBlank(filters)) {
                query = query + " and ( " + filters + " ) ";
            }


            c = commonRepository().rawCustomQueryForAdapter(query);
            c.moveToFirst();
            clientAdapter.setTotalcount(c.getInt(0));
            Timber.v("total count here %s", clientAdapter.getTotalcount());

            clientAdapter.setCurrentlimit(20);
            clientAdapter.setCurrentoffset(0);

        } catch (Exception e) {
            Timber.e(e);
        } finally {
            if (c != null) {
                c.close();
            }
        }
    }


    @Override
    protected void openProfile(String baseEntityId) {
        //implement when needed
    }


    @Override
    protected void refreshSyncProgressSpinner() {
        if (syncProgressBar != null) {
            syncProgressBar.setVisibility(android.view.View.GONE);
        }
        if (syncButton != null) {
            syncButton.setVisibility(android.view.View.VISIBLE);
            syncButton.setPadding(0, 0, 10, 0);
            syncButton.setImageDrawable(context().getDrawable(R.drawable.ic_add_white_24));
            syncButton.setOnClickListener(view -> {
                JSONObject form;
                try {
                    form = (new FormUtils()).getFormJsonFromRepositoryOrAssets(requireActivity(), Constants.FORMS.SBC_MONTHLY_SOCIAL_MEDIA_REPORT);
                    if (form != null) {
                        String randomId = generateRandomUUIDString();
                        form.put(ENTITY_ID, randomId);
                        requireActivity().startActivityForResult(org.smartregister.chw.core.utils.FormUtils.getStartFormActivity(form, requireActivity().getString(R.string.sbc_monthly_social_media_report), requireActivity()), JsonFormUtils.REQUEST_CODE_GET_JSON);
                    }
                } catch (JSONException e) {
                    Timber.e(e);
                }
            });
        }
    }
}
