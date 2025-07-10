package com.saatco.murshadik;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.saatco.ItemOffsetDecoration;
import com.saatco.murshadik.Helpers.TokenHelper;
import com.saatco.murshadik.Helpers.ToolbarHelper;
import com.saatco.murshadik.adapters.TimeSlotAdapter;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.api.APIInterface;
import com.saatco.murshadik.api.response.BaseResponse;
import com.saatco.murshadik.api.response.LabsResponse;
import com.saatco.murshadik.databinding.ActivityBookAppointmentBinding;
import com.saatco.murshadik.model.Appointment;
import com.saatco.murshadik.model.Item;
import com.saatco.murshadik.model.Lab;
import com.saatco.murshadik.utils.Util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookAppointmentActivity extends AppCompatActivity implements TimeSlotAdapter.OnSelectItemClickListener {


    FrameLayout btnLabs;
    FrameLayout btnRegions;
    FrameLayout btnDate;
    TextView tvLab;
    TextView tvDate;
    TextView tvRegion;
    CalendarView calenderView;
    RecyclerView recyclerView;

    private final ArrayList<String> regions = new ArrayList<>();
    private final ArrayList<String> labs = new ArrayList<>();
    private ArrayList<Appointment> appointments = new ArrayList<>();
    private ArrayList<Lab> labList = new ArrayList<>();


    String date = Util.getCurrentDate();

    int appointmentId;
    int labId = 0;
    TimeSlotAdapter adapter;
    ArrayList<Item> regionArrayList = new ArrayList<>();

    int regionId = 0;

    View loader;

    ActivityBookAppointmentBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBookAppointmentBinding.inflate(getLayoutInflater(), null, false);
        setContentView(binding.getRoot());

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        initViews();
        initActions();

        ToolbarHelper.setToolBar(this, "", findViewById(R.id.toolbarTrans));




        getLabData();


        calenderView.setMinDate(System.currentTimeMillis());
        calenderView.setOnDateChangeListener((calendarView, year, month, day) -> {

            final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            Calendar c = Calendar.getInstance();
            c.set(year, month, day);

            date = dateFormat.format(c.getTime());
            if (labId != 0)
                getAppointmentsByLabId(labId);
        });

    }

    private void initViews() {
        btnLabs = binding.layoutLab;
        btnRegions = binding.layoutRegion;
        btnDate = binding.layoutDate;

        tvLab = binding.tvLab;
        tvDate = binding.tvDate;
        tvRegion = binding.tvRegion;

        calenderView = binding.calendarView;

        recyclerView = binding.slotsRecycleView;

        loader = binding.llProgressBar.getRoot();
    }

    private void initActions() {
        btnRegions.setOnClickListener(v -> showRegionDialog());
        btnLabs.setOnClickListener(v -> showLabsDialog());
        binding.btnDone.setOnClickListener(v -> {
            if (labId == 0) {
                Util.showErrorToast("الرجاء اختيار المختبر أولاً", this);
            } else if (appointmentId == 0)
                Util.showErrorToast("حدد الوقت", this);
            else
                bookAppointment();
        });

    }


    @Override
    public void onCategoryClick(View view, int position, Appointment selectedSlot) {

        if (selectedSlot.isBooked())
            Util.showToast("تم حجز الموعد بالفعل", this);

        appointmentId = selectedSlot.getId();

        for (Appointment slot : appointments) {
            if (!slot.isBooked()) {
                slot.setSelected(slot.getId() == selectedSlot.getId());
            }
        }

        adapter.notifyDataSetChanged();
    }

    private void getLabData() {

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<LabsResponse> call = apiInterface.getLabData("Bearer " + TokenHelper.getToken());
        call.enqueue(new Callback<LabsResponse>() {
            @Override
            public void onResponse(@NonNull Call<LabsResponse> call, @NonNull Response<LabsResponse> response) {

                try {

                    assert response.body() != null;
                    labList = response.body().getLabs();

                    regionArrayList = response.body().getRegions();

                    if (regionArrayList != null) {
                        for (Item city : regionArrayList) {
                            regions.add(city.getNameAr());
                        }
                    }

                } catch (Exception ignored) {
                }
            }

            @Override
            public void onFailure(@NonNull Call<LabsResponse> call, @NonNull Throwable t) {

            }
        });

    }

    private void getAppointmentsByLabId(int labId) {

        loader.setVisibility(View.VISIBLE);;

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<List<Appointment>> call = apiInterface.getAppointments("Bearer " + TokenHelper.getToken(), labId, date);
        call.enqueue(new Callback<List<Appointment>>() {
            @Override
            public void onResponse(@NonNull Call<List<Appointment>> call, @NonNull Response<List<Appointment>> response) {

                loader.setVisibility(View.GONE);

                try {

                    ArrayList<Appointment> appointmentList = (ArrayList<Appointment>) response.body();

                    appointments = appointmentList;
                    setSlotData();

                    assert appointmentList != null;
                    if (appointmentList.isEmpty()) {
                        appointmentId = 0;
                        Util.showToast("لاتوجد مواعيد متاحه في هذا اليوم", BookAppointmentActivity.this);
                    }

                } catch (Exception ignored) {
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Appointment>> call, @NonNull Throwable t) {
                loader.setVisibility(View.GONE);
            }
        });

    }

    private void bookAppointment() {

        loader.setVisibility(View.VISIBLE);;

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<BaseResponse> call = apiInterface.bookAppointment("Bearer " + TokenHelper.getToken(), appointmentId);
        call.enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(@NonNull Call<BaseResponse> call, @NonNull Response<BaseResponse> response) {

                loader.setVisibility(View.GONE);

                try {

                    Util.showSuccessToast("تم حجز الموعد بنجاح", BookAppointmentActivity.this);
                    finish();

                } catch (Exception ignored) {
                }
            }

            @Override
            public void onFailure(@NonNull Call<BaseResponse> call, @NonNull Throwable t) {
                loader.setVisibility(View.GONE);
            }
        });
    }

    private void showRegionDialog() {

        final Dialog dialog = new Dialog(BookAppointmentActivity.this);
        dialog.setContentView(R.layout.dialog_regions);

        ListView listView = dialog.findViewById(R.id.statusListView);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, regions);


        // Assign adapter to ListView
        listView.setAdapter(adapter);

        // ListView Item Click Listener
        listView.setOnItemClickListener((parent, view, position, id) -> {

            // ListView Clicked item index
            String itemValue = (String) listView.getItemAtPosition(position);

            regionId = regionArrayList.get(position).getId();
            tvRegion.setText(itemValue);
            tvLab.setText("حدد مختبر");
            labId = 0;


            dialog.dismiss();

        });

        dialog.show();

    }

    private void showLabsDialog() {

        final Dialog dialog = new Dialog(BookAppointmentActivity.this);
        dialog.setContentView(R.layout.dialog_regions);

        ListView listView = dialog.findViewById(R.id.statusListView);

        TextView title = dialog.findViewById(R.id.title);
        title.setText("حدد مختبر");

        labs.clear();

        ArrayList<Lab> newLabList = new ArrayList<>();

        if (labList != null) {
            for (Lab lab : labList) {
                labs.add(lab.getName() + " - " + lab.getCity().getNameAr());
                newLabList.add(lab);
            }

        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, labs);

        // Assign adapter to ListView
        listView.setAdapter(adapter);

        // ListView Item Click Listener
        listView.setOnItemClickListener((parent, view, position, id) -> {

            // ListView Clicked item index
            String itemValue = (String) listView.getItemAtPosition(position);

            tvLab.setText(itemValue);
            labId = newLabList.get(position).getId();
            // getAppointmentsByLabId(labId);

            dialog.dismiss();

        });

        dialog.show();

    }

    private void setSlotData() {

        ArrayList<String> slots = new ArrayList<>();

        for (Appointment appointment : appointments)
            slots.add(appointment.getTime());


        Collections.reverse(slots);

        adapter = new TimeSlotAdapter(appointments, getApplicationContext(), this);
        //  recyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(),3));
        recyclerView.setLayoutManager(new LinearLayoutManager(BookAppointmentActivity.this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        // 3 columns
        // 50px
        new ItemOffsetDecoration(getApplicationContext(), R.dimen.item_offset);

        adapter.notifyDataSetChanged();

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));

        Configuration config = new Configuration(base.getResources().getConfiguration());
        config.fontScale = 1.2f;
        applyOverrideConfiguration(config);
    }
}