package com.saatco.murshadik.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;
import com.saatco.murshadik.Helpers.ProfileHelper;
import com.saatco.murshadik.MarketDetailActivity;
import com.saatco.murshadik.R;
import com.saatco.murshadik.model.Market;
import com.saatco.murshadik.utils.Consts;
import com.saatco.murshadik.utils.FloatingMarkerTitlesOverlay;

import java.util.ArrayList;

public class MarketMapFragment extends Fragment  {

    ArrayList<Market> markets = new ArrayList<>();

    public static  GoogleMap googleMap1;

    private FloatingMarkerTitlesOverlay floatingMarkersOverlay;


    private final OnMapReadyCallback callback = new OnMapReadyCallback() {


        @Override
        public void onMapReady(GoogleMap googleMap) {
            googleMap1 = googleMap;

            googleMap1.clear();


            setMarkets();

            googleMap1.setOnMarkerClickListener(marker -> {

                Intent intent = new Intent(getContext(), MarketDetailActivity.class);
                intent.putExtra("ID",Integer.parseInt(marker.getSnippet()));
                startActivity(intent);

                return true;
            });
        }
    };

    public static MarketMapFragment newInstance(Bundle bundle) {
        MarketMapFragment fragment = new MarketMapFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_market_map, container, false);

        Bundle bundle = getArguments();
        markets = (ArrayList<Market>) bundle.getSerializable("MARKETS");

        return view;
    }

    private void setMarkets(){

        for(int i = 0 ; i < markets.size() ; i++)
        {
            Market market = markets.get(i);

            if(market.getLocation().matches(".*[0-9].*") && market.getLocation().length() > 2) {

                String lati = Consts.DEFAULT_LAT;

                String[] newLoc = market.getLocation().replace(" ", "").split(",");
                if(newLoc[0] != null && !newLoc[0].equals(""))
                    lati = newLoc[0];

                String longi = Consts.DEFAULT_LONG;

                if(newLoc[1] != null && !newLoc[1].equals(""))
                    longi = newLoc[1];

                googleMap1.addMarker(new MarkerOptions()
                        .position(new LatLng(Double.parseDouble(lati), Double.parseDouble(longi)))
                        .anchor(0.5f, 0.5f)
                        .icon(BitmapDescriptorFactory.fromBitmap(getMarkerIconWithLabel(market.getName(), 20.0f)))
                        .snippet(String.valueOf(market.getId()))
                        .title(market.getName()));
            }

        }

        if(!markets.isEmpty()){
            showFirstMarketAccordingRegion();
        }

    }

    //************************* move camera to first market of user region *****************************//
    private void showFirstMarketAccordingRegion(){

        int regionId = ProfileHelper.getAccount(getContext()) != null ? ProfileHelper.getAccount(getContext()).getRegionId() : 0;
        int index = 0;

        Market market = markets.get(0);

        for(Market marketItems : markets){
            if(marketItems.getRegionId() == regionId) {
                market = marketItems;
                break;
            }
        }

        googleMap1.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(Double.parseDouble(market.getLatitude()), Double.parseDouble(market.getLongitude()))));
        googleMap1.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(market.getLatitude()),Double.parseDouble(market.getLongitude())), 16.0f));

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);


        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }


    public Bitmap getMarkerIconWithLabel(String label, float angle) {
        IconGenerator iconGenerator = new IconGenerator(getContext());
        View markerView = LayoutInflater.from(getContext()).inflate(R.layout.lay_marker, null);
        ImageView imgMarker = markerView.findViewById(R.id.img_marker);
        TextView tvLabel = markerView.findViewById(R.id.tv_label);
        imgMarker.setImageResource(R.drawable.map_icon);
        tvLabel.setText(label);
        iconGenerator.setContentView(markerView);
        iconGenerator.setBackground(null);
        return iconGenerator.makeIcon(label);
    }

}