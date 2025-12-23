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
import com.github.abrarshakhi.mmap.data.dto.MessMemberDto;
import com.github.abrarshakhi.mmap.databinding.FragmentManagementBinding;
import com.github.abrarshakhi.mmap.domain.model.MessMember;
import com.github.abrarshakhi.mmap.presentation.activities.AddMemberActivity;
import com.github.abrarshakhi.mmap.presentation.activities.MemberInfoActivity;
import com.github.abrarshakhi.mmap.presentation.adapters.MemberListAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

public class ManagementFragment extends Fragment {
    private FragmentManagementBinding binding;
    private HomeDataSource ds;
    private MemberListAdapter memberListAdapter;

    public ManagementFragment() {
    }

    @NonNull
    public static ManagementFragment newInstance() {
        return new ManagementFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ds = new HomeDataSource(requireContext());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentManagementBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        memberListAdapter = new MemberListAdapter(requireContext(), new ArrayList<>(), messMember -> {
        });
        binding.lvMemberList.setAdapter(memberListAdapter);
        binding.btnAddMember.setVisibility(View.GONE);
        loadMessMembers(ds.getCurrentMessId(), ds,
            members -> {
                memberListAdapter.clear();
                memberListAdapter.addAll(members);
                for (var mem : members) {
                    if (mem.userId.equals(ds.getLoggedInUser().getUid())) {
                        memberListAdapter.setListener(messMember -> {
                            var b = new Bundle();
                            b.putSerializable("MEM", messMember);
                            b.putString("ROLE", mem.role);
                            startActivity(new Intent(requireContext(), MemberInfoActivity.class).putExtras(b));
                        });
                        if (mem.role.equals(MessMemberRole.ADMIN)) {
                            binding.btnAddMember.setVisibility(View.VISIBLE);
                        } else {
                            binding.btnAddMember.setVisibility(View.GONE);
                        }
                        break;
                    }
                }
                memberListAdapter.notifyDataSetChanged();
            },
            e -> {
                Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        );

        binding.btnAddMember.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), AddMemberActivity.class));
        });
    }

    private void loadMessMembers(
        String messId,
        @NonNull HomeDataSource dataSource,
        OnSuccessListener<List<MessMember>> success,
        OnFailureListener failure
    ) {
        dataSource.getMess(
            messId,
            mess -> {
                if (mess.members == null || mess.members.isEmpty()) {
                    success.onSuccess(new ArrayList<>());
                    return;
                }

                List<MessMember> result = new ArrayList<>();
                int total = mess.members.size();

                for (MessMemberDto memberDto : mess.members) {
                    dataSource.fetchUserProfile(
                        memberDto.userId,
                        userDto -> {
                            String fullName = userDto != null
                                ? userDto.fullName
                                : "Unknown";

                            MessMember member = new MessMember(
                                memberDto.userId,
                                memberDto.messId,
                                memberDto.role,
                                memberDto.joinedAt,
                                memberDto.houseRent,
                                memberDto.utility,
                                fullName
                            );

                            result.add(member);

                            // When all users loaded
                            if (result.size() == total) {
                                success.onSuccess(result);
                            }
                        },
                        failure
                    );
                }
            },
            failure
        );
    }

}