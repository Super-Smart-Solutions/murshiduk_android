package com.saatco.murshadik;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.saatco.murshadik.Helpers.ProfileHelper;
import com.saatco.murshadik.Helpers.ToolbarHelper;
import com.saatco.murshadik.api.APIClient;
import com.saatco.murshadik.api.APIInterface;
import com.saatco.murshadik.api.response.WeatherResponse;
import com.saatco.murshadik.databinding.ActivityWeatherDetailBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherDetailActivity extends AppCompatActivity {

    TextView tvWeather;
    TextView tvCity;
    TextView tvFirstTemp;
    TextView tvSecondTemp;
    TextView tvThirdTemp;
    TextView tvFouthTemp;
    TextView tvFifthTemp;
    TextView tvTitle;
    TextView tvHumadity;
    TextView tvWind;
    TextView tvPressure;
    TextView tvDeposition;
    TextView tvFirstDay;
    TextView tvSecondDay;
    TextView tvThirdDay;
    TextView tvForthDay;
    TextView tvFifthDay;
    CardView cvWeatherNotifications;
    ImageView ivWeatherIcon;
    ImageView ivFirst;
    ImageView ivSecond;
    ImageView ivThird;
    ImageView ivFourth;
    ImageView ivFifth;

    ActivityWeatherDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWeatherDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initViews();

        ToolbarHelper.setToolBar(this, "", binding.appBar.toolbarTrans);

        if (ProfileHelper.getAccount(getApplicationContext()).getPrefix() != null)
            tvTitle.setText(ProfileHelper.getAccount(getApplicationContext()).getPrefix());

        cvWeatherNotifications.setOnClickListener(view -> {
            FragmentSelectorActivity.startActivity(this, FragmentSelectorActivity.NOTIFICATION_PAGE, FragmentSelectorActivity.WEATHER_NOTIFICATION_CODE);
        });

        getWeather();
    }

    private void initViews() {
        tvWeather = binding.tvWeather;
        tvCity = binding.tvCity;
        tvFirstTemp = binding.tvFirstTemp;
        tvSecondTemp = binding.tvSecondTemp;
        tvThirdTemp = binding.tvThirdTemp;
        tvFouthTemp = binding.tvFouthTemp;
        tvFifthTemp = binding.tvFifthTemp;
        tvTitle = binding.tvTitle;
        tvHumadity = binding.tvHumadity;
        tvWind = binding.tvWind;
        tvPressure = binding.tvPressure;
        tvDeposition = binding.tvDeposition;
        tvFirstDay = binding.tvFirstDay;
        tvSecondDay = binding.tvSecondDay;
        tvThirdDay = binding.tvThirdDay;
        tvForthDay = binding.tvForthDay;
        tvFifthDay = binding.tvFifthDay;
        cvWeatherNotifications = binding.cvWeatherNotification;
        ivWeatherIcon = binding.ivWeatherIcon;
        ivFirst = binding.ivFirst;
        ivSecond = binding.ivSecond;
        ivThird = binding.ivThird;
        ivFourth = binding.ivFourth;
        ivFifth = binding.ivFifth;
    }

    private void getWeather() {

        String weather = "OERK";
        if (ProfileHelper.getAccount(getApplicationContext()) != null)
            weather = ProfileHelper.getAccount(getApplicationContext()).getWeatherCode();

        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<WeatherResponse> call = apiInterface.getWeather(weather);
        call.enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {
                try {
                    assert response.body() != null;
                    tvWeather.setText(String.format("%s°c", response.body().getCurrentTemperature()));
                    ivWeatherIcon.setImageResource(getWeatherIcon(response.body().getCurrentIcon()));

                    if (ProfileHelper.getAccount(getApplicationContext()).getCity() != null)
                        tvCity.setText(response.body().getWeather_PhenomenonAr());
                    else
                        tvCity.setText(response.body().getWeather_PhenomenonAr());

                    tvHumadity.setText(response.body().getHumidity());
                    tvWind.setText(response.body().getWindspeed());
                    tvPressure.setText(response.body().getPressure());
                    tvDeposition.setText(response.body().getDewpoint());


                    tvFirstTemp.setText(response.body().getFirstdayMinTemperature().concat("°c / ").concat(response.body().getFirstdayMaxTemperature()).concat("°c"));
                    tvSecondTemp.setText(response.body().getSeconddayMinTemperature().concat("°c / ").concat(response.body().getSeconddayMaxTemperature()).concat("°c"));
                    tvThirdTemp.setText(response.body().getThirddayMinTemperature().concat("°c / ").concat(response.body().getThirddayMaxTemperature()).concat("°c"));
                    tvFouthTemp.setText(response.body().getFourthdayMinTemperature().concat("°c / ").concat(response.body().getFourthdayMaxTemperature()).concat("°c"));
                    tvFifthTemp.setText(response.body().getFifthdayMinTemperature().concat("°c / ").concat(response.body().getFifthdayMaxTemperature()).concat("°c"));


                    ivFirst.setImageResource(getWeatherIcon(response.body().getFirstdayIcon()));
                    ivSecond.setImageResource(getWeatherIcon(response.body().getSeconddayIcon()));
                    ivThird.setImageResource(getWeatherIcon(response.body().getThirddayIcon()));
                    ivFourth.setImageResource(getWeatherIcon(response.body().getFourthdayIcon()));
                    ivFifth.setImageResource(getWeatherIcon(response.body().getFifthdayIcon()));

                    SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa", Locale.ENGLISH);
                    Date firstDate = sdf.parse(response.body().getFirstDate());
                    tvFirstDay.setText(getArabicDay(firstDate));

                    Date secondDate = sdf.parse(response.body().getSecondDate());
                    tvSecondDay.setText(getArabicDay(secondDate));

                    Date thirdDate = sdf.parse(response.body().getThirdDate());
                    tvThirdDay.setText(getArabicDay(thirdDate));

                    Date forthDate = sdf.parse(response.body().getFourthDate());
                    tvForthDay.setText(getArabicDay(forthDate));

                    Date fifthDate = sdf.parse(response.body().getFifthDate());
                    tvFifthDay.setText(getArabicDay(fifthDate));

                    tvTitle.setText(response.body().getCity_Ar());

                } catch (Exception ignored) {
                }
            }

            @Override
            public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) {

            }
        });
    }

    private int getWeatherIcon(String icon) {
        String newIcon = icon.replaceAll(" .*", "").replace("-", "_");
        return getResources().getIdentifier(newIcon, "drawable", getPackageName());
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));

        Configuration config = new Configuration(base.getResources().getConfiguration());
        config.fontScale = 1.2f;
        applyOverrideConfiguration(config);
    }

    private String getArabicDay(Date date) {

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int day = cal.get(Calendar.DAY_OF_WEEK);

        switch (day) {
            case 2:
                return "الاثنين";
            case 3:
                return "الثلاثاء";
            case 4:
                return "الأربعاء";
            case 5:
                return "الخميس";
            case 6:
                return "الجمعة";
            case 7:
                return "السبت";
            default:
                return "الأحد";
        }
    }

}