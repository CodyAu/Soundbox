package ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.IPieDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.R;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.application.Observables;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.SongController;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.SongStatisticController;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.observables.SoundboxObserver;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.utils.SongFilters;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.logic.utils.SongStatisticFilters;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.Song;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.SongCollection;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.models.SongStatistic;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.activities.MainActivity;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.utils.Calculate;
import ca.umanitoba.cs.code.comp3350.winter2020.Soundbox.presentation.utils.Themes;

public class AnalyticsFragment extends BottomNavigationTab {
    private static final float OTHER_THRESHOLD = 0.05f;
    private static final float OTHER_MIN_THRESHOLD = 0.02f;

    private static final int MIN_BAR_CHART_ENTRIES = 6;
    private static final int MIN_PIE_CHART_ENTRIES = 50;

    private MainActivity mainActivity;

    private SoundboxObserver<Song> songObserver;

    private Toolbar toolbar;

    private SongController songController;
    private SongStatisticController songStatisticController;

    private NestedScrollView scrollView;

    private ViewGroup listenTimeContainer;
    private ViewGroup playsContainer;

    private PieChart listenTimePieChart;
    private PieChart playsPieChart;

    private HorizontalBarChart artistBarChart;
    private HorizontalBarChart albumBarChart;
    private HorizontalBarChart genreBarChart;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainActivity = (MainActivity) getActivity();

        songObserver = (observer, arg) -> updateSong(observer.getValue());

        songController = new SongController();
        songStatisticController = new SongStatisticController();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_analytics, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mainActivity.changeNavigationView(4);

        toolbar = (Toolbar) view.findViewById(R.id.toolbar);

        scrollView = (NestedScrollView) view.findViewById(R.id.nested_scroll_view);

        listenTimeContainer = view.findViewById(R.id.listen_time_pie_chart_container);
        playsContainer = view.findViewById(R.id.plays_pie_chart_container);

        listenTimePieChart = (PieChart) view.findViewById(R.id.listen_time_pie_chart);
        playsPieChart = (PieChart) view.findViewById(R.id.plays_pie_chart);

        artistBarChart = (HorizontalBarChart) view.findViewById(R.id.artist_bar_chart);
        albumBarChart = (HorizontalBarChart) view.findViewById(R.id.album_bar_chart);
        genreBarChart = (HorizontalBarChart) view.findViewById(R.id.genre_bar_chart);

        List<Song> songs = songController.getAllSongs();
        List<SongCollection> artists = SongFilters.groupSongsByArtist(songs);
        List<SongCollection> albums = SongFilters.groupSongsByAlbum(songs);
        List<SongCollection> genres = SongFilters.groupSongsByGenre(songs);

        List<SongStatistic> listenTime = songStatisticController.getStatisticsByType(SongStatistic.Statistic.LISTEN_TIME);
        List<SongStatistic> plays = songStatisticController.getStatisticsByType(SongStatistic.Statistic.PLAYS);

        initToolbar();

        String listenTimeLabel = "Listen time";
        String playsLabel = "Plays";

        initPieCharts(listenTimePieChart, getDataPieCharts(listenTime, listenTimeLabel));
        initPieCharts(playsPieChart, getDataPieCharts(plays, playsLabel));

        initBarCharts(artistBarChart, getDataBarCharts(artists, "Artists count"), songs.size());
        initBarCharts(albumBarChart, getDataBarCharts(albums, "Albums count"), songs.size());
        initBarCharts(genreBarChart, getDataBarCharts(genres, "Genres count"), songs.size());

        addStatisticTitleText(listenTimeContainer, listenTimeLabel, 20);
        addStatisticTitleText(playsContainer, playsLabel, 20);

        addStatisticBullet(listenTimeContainer, "Total listen time:", Calculate.formatTimeReadable(SongStatisticFilters.sumStatisticsValues(listenTime), TimeUnit.SECONDS), 16);
        addStatisticBullet(playsContainer, "Total plays:", String.valueOf(SongStatisticFilters.sumStatisticsValues(plays)), 16);
    }

    private void initToolbar() {
        toolbar.setTitle(getString(R.string.title_analytics));
        mainActivity.setSupportActionBar(toolbar);
    }

    private void initPieCharts(PieChart chart, PieData data) {
        IPieDataSet dataSet = data.getDataSet();

        chart.setUsePercentValues(true);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.setExtraOffsets(5, 5, 5, 5);
        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            PieEntry last = null;

            @Override
            public void onValueSelected(Entry e, Highlight h) {
                PieEntry entry = (PieEntry) e;
                entry.setLabel((String) entry.getData());

                removeLast();
                last = entry;
            }

            @Override
            public void onNothingSelected() {
                removeLast();
            }

            private void removeLast() {
                if (last != null) {
                    last.setLabel("");
                    last = null;
                }
            }
        });

        float holeRadius = 35.0f;
        float transparentRadius = 5.0f;
        chart.setDrawHoleEnabled(true);
        chart.setHoleRadius(holeRadius);
        chart.setHoleColor(Color.TRANSPARENT);
        chart.setTransparentCircleRadius(holeRadius + transparentRadius);

        chart.animateY(1000, Easing.EaseInOutQuad);

        chart.setEntryLabelColor(Themes.getThemeColor(mainActivity, R.attr.themeTextColorPrimary));
        chart.setEntryLabelTextSize(14);

        chart.setDrawCenterText(true);
        chart.setCenterText(dataSet.getLabel());
        chart.setCenterTextSize(20);
        chart.setCenterTextColor(Themes.getThemeColor(mainActivity, R.attr.themeTextColorPrimary));

        dataSet.setValueTextColor(Themes.getThemeColor(mainActivity, R.attr.themeTextColorPrimary));
        dataSet.setValueTextSize(13);

        dataSet.setValueFormatter(new PercentFormatter(chart));

        chart.setData(data);
    }

    private void initBarCharts(HorizontalBarChart chart, BarData data, int maxYRange) {
        IBarDataSet dataSet = data.getDataSetByIndex(0);
        List<String> xAxisValues = new ArrayList<>();
        for (int i = 0; i < dataSet.getEntryCount(); i++) {
            xAxisValues.add((String) dataSet.getEntryForIndex(i).getData());
        }
        Collections.reverse(xAxisValues);

        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.setPinchZoom(false);
        chart.setDoubleTapToZoomEnabled(false);
        chart.setDrawValueAboveBar(true);
        chart.setFitBars(true);
        chart.setDrawGridBackground(false);

        chart.animateY(1000, Easing.EaseInOutQuad);

        XAxis xAxis = chart.getXAxis();
        xAxis.setTextColor(Themes.getThemeColor(mainActivity, R.attr.themeTextColorPrimary));
        xAxis.setDrawLabels(true);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setGranularity(1);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisValues));

        YAxis yAxis = chart.getAxisLeft();
        yAxis.setAxisMinimum(0);
        yAxis.setAxisMaximum(maxYRange);
        yAxis.setGranularity(1);
        yAxis.setTextColor(Themes.getThemeColor(mainActivity, R.attr.themeTextColorPrimary));
        yAxis.setEnabled(false);

        yAxis = chart.getAxisRight();
        yAxis.setAxisMinimum(0);
        yAxis.setAxisMaximum(maxYRange);
        yAxis.setGranularity(1);
        yAxis.setTextColor(Themes.getThemeColor(mainActivity, R.attr.themeTextColorPrimary));

        dataSet.setValueTextColor(Themes.getThemeColor(mainActivity, R.attr.themeTextColorPrimary));
        dataSet.setValueFormatter(new DefaultAxisValueFormatter(0));

        chart.setData(data);

        // Add title
        addStatisticTitleText((ViewGroup) chart.getParent(), dataSet.getLabel(), 20);
    }

    private PieData getDataPieCharts(List<SongStatistic> statistics, String label) {
        List<SongStatistic> statisticEntries = new ArrayList<>(statistics);
        Collections.sort(statisticEntries, (statistic1, statistic2) -> statistic2.getValue() - statistic1.getValue());
        int sumTotal = SongStatisticFilters.sumStatisticsValues(statisticEntries);

        List<PieEntry> entries = new ArrayList<>();
        int valueSum = 0;
        for (SongStatistic statistic : statisticEntries) {
            if (statistic.getValue() > sumTotal * OTHER_THRESHOLD || (entries.size() < MIN_PIE_CHART_ENTRIES && statistic.getValue() > sumTotal * OTHER_MIN_THRESHOLD)) {
                PieEntry entry = new PieEntry(statistic.getValue());
                entry.setData(statistic.getSong().getSongName());
                entries.add(entry);
                valueSum += statistic.getValue();
            }
        }
        int otherTime = sumTotal - valueSum;
        if (otherTime > 0) {
            PieEntry entry = new PieEntry(otherTime);
            entry.setData("Other");
            entries.add(entry);
        }

        PieDataSet dataSet = new PieDataSet(entries, label);
        dataSet.setSliceSpace(3f);
        dataSet.setColors(Themes.getMaterialColors());
        dataSet.setUsingSliceColorAsValueLineColor(true);

        return new PieData(dataSet);
    }

    private BarData getDataBarCharts(List<SongCollection> collections, String label) {
        List<SongCollection> statisticEntries = new ArrayList<>(collections);
        Collections.sort(statisticEntries, (statistic1, statistic2) -> statistic2.songsSize() - statistic1.songsSize());

        List<BarEntry> entries = new ArrayList<>();
        int entriesCount = Math.min(statisticEntries.size(), MIN_BAR_CHART_ENTRIES);
        for (int i = 0; i < entriesCount; i++) {
            SongCollection collection = statisticEntries.get(i);

            BarEntry entry = new BarEntry(entriesCount - i - 1, collection.songsSize());
            entry.setData(collection.getName());
            entries.add(entry);
        }

        BarDataSet dataSet = new BarDataSet(entries, label);
        dataSet.setColors(Themes.getMaterialColors());

        return new BarData(dataSet);
    }

    private void addStatisticTitleText(ViewGroup parent, String text, int textSize) {
        int padding = Calculate.dpToPx(mainActivity, 10);

        TextView yAxisName = new TextView(mainActivity, null);

        yAxisName.setText(text);
        yAxisName.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        yAxisName.setTextSize(textSize);
        yAxisName.setPadding(padding, padding, padding, padding);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER | Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;

        parent.addView(yAxisName, 0, params);
    }

    private void addStatisticBullet(ViewGroup parent, String title, String text, int textSize) {
        int padding = Calculate.dpToPx(mainActivity, 15);

        LinearLayout linearLayout = new LinearLayout(mainActivity, null);

        TextView bulletTitle = new TextView(mainActivity, null);
        bulletTitle.setText(title);
        bulletTitle.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        bulletTitle.setTextSize(textSize);
        bulletTitle.setPadding(padding, padding, padding, padding);

        TextView bulletText = new TextView(mainActivity, null);
        bulletText.setText(text);
        bulletText.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
        bulletText.setTextSize(textSize);
        bulletText.setTextColor(Themes.getThemeColor(mainActivity, R.attr.themeTextColorSecondary));
        bulletText.setPadding(padding, padding, padding, padding);

        linearLayout.addView(bulletTitle);
        linearLayout.addView(bulletText);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER | Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;

        parent.addView(linearLayout, params);
    }

    private void registerObservers() {
        Observables.getPlaybackSongObservable().addObserver(songObserver);
    }

    private void unregisterObservers() {
        Observables.getPlaybackSongObservable().deleteObserver(songObserver);
    }

    public void updateSong(Song song) {

    }

    @Override
    public void onDuplicateTap() {
        scrollView.smoothScrollTo(0, 0);
    }

    @Override
    public void onResume() {
        super.onResume();

        registerObservers();
    }

    @Override
    public void onPause() {
        super.onPause();

        unregisterObservers();
    }

    @Override
    public void onDestroy() {
        unregisterObservers();

        super.onDestroy();
    }

}
