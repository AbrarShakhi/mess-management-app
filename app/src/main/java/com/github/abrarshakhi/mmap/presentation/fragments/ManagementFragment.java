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
import com.github.abrarshakhi.mmap.databinding.FragmentManagementBinding;
import com.github.abrarshakhi.mmap.presentation.activities.AddMemberActivity;
import com.github.abrarshakhi.mmap.presentation.activities.MemberInfoActivity;
import com.github.abrarshakhi.mmap.presentation.adapters.MemberListAdapter;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;

public class ManagementFragment extends Fragment {
    private FragmentManagementBinding binding;
    private HomeDataSource ds;
    private MemberListAdapter memberListAdapter;
    private ListenerRegistration memberListener;

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
        memberListener = ds.listenMessMembersRealtime(
                ds.getCurrentMessId(),
                members -> {
                    memberListAdapter.clear();
                    memberListAdapter.addAll(members);

                    // Set listener for logged-in user
                    for (var mem : members) {
                        if (mem.userId.equals(ds.getLoggedInUser().getUid())) {
                            memberListAdapter.setListener(messMember -> {
                                var b = new Bundle();
                                b.putSerializable("MEM", messMember);
                                b.putString("ROLE", mem.role);
                                startActivity(new Intent(requireContext(), MemberInfoActivity.class).putExtras(b));
                            });

                            binding.btnAddMember.setVisibility(mem.role.equals(MessMemberRole.ADMIN) ? View.VISIBLE : View.GONE);
                            break;
                        }
                    }
                    memberListAdapter.notifyDataSetChanged();
                },
                e -> Toast.makeText(requireContext(), e.getMessage(), Toast.LENGTH_SHORT).show()
        );

        binding.btnAddMember.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), AddMemberActivity.class));
        });
    }

    @Override
    public void onDestroyView() {
        if (memberListener != null) memberListener.remove();
        binding = null;
        super.onDestroyView();
    }
}