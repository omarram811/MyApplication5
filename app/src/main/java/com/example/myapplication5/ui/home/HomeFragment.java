package com.example.myapplication5.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication5.R;
import com.example.myapplication5.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        homeViewModel.updateAll(requireContext());

        TextView networkOperatorText = root.findViewById(R.id.networkOperatorText);
        networkOperatorText.setText(homeViewModel.getNetworkOperator());

        TextView cellIdText = root.findViewById(R.id.cellIdText);
        cellIdText.setText(homeViewModel.getCellId());

        TextView networkTypeText = root.findViewById(R.id.networkTypeText);
        networkTypeText.setText(homeViewModel.getNetworkType());

        TextView signalStrengthText = root.findViewById(R.id.signalStrengthText);
        signalStrengthText.setText(homeViewModel.getSignalStrength());

        TextView SNRText = root.findViewById(R.id.snrText);
        SNRText.setText(homeViewModel.getSnr());

        TextView DateText = root.findViewById(R.id.timeText);
        DateText.setText(homeViewModel.getTime());

        TextView FrequencyText = root.findViewById(R.id.frequencyText);
        FrequencyText.setText(homeViewModel.getFrequency());

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}