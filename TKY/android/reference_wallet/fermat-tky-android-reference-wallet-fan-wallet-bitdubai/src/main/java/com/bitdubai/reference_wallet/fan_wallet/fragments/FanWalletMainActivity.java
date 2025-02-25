package com.bitdubai.reference_wallet.fan_wallet.fragments;


import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bitdubai.fermat_android_api.layer.definition.wallet.AbstractFermatFragment;
import com.bitdubai.fermat_api.layer.all_definition.navigation_structure.enums.Wallets;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.enums.UnexpectedWalletExceptionSeverity;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.interfaces.ErrorManager;
import com.bitdubai.fermat_tky_api.layer.wallet_module.FanWalletPreferenceSettings;
import com.bitdubai.fermat_tky_api.layer.wallet_module.interfaces.FanWalletModuleManager;
import com.bitdubai.reference_wallet.fan_wallet.R;
import com.bitdubai.reference_wallet.fan_wallet.session.FanWalletSession;
import com.bitdubai.reference_wallet.fan_wallet.util.LockableViewPager;


/**
 * Created by Miguel Payarez on 16/03/16.
 */
public class FanWalletMainActivity extends AbstractFermatFragment  {

    //FermatManager
    private FanWalletSession fanwalletSession;
    private FanWalletModuleManager fanwalletmoduleManager;
    private FanWalletPreferenceSettings  fanWalletSettings;
    private ErrorManager errorManager;


    View view;
    private FragmentTabHost mTabHost;
    Toolbar toolbar;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        try {
            fanwalletSession = ((FanWalletSession) appSession);
            fanwalletmoduleManager = fanwalletSession.getModuleManager();
            errorManager = appSession.getErrorManager();
            System.out.println("HERE START FAN WALLET");

            try {
                    fanWalletSettings =  fanwalletmoduleManager.getSettingsManager().loadAndGetSettings(appSession.getAppPublicKey());
            } catch (Exception e) {
                fanWalletSettings = null;
            }

            if (fanWalletSettings == null) {
                fanWalletSettings = new FanWalletPreferenceSettings();
                fanWalletSettings.setIsPresentationHelpEnabled(true);
                try {
                    fanwalletmoduleManager.getSettingsManager().persistSettings(appSession.getAppPublicKey(), fanWalletSettings);
                } catch (Exception e) {
                    errorManager.reportUnexpectedWalletException(Wallets.TKY_FAN_WALLET, UnexpectedWalletExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_FRAGMENT, e);
                }
            }


        } catch (Exception e) {
            if (errorManager != null)
                errorManager.reportUnexpectedWalletException(Wallets.TKY_FAN_WALLET, UnexpectedWalletExceptionSeverity.DISABLES_SOME_FUNCTIONALITY_WITHIN_THIS_FRAGMENT, e);
        }



    }

    public static FanWalletMainActivity newInstance(){return new FanWalletMainActivity();}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        view= inflater.inflate(R.layout.tky_fan_wallet_activity,container,false);

        toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        
        getActivity().getWindow().setBackgroundDrawableResource(R.drawable.fanwallet_background_viewpager);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);


        // Setup the viewPager
        LockableViewPager viewPager = (LockableViewPager) view.findViewById(R.id.view_pager);
        MyPagerAdapter pagerAdapter = new MyPagerAdapter(getFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setSwipeable(false);

        // Setup the Tabs
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        // By using this method the tabs will be populated according to viewPager's count and
        // with the name from the pagerAdapter getPageTitle()
        tabLayout.setTabsFromPagerAdapter(pagerAdapter);
        // This method ensures that tab selection events update the ViewPager and page changes update the selected tab.
        tabLayout.setupWithViewPager(viewPager);

        return view;

    }

    private class MyPagerAdapter extends FragmentStatePagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            switch(pos) {

                case 0: return SongFragment.newInstance(fanwalletSession,
                        fanwalletmoduleManager,
                        errorManager);
                case 1: return FollowingFragment.newInstance();
                default: return SongFragment.newInstance(fanwalletSession,
                        fanwalletmoduleManager,
                        errorManager);
            }

        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if(position==0){

                return "Songs";

            }else{

                return "Following";
            }


        }
    }






}
