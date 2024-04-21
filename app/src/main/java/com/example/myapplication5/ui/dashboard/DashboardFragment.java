package com.example.myapplication5.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModelProvider;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.widget.Button;
import com.example.myapplication5.R;
import java.text.SimpleDateFormat;

import java.util.Calendar;

import com.example.myapplication5.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(requireActivity()).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        dashboardViewModel.getAvgConnOperator().observe(getViewLifecycleOwner(), binding.AvgConnOperatorText::setText);
        dashboardViewModel.getAvgConnNetwork().observe(getViewLifecycleOwner(), binding.AvgConnNetworkText::setText);
        dashboardViewModel.getAvgSigPowType().observe(getViewLifecycleOwner(), binding.AvgSigPowTypeText::setText);
        dashboardViewModel.getAvgSigPowDevice().observe(getViewLifecycleOwner(), binding.AvgSigPowDeviceText::setText);
        dashboardViewModel.getAvgSNRType().observe(getViewLifecycleOwner(), binding.AvgSNRTypeText::setText);
        dashboardViewModel.getStartDate().observe(getViewLifecycleOwner(), binding.textStart::setText);
        dashboardViewModel.getEndDate().observe(getViewLifecycleOwner(), binding.textEnd::setText);


        Button buttonStart = root.findViewById(R.id.buttonStart);
        buttonStart.setOnClickListener(view -> {
            showDateTimeDialog(dashboardViewModel.getStartDate());
        });

        Button buttonEnd = root.findViewById(R.id.buttonEnd);
        buttonEnd.setOnClickListener(view -> {
            showDateTimeDialog(dashboardViewModel.getEndDate());
        });

        final TextView textView = binding.textDashboard;
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    private void showDateTimeDialog(final MutableLiveData<String> mData) {
        final Calendar calendar=Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener= (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR,year);
            calendar.set(Calendar.MONTH,month);
            calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);

            TimePickerDialog.OnTimeSetListener timeSetListener= (view1, hourOfDay, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                calendar.set(Calendar.MINUTE,minute);

                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yy-MM-dd HH:mm");

                mData.setValue(simpleDateFormat.format(calendar.getTime()));
            };

            new TimePickerDialog(requireContext(),timeSetListener,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false).show();
        };

        new DatePickerDialog(requireContext(),dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}