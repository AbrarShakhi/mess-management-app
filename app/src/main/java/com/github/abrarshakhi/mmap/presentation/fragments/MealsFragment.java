package com.github.abrarshakhi.mmap.presentation.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.abrarshakhi.mmap.core.constants.MessMemberRole;
import com.github.abrarshakhi.mmap.data.datasourse.HomeDataSource;
import com.github.abrarshakhi.mmap.data.dto.MealTrackingDto;
import com.github.abrarshakhi.mmap.databinding.FragmentMealsBinding;
import com.github.abrarshakhi.mmap.domain.model.MealTracking;
import com.github.abrarshakhi.mmap.domain.model.MessMember;
import com.github.abrarshakhi.mmap.domain.model.MonthYear;
import com.github.abrarshakhi.mmap.presentation.activities.AddMealsActivity;
import com.github.abrarshakhi.mmap.presentation.adapters.DailyMealAdapter;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MealsFragment extends Fragment {
    public FragmentMealsBinding b;
    private HomeDataSource ds;
    private MonthYear monthYear;
    private DailyMealAdapter adapter;

    private ListenerRegistration mealListener;
    private ListenerRegistration memberListener;
    private List<MessMember> cachedMembers = new ArrayList<>();

    public MealsFragment() {
    }

    @NonNull
    public static MealsFragment newInstance() {
        return new MealsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ds = new HomeDataSource(requireContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        b = FragmentMealsBinding.inflate(inflater, container, false);
        return b.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ds = new HomeDataSource(requireContext());
        adapter = new DailyMealAdapter(requireContext());
        b.lvCalender.setAdapter(adapter);
        fetchMonthYearThenLoad();
        b.btnAddMeal.setVisibility(View.GONE);
    }

    private void loadMessMembers(String messId) {
        if (memberListener != null) {
            memberListener.remove();
        }

        memberListener = ds.listenMessMembersRealtime(
            messId,
            members -> {
                cachedMembers = members;
                if (monthYear != null) {
                    loadMealTrackingRealTime(messId);
                }
            },
            e -> Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show()
        );
    }


    private void fetchMonthYearThenLoad() {
        String messId = ds.getCurrentMessId();
        ds.getCurrentMonthYearFromMess(messId, my -> {
                ds.getMess(messId, messDto -> {
                    if (messDto != null && messDto.members != null) {
                        for (var member : messDto.members) {
                            if (member.userId.equals(ds.getLoggedInUser().getUid())) {
                                if (member.role.equals(MessMemberRole.ADMIN) || member.role.equals(MessMemberRole.MANAGER)) {
                                    b.btnAddMeal.setVisibility(View.VISIBLE);
                                    b.btnAddMeal.setOnClickListener(v -> {
                                        startActivity(new Intent(requireActivity(), AddMealsActivity.class)
                                            .putExtra("M", monthYear.getMonth())
                                            .putExtra("Y", monthYear.getYear())
                                        );
                                    });
                                }
                                break;
                            }
                        }
                    }
                }, e -> {
                });
                loadMessMembers(ds.getCurrentMessId());
                b.ivArrowLeft.setOnClickListener(v -> {
                    monthYear = monthYear.previous();
                    loadMealTrackingRealTime(messId);
                });
                b.ivArrowRight.setOnClickListener(v -> {
                    monthYear = monthYear.next();
                    loadMealTrackingRealTime(messId);
                });
                monthYear = my;
                b.tvMonthYear.setText(monthYear.toString());
                monthYear = my;
                b.tvMonthYear.setText(monthYear.toString());
                loadMealTrackingRealTime(messId);
            },
            e -> Toast.makeText(
                requireContext(),
                e.getMessage(),
                Toast.LENGTH_SHORT
            ).show()
        );
    }

    private void loadMealTrackingRealTime(String messId) {

        // Remove previous listener to avoid duplicates
        if (mealListener != null) {
            mealListener.remove();
        }

        b.tvMonthYear.setText(monthYear.toString());

        mealListener = ds.listenMealsRealtime(
            messId,
            monthYear.getMonth(),
            monthYear.getYear(),
            mealDtos -> {

                // Map: day + mealTime → MealTracking
                Map<String, MealTracking> map = new LinkedHashMap<>();

                for (MealTrackingDto dto : mealDtos) {

                    // find member info
                    MessMember member = null;
                    for (MessMember m : cachedMembers) {
                        if (m.userId.equals(dto.userId)) {
                            member = m;
                            break;
                        }
                    }
                    if (member == null) continue;

                    String key = dto.day + "_" + dto.mealTime;

                    MealTracking tracking = map.computeIfAbsent(key, k -> new MealTracking(
                        dto.mealTime,
                        0,
                        new ArrayList<>(),
                        dto.day,
                        dto.year,
                        dto.month
                    ));

                    // individual meal
                    tracking.individualMealList.add(
                        new MealTracking.IndividualMeal(
                            member,
                            dto.mealCount,
                            dto.mealTime
                        )
                    );

                    tracking.mealCount += dto.mealCount;
                }

                List<MealTracking> finalList = new ArrayList<>(map.values());

                adapter.clear();
                adapter.addAll(finalList);
                adapter.notifyDataSetChanged();
            },
            e -> Toast.makeText(
                requireContext(),
                e.getMessage(),
                Toast.LENGTH_SHORT
            ).show()
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mealListener != null) mealListener.remove();
        if (memberListener != null) memberListener.remove();
    }
}