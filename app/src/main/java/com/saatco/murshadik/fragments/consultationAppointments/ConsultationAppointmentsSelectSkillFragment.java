package com.saatco.murshadik.fragments.consultationAppointments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.saatco.murshadik.ConsultationAppointmentsActivity;
import com.saatco.murshadik.R;
import com.saatco.murshadik.adapters.ConsultaionAppointments.SkillAdapter;
import com.saatco.murshadik.api.ApiMethodsHelper;
import com.saatco.murshadik.databinding.FragmentConsultationAppointmentsSelectSkillBinding;
import com.saatco.murshadik.enums.ManagementType;
import com.saatco.murshadik.model.Item;
import com.saatco.murshadik.utils.DataUtilHelper;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ConsultationAppointmentsSelectSkillFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConsultationAppointmentsSelectSkillFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_SKILL = "skill";
    private static final String ARG_IS_SUB_SKILL = "is_sub_skill";
    private static final String ARG_MANAGEMENT_TYPE = "management_type";

    private Item selectedSkill;
    private boolean shuoldUseSubskillsTreeMethod;
    private SkillAdapter adapter;
    private ArrayList<Item> skills;
    private int managementTypeValue;
    FragmentConsultationAppointmentsSelectSkillBinding binding;

    public ConsultationAppointmentsSelectSkillFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param skill Parameter 1.
     * @return A new instance of fragment ConsultationAppointmentsSelectSkillFragment.
     */
    public static ConsultationAppointmentsSelectSkillFragment newInstance(Item skill, boolean isSubSkill, ManagementType managementType) {
        ConsultationAppointmentsSelectSkillFragment fragment = new ConsultationAppointmentsSelectSkillFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_SKILL, skill);
        args.putBoolean(ARG_IS_SUB_SKILL, isSubSkill);
        args.putInt(ARG_MANAGEMENT_TYPE, managementType.getValue());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            selectedSkill = (Item) getArguments().getSerializable(ARG_SKILL);
            shuoldUseSubskillsTreeMethod = getArguments().getBoolean(ARG_IS_SUB_SKILL);
            managementTypeValue = getArguments().getInt(ARG_MANAGEMENT_TYPE);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentConsultationAppointmentsSelectSkillBinding.inflate(inflater, container, false);

        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        ConsultationAppointmentsActivity activity = (ConsultationAppointmentsActivity) getActivity();
        if (activity != null) {
            binding.appBar.btnBack.setOnClickListener(v -> activity.back());
            binding.appBar.toolbarTitle.setText(activity.getString(R.string.select_skill));
            binding.appBar.btnCancel.setOnClickListener(v -> activity.close());
        }

        skills = DataUtilHelper.getSkillsFromLocalStorage(getContext());
        if (skills == null) {
            ApiMethodsHelper.getSkills(msg -> {
                skills = (ArrayList<Item>) msg;
                initSkillsRecyclerView();
            });
        } else {
            initSkillsRecyclerView();
        }

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s.isEmpty())
                    adapter.setItems(skills);
                else
                    adapter.setItems(getSkillsByName(s));
                return false;
            }
        });
    }

    ArrayList<Item> getSkillsByName(String name) {
        return this.skills.stream().filter(skill -> skill.getNameAr().contains(name)).collect(Collectors.toCollection(ArrayList::new));
    }

    ArrayList<Item> getAllSubSkillsExceptManagements(ArrayList<Item> skills) {
        ArrayList<Item> skillNames = new ArrayList<>();
        for (Item skill : skills) {
            if (skill.getId() == ManagementType.BRANCHES.getValue() || skill.getId() == ManagementType.MINISTRY.getValue())
                continue;
            if (skill.getChildrens().isEmpty())
                skillNames.add(skill);
            else {
                skillNames.addAll(getAllSubSkillsExceptManagements(skill.getChildrens()));
            }
        }
        return skillNames;
    }

    ArrayList<Item> getManagementsSkills(ArrayList<Item> skills) {
        ArrayList<Item> skillNames = new ArrayList<>();
        for (Item skill : skills) {
            if (skill.getId() == managementTypeValue) {
                skillNames.addAll(skill.getChildrens());
                break;
            }
        }
        return skillNames;
    }

    ArrayList<String> getSkills(ArrayList<Item> skills) {
        ArrayList<String> skillNames = new ArrayList<>();
        for (Item skill : skills) {
            skillNames.add(skill.getName());
        }
        return skillNames;
    }

    void initSkillsRecyclerView() {
        if (!shuoldUseSubskillsTreeMethod) {
            skills = getAllSubSkillsExceptManagements(skills);
        } else {
            skills = getManagementsSkills(skills);
        }
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new SkillAdapter(skills, getContext(), selectedSkill, (skill, position) -> {
            if (skill.hasChildren()){
                adapter.setItems(skill.getChildrens());
                return;
            }
            selectedSkill = skill;
            this.next();
        });
        binding.recyclerView.setAdapter(adapter);
    }

    void next() {
        if (shuoldUseSubskillsTreeMethod && selectedSkill.hasChildren()){
            adapter.setItems(selectedSkill.getChildrens());
            return;
        }
        ConsultationAppointmentsActivity activity = (ConsultationAppointmentsActivity) getActivity();
        if (activity != null) {
            activity.setSkill(selectedSkill);
            activity.showFragment(ConsultationAppointments.ConsultationAppointmentsReasonFragment);
        }
    }
}