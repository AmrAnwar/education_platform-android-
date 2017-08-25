package com.mrerror.tm.fragments;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mrerror.tm.MainActivity;
import com.mrerror.tm.R;

public class GeneralNews extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    ProgressBar mProgressBar;
    TextView blankText;

    public GeneralNews() {
        // Required empty public constructor
    }

    public static GeneralNews newInstance(String param1, String param2) {
        GeneralNews fragment = new GeneralNews();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_general_news, container, false);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());
        mProgressBar = (ProgressBar) ((MainActivity) getActivity()).findViewById(R.id.progressbar);

        blankText = (TextView) ((MainActivity) getActivity()).findViewById(R.id.no_list_net);
        mProgressBar.setVisibility(View.GONE);
        blankText.setVisibility(View.GONE);

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.VISIBLE);
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) v.findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) v.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        return v;
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 1:
                    mProgressBar.setVisibility(View.GONE);
                    blankText.setVisibility(View.GONE);
                    return NewsFragment.newInstance("b");

                case 0:
                    mProgressBar.setVisibility(View.GONE);
                    blankText.setVisibility(View.GONE);
                    return NewsFragment.newInstance("a");
                default:
                    mProgressBar.setVisibility(View.GONE);
                    blankText.setVisibility(View.GONE);
                    return NewsFragment.newInstance("a");
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "General";
                case 1:
                    return "Educational";
            }
            return null;
        }
    }
}
