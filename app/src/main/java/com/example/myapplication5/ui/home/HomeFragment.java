package com.example.myapplication5.ui.home;

import android.content.ContentValues;
import android.content.Context;

import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.myapplication5.R;
import com.example.myapplication5.databinding.FragmentHomeBinding;

import java.util.Objects;

import com.example.myapplication5.UIDataExtractor;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        Context context = requireContext();

        TextView networkOperatorText = root.findViewById(R.id.networkOperatorText);
        networkOperatorText.setText(UIDataExtractor.getOperator(context));


        TextView cellIdText = root.findViewById(R.id.cellIdText);
        cellIdText.setText(UIDataExtractor.getCellID(context));

        TextView networkTypeText = root.findViewById(R.id.networkTypeText);
        networkTypeText.setText(UIDataExtractor.getNetworkType(context));

        TextView signalStrengthText = root.findViewById(R.id.signalStrengthText);
        signalStrengthText.setText(UIDataExtractor.getSignalStrength(context));


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