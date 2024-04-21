package com.example.myapplication5.ui.home;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication5.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;

    private final Handler handler = new Handler();
    private final Runnable fieldsAutoRefresh = new Runnable() {
        @Override
        public void run() {
            homeViewModel.updateAll(requireContext());
            handler.postDelayed(this, 10000);
        }
    };

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        homeViewModel.getNetworkOperator().observe(getViewLifecycleOwner(), binding.networkOperatorText::setText);
        homeViewModel.getCellId().observe(getViewLifecycleOwner(), binding.cellIdText::setText);
        homeViewModel.getNetworkType().observe(getViewLifecycleOwner(), binding.networkTypeText::setText);
        homeViewModel.getSignalStrength().observe(getViewLifecycleOwner(), binding.signalStrengthText::setText);
        homeViewModel.getSnr().observe(getViewLifecycleOwner(), binding.snrText::setText);
        homeViewModel.getTime().observe(getViewLifecycleOwner(), binding.timeText::setText);
        homeViewModel.getFrequency().observe(getViewLifecycleOwner(), binding.frequencyText::setText);

        handler.post(fieldsAutoRefresh);
        return root;
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        handler.removeCallbacks(fieldsAutoRefresh);
    }
}