package com.saatco.murshadik.fragments.consultationAppointments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.saatco.murshadik.ConsultationAppointmentsActivity;
import com.saatco.murshadik.adapters.ConsultaionAppointments.CityAdapter;
import com.saatco.murshadik.api.ApiMethodsHelper;
import com.saatco.murshadik.databinding.FragmentConsultationAppointmentsSelectCityBinding;
import com.saatco.murshadik.model.City;
import com.saatco.murshadik.model.Item;
import com.saatco.murshadik.utils.DataUtilHelper;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ConsultationAppointmentsSelectCityFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConsultationAppointmentsSelectCityFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_CITY = "city";


    private String selectedCity;
    private ArrayList<String> cities;
    CityAdapter adapter;

    FragmentConsultationAppointmentsSelectCityBinding binding;

    public ConsultationAppointmentsSelectCityFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param city Parameter 1.
     * @return A new instance of fragment ConsultationAppointmentsSelectCityFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ConsultationAppointmentsSelectCityFragment newInstance(String city) {
        ConsultationAppointmentsSelectCityFragment fragment = new ConsultationAppointmentsSelectCityFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CITY, city);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selectedCity = getArguments().getString(ARG_CITY);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentConsultationAppointmentsSelectCityBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ArrayList<Item> regions = DataUtilHelper.getRegions();
        if (regions == null) {
            getRemoteRegions();
        } else {
            cities = getCities(regions);
            initCitiesRecyclerView();
        }

        ConsultationAppointmentsActivity activity = (ConsultationAppointmentsActivity) getActivity();
        if (activity != null) {
            binding.appBar.btnBack.setOnClickListener(v -> activity.back());
            binding.appBar.toolbarTitle.setText(getString(com.saatco.murshadik.R.string.select_city));
        }

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s.isEmpty())
                    adapter.setItems(cities);
                else
                    adapter.setItems(getCitiesByName(s));
                return false;
            }
        });



    }

    void initCitiesRecyclerView() {
        adapter = new CityAdapter(cities, getContext(), selectedCity, (city, position) -> {
            selectedCity = city;
            setCityAndGoNext();
        });
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
    }

    ArrayList<String> getCities(ArrayList<Item> regions) {
        ArrayList<String> cities = new ArrayList<>();
        for (Item region : regions) {
            cities.addAll(region.getCities().stream().map(City::getNameAr).collect(Collectors.toList()));
        }
        return cities;
    }

    ArrayList<String> getCitiesByName(String name) {
        return this.cities.stream().filter(city -> city.contains(name)).collect(Collectors.toCollection(ArrayList::new));
    }

    void setCityAndGoNext() {
        ConsultationAppointmentsActivity activity = (ConsultationAppointmentsActivity) getActivity();
        if (activity != null) {
            activity.setCity(selectedCity);
            activity.showFragment(ConsultationAppointments.ConsultationAppointmentsSelectSkillFragment);
        }
    }

    void getRemoteRegions() {
        ApiMethodsHelper.getRegions(regionList -> {
            if (regionList != null) {

                cities = getCities(regionList);
                initCitiesRecyclerView();
            }
        });
    }


}