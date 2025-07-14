package com.infosys.infyme.ui.apps.bookseat;

import static com.infosys.infyme.ui.apps.bookseat.BookSeatFragment.bookSeatFragment;
import static com.infosys.infyme.ui.apps.bookseat.groupbooking.BookGroupSeatFragment.mBookGroupSeatFragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.infosys.infyme.Firebase;
import com.infosys.infyme.R;
import com.infosys.infyme.StaticData;
import com.infosys.infyme.api.BookSeatService;
import com.infosys.infyme.api.CustomRetrofitCallback;
import com.infosys.infyme.api.ServiceGenerator;
import com.infosys.infyme.databinding.CardBookSeatZoomBindingImpl;
import com.infosys.infyme.ui.Common;
import com.infosys.infyme.ui.apps.bookseat.groupbooking.BookGroupSeatFragment;
import com.infosys.infyme.ui.apps.bookseat.model.BookSeatPostModel;
import com.infosys.infyme.ui.apps.bookseat.model.BookSeatTilePostData;
import com.infosys.infyme.ui.apps.bookseat.model.BookSeatTilesDataModel;
import com.infosys.infyme.ui.apps.bookseat.model.BookSeatTilesModel;
import com.infosys.infyme.ui.apps.bookseat.model.BookTilePostResponseModel;
import com.infosys.infyme.ui.apps.bookseat.model.groupbooking.GroupBookingModel;
import com.infosys.infyme.ui.apps.bookseat.model.groupbooking.GroupSeatModel;
import com.infosys.infyme.ui.apps.bookseat.model.groupbooking.ProcessGBModel;
import com.infosys.infyme.ui.apps.bookseat.model.groupbooking.SeatModel;
import com.infosys.infyme.ui.behaviour.EmptyCallback;
import com.infosys.infyme.ui.constants.Constants;
/*import com.moagrius.tileview.TileView;
import com.moagrius.tileview.io.StreamProviderFiles;
import com.moagrius.tileview.plugins.MarkerPlugin;*/

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ir.mahdi.mzip.zip.ZipArchive;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class BookSeatTileViewActivity extends AppCompatActivity {

    private BookSeatTilesModel bookSeatTilesModel;
    private List<BookSeatTilesModel.SeatList> seatsList;
   // private MarkerPlugin markerPlugin;
    private final List<ImageView> markers = new ArrayList<>();
  //  private TileView tileView;
    private BookSeatPostModel objBookSeatPostModel;
    private ProgressBar progressBar;
    private Bundle bundleValue;
    private ProcessGBModel mProcessGBModel;
    private GroupSeatModel mGroupSeatModel;
    private List<SeatModel> mSeatModels;
    private final List<String> mCubicleNo = new ArrayList<>();
    private AppCompatTextView mAppCompatTextViewSeats;
    private AppCompatTextView mAppCompatTextViewSeats1;
    private Boolean isAccept;

    private ExecutorService executor = Executors.newSingleThreadExecutor();
    Handler handler = new Handler(Looper.getMainLooper());


    private static EmptyCallback callback;

    public static void setCallback(EmptyCallback callback) {
        BookSeatTileViewActivity.callback = callback;
    }

    public static EmptyCallback getCallback() {
        return callback;
    }

    public static BookSeatTileViewActivity newInstance() {
        return new BookSeatTileViewActivity();
    }

    @Override
    protected void onDestroy() {
        deleteCache(BookSeatTileViewActivity.this);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        deleteCache(BookSeatTileViewActivity.this);
        super.onBackPressed();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //VA fix
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.fragment_book_seat_tile);
        SharedPreferences sharedPreferences = getSharedPreferences(StaticData.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        int count = sharedPreferences.getInt("POPUP_COUNT", 1);
        if (count < 4) {
            count++;
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("POPUP_COUNT", count);
            editor.apply();
            alertZoomPopup(this);
        }
        //should be hidden in single selection
        LinearLayoutCompat mSelectedLayout = findViewById(R.id.selected_layout);
        //tileView = findViewById(R.id.seatTileView);
        AppCompatTextView appCompatTextViewTitle = findViewById(R.id.toolbar_title);
        progressBar = findViewById(R.id.loadingProgressBar);
        AppCompatButton button = findViewById(R.id.buttonSubmit);
        button.setOnClickListener(v -> BookSeatTileViewActivity.this.createGroupBookObject());
        ImageView imageBack = findViewById(R.id.bookImageBack);
        mAppCompatTextViewSeats = findViewById(R.id.textSelected);
        mAppCompatTextViewSeats1 = findViewById(R.id.textSelect);
        imageBack.setOnClickListener(v -> BookSeatTileViewActivity.this.finish());
        objBookSeatPostModel = BookSeatFragment.getTilePostData();
        if (mBookGroupSeatFragment != null) {
            mProcessGBModel = BookGroupSeatFragment.getProcessData();
        }
        bundleValue = getIntent().getExtras();

        if (bundleValue != null && bundleValue.getString("view") != null) {
            if (Objects.requireNonNull(bundleValue.getString("view")).equalsIgnoreCase("")) {
                appCompatTextViewTitle.setText("Choose Seat");
                isAccept = bundleValue.getBoolean("isAccept");
                if (bundleValue.getBoolean("flag")) {
                    mAppCompatTextViewSeats.setVisibility(View.VISIBLE);
                    mAppCompatTextViewSeats1.setVisibility(View.VISIBLE);
                    mSelectedLayout.setVisibility(View.VISIBLE);
                    button.setVisibility(View.GONE);
                    if (getCallback() != null)
                        button.setVisibility(View.VISIBLE);
                    groupServiceCall();
                } else {
                    tileServiceCall();
                }
            } else {
                appCompatTextViewTitle.setText("View Seat");
                if (bundleValue.getBoolean("flag")) {
                    mSelectedLayout.setVisibility(View.VISIBLE);
                    button.setVisibility(View.GONE);
                    getSeatLayout(bundleValue.getString("view"), bundleValue.getString("emp"));
                } else {
                    tileViewServiceCall(bundleValue.getString("view"));
                }
            }
        }
    }

    private void tileServiceCall() {
        if (objBookSeatPostModel != null) {
            progressBar.setVisibility(View.VISIBLE);
            BookSeatTilesDataModel tileData = new BookSeatTilesDataModel();
            if (objBookSeatPostModel.getBookingDate() != null) {
                tileData.setBookingDate(objBookSeatPostModel.getBookingDate());
            } else {
                tileData.setBookingDate("");
            }
            tileData.setOdcflag(objBookSeatPostModel.getOdcFlag());
            tileData.setJoblevel7flag(objBookSeatPostModel.getJobLevel7Flag());
            tileData.setDuration(objBookSeatPostModel.getDuration());
            tileData.setFloor(objBookSeatPostModel.getFloor());
            tileData.setBuilding(objBookSeatPostModel.getBuilding());
            tileData.setDC(objBookSeatPostModel.getDC());
            tileData.setWing(objBookSeatPostModel.getWing());
            tileData.setCity(objBookSeatPostModel.getCity());
            tileData.setEnablementType(objBookSeatPostModel.getEnablementType());
            tileData.setEnablementTypeValue(objBookSeatPostModel.getEnablementTypeValue());
            tileData.setCubicleNo("");
            ServiceGenerator.createService(BookSeatService.class).getTileDetails(tileData).enqueue(new Callback<BookSeatTilesModel>() {
                @Override
                public void onResponse(@NotNull Call<BookSeatTilesModel> call, @NotNull Response<BookSeatTilesModel> response) {
                    if (response.isSuccessful() && response.code() == 200 && response.body() != null) {
                        bookSeatTilesModel = response.body();
                        if (bookSeatTilesModel.getImageSrc() != null)
                            downloadAndDisplay();
                    } else {
                        progressBar.setVisibility(View.GONE);
                        showErrorAlert(true, "", Constants.SERVICE_ERROR);
                    }
                }

                @Override
                public void onFailure(@NotNull Call<BookSeatTilesModel> call, @NotNull Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    Firebase.crashlyticsRecordException(t);
                    showErrorAlert(true, "", Constants.SERVICE_ERROR);
                }
            });
        }

    }

    private void tileViewServiceCall(String date) {
        progressBar.setVisibility(View.VISIBLE);
        ServiceGenerator.createService(BookSeatService.class).getViewSeatBookingDetails(date).enqueue(new Callback<BookSeatTilesModel>() {
            @Override
            public void onResponse(@NotNull Call<BookSeatTilesModel> call, @NotNull Response<BookSeatTilesModel> response) {
                if (response.isSuccessful() && response.code() == 200 && response.body() != null) {
                    bookSeatTilesModel = response.body();
                    if (bookSeatTilesModel.getImageSrc() != null)
                        downloadAndDisplay();
                }
            }

            @Override
            public void onFailure(@NotNull Call<BookSeatTilesModel> call, @NotNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                Firebase.crashlyticsRecordException(t);
                showErrorAlert(true, "", Constants.SERVICE_ERROR);
            }
        });

    }

    private void seatSubmit(String cubicleNo) {
        progressBar.setVisibility(View.VISIBLE);
        BookSeatTilePostData data = new BookSeatTilePostData();
        data.setBookingDate(objBookSeatPostModel.getBookingDate());
        data.setCubicleNo(cubicleNo);
        data.setDuration(objBookSeatPostModel.getDuration());
        data.setEnablementType(objBookSeatPostModel.getEnablementType());
        data.setEnablementTypeValue(objBookSeatPostModel.getEnablementTypeValue());

        ServiceGenerator.createService(BookSeatService.class).postSeatTileSubmit(isAccept, data).enqueue(new Callback<List<BookTilePostResponseModel>>() {
            @Override
            public void onResponse(@NotNull Call<List<BookTilePostResponseModel>> call, @NotNull Response<List<BookTilePostResponseModel>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.code() == 200 && response.body() != null && !response.body().isEmpty()) {
                    if (response.body().get(0).getType() != null && !response.body().get(0).getType().equalsIgnoreCase("failure") && response.body().get(0).getContent() != null) {
                        bookSeatFragment.showSuccessAlert(response.body().get(0).getContent(), false);
                        BookSeatTileViewActivity.this.finish();
                    }
                    else if (response.body().get(0).getType() != null && response.body().get(0).getType().equalsIgnoreCase("failure") && response.body().get(0).getContent() != null)
                        showErrorAlert(true, response.body().get(0).getType(), response.body().get(0).getContent());
                }
            }

            @Override
            public void onFailure(@NotNull Call<List<BookTilePostResponseModel>> call, @NotNull Throwable t) {
                progressBar.setVisibility(View.GONE);
                Firebase.crashlyticsRecordException(t);
                showErrorAlert(false, "", Constants.SERVICE_ERROR);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void downloadAndDisplay() {
        deleteCache(BookSeatTileViewActivity.this);
        File filepath = new File(getExternalCacheDir(), "bookImageTiles.zip");
        if (getExternalCacheDir() != null) {
            try (OutputStream outputStream = new FileOutputStream(filepath)) {
                setUpdatedData();
                byte[] bytes;
                if (bookSeatTilesModel != null) {
                    bytes = Base64.decode(bookSeatTilesModel.getImageSrc().getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
                } else {
                    bytes = Base64.decode(mGroupSeatModel.getImageSrc().getBytes(StandardCharsets.UTF_8), Base64.DEFAULT);
                }
                outputStream.write(bytes);
                outputStream.flush();
                executor.execute(() -> {
                    File directory = new File(getExternalCacheDir() + "/bookImageTiles");
                    directory.mkdirs();
                    ZipArchive.unzip(getExternalCacheDir().getAbsolutePath() + "/bookImageTiles.zip", directory.getAbsolutePath(), "");
                    filepath.delete();
                    directory.delete();
                    handler.post(() -> {
                        if (bookSeatTilesModel != null) {
                          /*  new TileView.Builder(tileView)
                                    .setSize(bookSeatTilesModel.getWidth(), bookSeatTilesModel.getHeight())
                                    .setStreamProvider(new StreamProviderFiles())
                                    .defineZoomLevel(getExternalCacheDir().getAbsolutePath() + "/bookImageTiles/" + "tile_%d_%d.png")
                                    .addReadyListener(this::onReady)
                                    .installPlugin(new MarkerPlugin(this))
                                    .build();*/
                        } else {
                            if (Objects.requireNonNull(bundleValue.getString("view")).equalsIgnoreCase("")) {
                                mAppCompatTextViewSeats.setText("Seats to be selected : " + (mProcessGBModel.getEmpDetails().size()));
                                mAppCompatTextViewSeats1.setText("Seats selected: " + mCubicleNo.size());
                            }
                           /* new TileView.Builder(tileView)
                                    .setSize(mGroupSeatModel.getWidth(), mGroupSeatModel.getHeight())
                                    .setStreamProvider(new StreamProviderFiles())
                                    .defineZoomLevel(getExternalCacheDir().getAbsolutePath() + "/bookImageTiles/" + "tile_%d_%d.png")
                                    .addReadyListener(this::onReady)
                                    .installPlugin(new MarkerPlugin(this))
                                    .build();*/
                        }
                        progressBar.setVisibility(View.GONE);
                    });
                });

            } catch (IOException e) {
                Firebase.crashlyticsRecordException(e);
            }
        }
    }

    public void deleteCache(Context context) {
        try {
            File dir = new File(context.getExternalCacheDir() + "/bookImageTiles");
            deleteDir(dir);
        } catch (Exception e) {
            Firebase.crashlyticsRecordException(e);
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            if (children != null) {
                for (String child : children) {
                    boolean success = deleteDir(new File(dir, child));
                    if (!success) {
                        return false;
                    }
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

  /*  private void onReady(TileView tileView) {
        scrollToFirstSeat();
        markerPlugin = tileView.getPlugin(MarkerPlugin.class);
        mapMarkersInView(seatsList, mSeatModels);
        markerPlugin.refreshPositions();
    }*/

    private void scrollToFirstSeat() {
     //   tileView.post(() -> tileView.scrollTo(0, 0));
    }

    private void mapMarkersInView(List<BookSeatTilesModel.SeatList> seats, List<SeatModel> seatModels) {

        if (seats != null) {
            for (BookSeatTilesModel.SeatList seat : seats) {
                ImageView marker = new ImageView(this);
                marker.setMinimumHeight(50);
                marker.setMinimumWidth(50);
                marker.setTag(seat.getSeatId());
                marker.setImageResource(R.drawable.tile_green_marker);
                marker.setOnClickListener(this::marckerClicked);
                markers.add(marker);
                //markerPlugin.addMarker(marker, seat.getSeatXcoordinate().intValue(), seat.getSeatYcoordinate().intValue(), -0.5f, -0.5f, 0, 0);

                if (seat.getSeatStatus().equalsIgnoreCase("Booked")) {
                    changeTint(marker, R.color.book_seat_red);
                } else if (seat.getSeatStatus().equalsIgnoreCase("Blocked")) {
                    changeTint(marker, R.color.book_seat_yellow);
                } else if (seat.getSeatStatus().equalsIgnoreCase("Selected"))
                    changeTint(marker, R.color.orange_type);

                if (!Objects.requireNonNull(bundleValue.getString("view")).equalsIgnoreCase("")) {
                    changeTint(marker, R.color.book_seat_red);
                }
            }
        } else {
            for (SeatModel seat : seatModels) {
                ImageView marker = new ImageView(this);
                marker.setTag(seat.getSeatId());
                marker.setImageResource(R.drawable.tile_green_marker);
                marker.setOnClickListener(this::marckerClicked);
                markers.add(marker);
                //markerPlugin.addMarker(marker, seat.getSeatXcoordinate(), seat.getSeatYcoordinate(), -0.5f, -0.5f, 0, 0);

                if (seat.getSeatStatus().equalsIgnoreCase("Booked")) {
                    changeTint(marker, R.color.book_seat_red);
                } else if (seat.getSeatStatus().equalsIgnoreCase("Blocked")) {
                    changeTint(marker, R.color.book_seat_yellow);
                } else if (seat.getSeatStatus().equalsIgnoreCase("Selected")) {
                    changeTint(marker, R.color.orange_type);
                }

                if (!Objects.requireNonNull(bundleValue.getString("view")).equalsIgnoreCase("")) {
                    changeTint(marker, R.color.book_seat_red);
                }
            }
        }
    }


    private void alertZoomPopup(Activity mContext) {
        if (mContext != null) {
            CardBookSeatZoomBindingImpl mBinding = DataBindingUtil.inflate(LayoutInflater.from(mContext),
                    R.layout.card_book_seat_zoom, null, false);
            android.app.AlertDialog.Builder alert = new android.app.AlertDialog.Builder(mContext);
            alert.setView(mBinding.getRoot());
            android.app.AlertDialog commentDialog = alert.create();
            if (commentDialog.getWindow() != null) {
                commentDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                commentDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            }
            commentDialog.show();
            commentDialog.setCancelable(false);
            mBinding.txtOk.setOnClickListener(view -> closePopUp(commentDialog));
            new Handler(Looper.getMainLooper()).postDelayed(() -> closePopUp(commentDialog), 3000);
        }
    }

    private void closePopUp(android.app.AlertDialog commentDialog) {
        if (!BookSeatTileViewActivity.this.isDestroyed() && !BookSeatTileViewActivity.this.isFinishing() && commentDialog != null && commentDialog.isShowing())
            commentDialog.dismiss();
    }


    @SuppressLint("SetTextI18n")
    private void marckerClicked(View v) {
        if (!(v instanceof ImageView))
            return;
        if (bookSeatTilesModel != null) {
            BookSeatTilesModel.SeatList seatDetails = getSeatDetails(v.getTag().toString());
            showPopup(seatDetails);
        } else {
            SeatModel seatModel = getGroupSeatDetails(v.getTag().toString());
            showPopup1(seatModel, (ImageView) v);
        }
    }

    private BookSeatTilesModel.SeatList getSeatDetails(String seatId) {
        BookSeatTilesModel.SeatList currentData = new BookSeatTilesModel.SeatList();
        for (BookSeatTilesModel.SeatList data : seatsList) {
            if (data.getSeatId().equalsIgnoreCase(seatId))
                currentData = data;
        }
        return currentData;
    }

    private void changeTint(ImageView imageView, int color) {
        imageView.setColorFilter(ContextCompat.getColor(this, color), android.graphics.PorterDuff.Mode.SRC_IN);
    }

    private void setUpdatedData() {
        if (bookSeatTilesModel != null) {
            seatsList = new ArrayList<>();
            for (BookSeatTilesModel.SeatList data : bookSeatTilesModel.getSeatList()) {
                BookSeatTilesModel.SeatList seat = new BookSeatTilesModel.SeatList();
                seat.setSeatXcoordinate(data.getSeatXcoordinate());
                seat.setSeatYcoordinate(data.getSeatYcoordinate());
                seat.setSeatStatus(data.getSeatStatus());
                seat.setSeatId(data.getSeatId());
                seat.setNumcubicleid(data.getNumcubicleid());
                seatsList.add(seat);
            }
        } else {
            mSeatModels = new ArrayList<>();
            for (SeatModel data : mGroupSeatModel.getSeatList()) {
                SeatModel seat = new SeatModel();
                seat.setSeatXcoordinate(data.getSeatXcoordinate());
                seat.setSeatYcoordinate(data.getSeatYcoordinate());
                seat.setSeatStatus(data.getSeatStatus());
                seat.setSeatId(data.getSeatId());
                seat.setNumcubicleid(data.getNumcubicleid());
                mSeatModels.add(seat);
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void showPopup(BookSeatTilesModel.SeatList seatDetails) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = getLayoutInflater();
        View customView = layoutInflater.inflate(R.layout.book_seat_details_popup, null);

        TextView heading = customView.findViewById(R.id.heading);
        TextView cubicleId = customView.findViewById(R.id.cubicleId);
        TextView availability = customView.findViewById(R.id.availability);
        TextView submitText = customView.findViewById(R.id.add_text);
        TextView cancelText = customView.findViewById(R.id.cancel_text);
        heading.setText("Seat Details");
        cubicleId.setText(seatDetails.getSeatId());
        availability.setText(String.format("Seat: %s", seatDetails.getSeatStatus()));

        cancelText.setVisibility(View.GONE);
        submitText.setText("OK");
        if (seatDetails.getSeatStatus().equalsIgnoreCase("available")) {
            cancelText.setVisibility(View.VISIBLE);
            submitText.setText("Book Seat");
            cancelText.setText("Cancel");
        }

        AlertDialog declarationDialog = builder.create();
        declarationDialog.setView(customView);
        submitText.setOnClickListener(v -> {
            if (declarationDialog.isShowing())
                declarationDialog.dismiss();
            if (submitText.getText().toString().equalsIgnoreCase("book seat"))
                seatSubmit(seatDetails.getNumcubicleid());
        });
        cancelText.setOnClickListener(v -> {
            if (declarationDialog.isShowing())
                declarationDialog.dismiss();
        });
        declarationDialog.show();
    }

    private void showErrorAlert(boolean value, String title, String serviceError) {
        try {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            if (!title.isEmpty())
                alertDialogBuilder.setTitle(title);
            alertDialogBuilder.setMessage(serviceError);
            alertDialogBuilder.setPositiveButton(android.R.string.yes, (dialog, which) -> {
                dialog.dismiss();
                if (value)
                    BookSeatTileViewActivity.this.finish();
            });
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.show();
        } catch (Exception e) {
            Firebase.crashlyticsRecordException(e);
        }
    }

    private void groupServiceCall() {
        progressBar.setVisibility(View.VISIBLE);
        if (mProcessGBModel != null) {
            ProcessGBModel processGBModel = new ProcessGBModel();
            if (mProcessGBModel.getBookingDate() != null) {
                processGBModel.setBookingDate(mProcessGBModel.getBookingDate());
            } else {
                processGBModel.setBookingDate("");
            }
            processGBModel.setBuilding(mProcessGBModel.getBuilding());
            processGBModel.setCity(mProcessGBModel.getCity());
            processGBModel.setCubicleNo(mProcessGBModel.getCubicleNo());
            processGBModel.setDc(mProcessGBModel.getDc());
            processGBModel.setDuration(mProcessGBModel.getDuration());
            processGBModel.setEmpDetails(mProcessGBModel.getEmpDetails());
            processGBModel.setFloor(mProcessGBModel.getFloor());
            processGBModel.setOdcflag(mProcessGBModel.getOdcflag());
            processGBModel.setWing(mProcessGBModel.getWing());
            ServiceGenerator.createService(BookSeatService.class).getAvailableSeatCoordinatesforGB(processGBModel).enqueue(new CustomRetrofitCallback<GroupSeatModel>() {
                @Override
                public void onResponse(Response<GroupSeatModel> response) {
                    if (response.isSuccessful() && response.code() == 200 && response.body() != null) {
                        mGroupSeatModel = response.body();
                        if (mGroupSeatModel.getImageSrc() != null)
                            downloadAndDisplay();
                    } else {
                        progressBar.setVisibility(View.GONE);
                        showErrorAlert(true, "", Constants.SERVICE_ERROR);
                    }
                }

                @Override
                public void onFailure(String message) {
                    progressBar.setVisibility(View.GONE);
                    showErrorAlert(true, "", Constants.SERVICE_ERROR);
                }
            });
        } else {
            showErrorAlert(true, "", Constants.SERVICE_ERROR);
        }
    }

    private SeatModel getGroupSeatDetails(String seatId) {
        SeatModel currentData = new SeatModel();
        for (SeatModel data : mSeatModels) {
            if (data.getSeatId().equalsIgnoreCase(seatId))
                currentData = data;
        }
        return currentData;
    }

    @SuppressLint("SetTextI18n")
    private void showPopup1(SeatModel seatDetails, ImageView imageView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater layoutInflater = getLayoutInflater();
        View customView = layoutInflater.inflate(R.layout.book_seat_details_popup, null);

        TextView heading = customView.findViewById(R.id.heading);
        TextView cubicleId = customView.findViewById(R.id.cubicleId);
        TextView availability = customView.findViewById(R.id.availability);
        TextView cancelText = customView.findViewById(R.id.cancel_text);
        TextView submitText = customView.findViewById(R.id.add_text);

        heading.setText("SEAT DETAILS");
        cubicleId.setText(seatDetails.getSeatId());
        availability.setText(String.format("Seat: %s", seatDetails.getSeatStatus()));

        cancelText.setVisibility(View.VISIBLE);
        if (seatDetails.getSeatStatus().equalsIgnoreCase("available")) {
            if (mCubicleNo.size() > mProcessGBModel.getEmpDetails().size() - 1) {
                showErrorAlert(false, "", "You can maximum select " + mProcessGBModel.getEmpDetails().size() + " seats at a time");
                return;
            }
            cancelText.setText("CANCEL");
            submitText.setText("ADD");
        } else if (seatDetails.getSeatStatus().equalsIgnoreCase("selected")) {
            cancelText.setText("CANCEL");
            submitText.setText("REMOVE");
        } else {
            cancelText.setVisibility(View.GONE);
            submitText.setText("OK");
        }

        AlertDialog declarationDialog = builder.create();
        declarationDialog.setView(customView);
        submitText.setOnClickListener(v -> {
            if (submitText.getText().toString().equalsIgnoreCase("add")) {
                seatDetails.setSeatStatus("Selected");
                mCubicleNo.add(seatDetails.getNumcubicleid());
                mAppCompatTextViewSeats1.setText("Seats selected: " + mCubicleNo.size());
                changeTint(imageView, R.color.orange_type);
            } else if (submitText.getText().toString().equalsIgnoreCase("remove")) {
                seatDetails.setSeatStatus("Available");
                mCubicleNo.remove(seatDetails.getNumcubicleid());
                mAppCompatTextViewSeats1.setText("Seats selected: " + mCubicleNo.size());
                changeTint(imageView, R.color.book_seat_selected);
            }
            declarationDialog.dismiss();
        });

        cancelText.setOnClickListener(v -> declarationDialog.dismiss());
        declarationDialog.show();
    }

    private void createGroupBookObject() {
        if (bundleValue.getString("view") != null && bundleValue.getString("view").equalsIgnoreCase("")) {
            if (mCubicleNo.size() == mProcessGBModel.getEmpDetails().size() && mProcessGBModel != null) {
                ProcessGBModel processGBModel = new ProcessGBModel();
                processGBModel.setBookingDate(mProcessGBModel.getBookingDate());
                processGBModel.setBuilding(mProcessGBModel.getBuilding());
                processGBModel.setCity(mProcessGBModel.getCity());
                processGBModel.setCubicleNo(mCubicleNo);
                processGBModel.setDc(mProcessGBModel.getDc());
                processGBModel.setDuration(mProcessGBModel.getDuration());
                processGBModel.setEmpDetails(mProcessGBModel.getEmpDetails());
                processGBModel.setFloor(mProcessGBModel.getFloor());
                processGBModel.setOdcflag(mProcessGBModel.getOdcflag());
                processGBModel.setWing(mProcessGBModel.getWing());
                bookSystemAllocatedSeat(processGBModel);
            } else {
                showErrorAlert(false, "", "You seem to have missed selecting seats for " + (mProcessGBModel.getEmpDetails().size() - mCubicleNo.size()) + " remaining colleagues");
            }
        } else {
            bookSeatFragment.refresh(true);
            BookSeatTileViewActivity.this.finish();
        }
    }

    private void bookSystemAllocatedSeat(ProcessGBModel processGBModel) {
        Common.loaderLaunch(BookSeatTileViewActivity.this.getSupportFragmentManager());
        try {
            ServiceGenerator.createService(BookSeatService.class).processGroupBooking(processGBModel).enqueue(new CustomRetrofitCallback<GroupBookingModel>() {
                @Override
                public void onResponse(Response<GroupBookingModel> response) {
                    Common.dismissLoader();
                    if (response.isSuccessful() && response.code() == 200 && response.body() != null) {
                        GroupBookingModel groupBookingModel = response.body();
                        if (groupBookingModel.getGroupBookingMessage() != null && groupBookingModel.getGroupBookingStatus() != null) {
                            showSuccessDetails(groupBookingModel);
                        } else
                            showErrorAlert(false, "", groupBookingModel.getGroupBookingMessage());
                    } else {
                        showErrorAlert(false, "", com.infosys.infyme.ui.constants.Constants.API_FAILURE);
                    }
                }

                @Override
                public void onFailure(String message) {
                    Common.dismissLoader();
                    showErrorAlert(false, "", com.infosys.infyme.ui.constants.Constants.API_FAILURE);
                }
            });
        } catch (Exception e) {
            Firebase.crashlyticsRecordException(e);
            Common.dismissLoader();
            showErrorAlert(false, "", com.infosys.infyme.ui.constants.Constants.API_FAILURE);
        }
    }

    private void showSuccessDetails(GroupBookingModel groupBookingModel) {
        try {
            if (!groupBookingModel.getGroupBookingStatus().equalsIgnoreCase("failure"))
                showSuccessAlert(groupBookingModel.getGroupBookingMessage());
            else {
                showErrorAlert(false, "", groupBookingModel.getGroupBookingMessage());
            }
        } catch (Exception e) {
            Firebase.crashlyticsRecordException(e);
        }
    }

    private void getSeatLayout(String view, String emp) {
        progressBar.setVisibility(View.VISIBLE);
        ServiceGenerator.createService(BookSeatService.class).getSeatLayoutForGBEmployee(view, emp).enqueue(new CustomRetrofitCallback<GroupSeatModel>() {
            @Override
            public void onResponse(Response<GroupSeatModel> response) {
                if (response.isSuccessful() && response.code() == 200 && response.body() != null) {
                    mGroupSeatModel = response.body();
                    if (mGroupSeatModel.getImageSrc() != null)
                        downloadAndDisplay();
                } else {
                    progressBar.setVisibility(View.GONE);
                    showErrorAlert(true, "", Constants.SERVICE_ERROR);
                }
            }

            @Override
            public void onFailure(String message) {
                progressBar.setVisibility(View.GONE);
                showErrorAlert(true, "", Constants.SERVICE_ERROR);
            }
        });
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void showSuccessAlert(String message) {
        try {
            PreferenceManager.getDefaultSharedPreferences(bookSeatFragment.getContext()).edit().putString("RECENT-BOOK-SEAT", "TRUE").apply();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater layoutInflater = getLayoutInflater();
            View customView = layoutInflater.inflate(R.layout.book_seat_custom_dialog, null);
            WebView webView = customView.findViewById(R.id.contentDescription);
            AppCompatButton appCompatButton = customView.findViewById(R.id.okButton);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.loadDataWithBaseURL("", message, "text/html", "UTF-8", "");
            AlertDialog declarationDialog = builder.create();
            declarationDialog.setView(customView);
            appCompatButton.setOnClickListener(v -> {
                declarationDialog.dismiss();
                if (bookSeatFragment != null)
                    bookSeatFragment.refresh(true);
                BookSeatTileViewActivity.this.finish();
                if (getCallback() != null)
                    getCallback().callBack();
            });
            declarationDialog.show();
        } catch (Exception e) {
            Firebase.crashlyticsRecordException(e);
        }
    }
}
