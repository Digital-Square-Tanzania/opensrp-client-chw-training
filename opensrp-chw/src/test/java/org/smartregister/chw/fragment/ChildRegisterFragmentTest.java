package org.smartregister.chw.fragment;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.chw.BaseUnitTest;
import org.smartregister.chw.activity.ChildRegisterActivity;
import org.smartregister.chw.core.presenter.CoreChildRegisterFragmentPresenter;
import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.domain.FetchStatus;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ChildRegisterFragmentTest extends BaseUnitTest {
    @Mock
    private CommonRepository commonRepository;
    @Mock
    private Context context;
    @Mock
    private ProgressBar syncProgressBar;
    @Mock
    private ImageView syncButton;
    @Mock
    private CoreChildRegisterFragmentPresenter presenter;
    private ChildRegisterFragment fragment;
    @Mock
    private View view;

    private FragmentActivity activity;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        fragment = Mockito.mock(ChildRegisterFragment.class, Mockito.CALLS_REAL_METHODS);
        ReflectionHelpers.setField(fragment, "presenter", presenter);
        ReflectionHelpers.setField(fragment, "view", view);
        ReflectionHelpers.setField(fragment, "dueOnlyLayout", view);
        ReflectionHelpers.setField(fragment, "dueFilterActive", true);

        CoreLibrary.init(context);
        when(context.commonrepository(anyString())).thenReturn(commonRepository);
        activity = Robolectric.buildActivity(AppCompatActivity.class).create().resume().get();
        Context.bindtypes = new ArrayList<>();
        SyncStatusBroadcastReceiver.init(activity);
    }

    @Test
    public void testInitializePresenter() {
        fragment.initializePresenter();
        assertNotNull(presenter);
    }


    @Test
    public void testSetUniqueID() {
        when(fragment.getSearchView()).thenReturn(new EditText(activity));
        fragment.setUniqueID("unique");
        assertEquals("unique", fragment.getSearchView().getText().toString());
    }

    @Test
    public void onSyncCompleteTogglesSyncVisibility() {
        FetchStatus fetchStatus = Mockito.anyObject();
        ReflectionHelpers.setField(fragment, "syncButton", syncButton);
        ReflectionHelpers.setField(fragment, "syncProgressBar", syncProgressBar);
        fragment.onSyncComplete(fetchStatus);
        verify(syncProgressBar, Mockito.times(2)).setVisibility(View.GONE);
        verify(syncButton, Mockito.times(2)).setVisibility(View.GONE);
    }
}



