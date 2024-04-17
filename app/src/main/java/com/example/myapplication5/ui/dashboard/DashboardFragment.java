package com.example.myapplication5.ui.dashboard;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.widget.Button;
import com.example.myapplication5.R;
import java.text.SimpleDateFormat;

import java.util.Calendar;

import com.example.myapplication5.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment {

    private TextView textStartView;
    private TextView textEndView;
    private Button buttonStart;
    private Button buttonEnd;
    private FragmentDashboardBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel =
                new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        textStartView = root.findViewById(R.id.textStart);
        textEndView = root.findViewById(R.id.textEnd);
        buttonStart = root.findViewById(R.id.buttonStart);
        buttonEnd = root.findViewById(R.id.buttonEnd);

        buttonStart.setOnClickListener(view -> {

            showDateTimeDialog(textStartView);
        });

        buttonEnd.setOnClickListener(view -> {
            showDateTimeDialog(textEndView);
        });

        final TextView textView = binding.textDashboard;
        dashboardViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    private void showDateTimeDialog(final TextView textView) {
        final Calendar calendar=Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener= (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR,year);
            calendar.set(Calendar.MONTH,month);
            calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);

            TimePickerDialog.OnTimeSetListener timeSetListener= (view1, hourOfDay, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                calendar.set(Calendar.MINUTE,minute);

                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yy-MM-dd HH:mm");

                textView.setText(simpleDateFormat.format(calendar.getTime()));
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