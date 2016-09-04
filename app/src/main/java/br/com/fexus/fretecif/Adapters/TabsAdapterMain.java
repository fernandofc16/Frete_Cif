package br.com.fexus.fretecif.Adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import br.com.fexus.fretecif.Fragments.AgendaListaFragment;
import br.com.fexus.fretecif.Fragments.CalendarFragment;

public class TabsAdapterMain extends FragmentPagerAdapter {

    private String[] titles = {"CALENDÁRIO", "  AGENDA  "};

    public TabsAdapterMain(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment frag = null;

        switch (position) {
            case 0:
                frag = new CalendarFragment();
                break;
            case 1:
                frag = new AgendaListaFragment();
                break;
        }

        Bundle bundle = new Bundle();
        bundle.putInt("position", position);

        if (frag != null) {
            frag.setArguments(bundle);
        }

        return frag;
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return (titles[position]);
    }

}