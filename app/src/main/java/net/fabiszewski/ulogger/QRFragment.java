/*
 * Copyright (c) 2019 Bartek Fabiszewski
 * http://www.fabiszewski.net
 *
 * This file is part of μlogger-android.
 * Licensed under GPL, either version 3, or any later.
 * See <http://www.gnu.org/licenses/>
 */

package net.fabiszewski.ulogger;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.concurrent.ExecutorService;

import static android.app.Activity.RESULT_OK;
import static android.content.Intent.EXTRA_LOCAL_ONLY;
import static android.content.Intent.EXTRA_MIME_TYPES;
import static android.content.Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
import static java.util.concurrent.Executors.newCachedThreadPool;

import com.budiyev.android.codescanner.AutoFocusMode;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.ScanMode;

public class QRFragment extends Fragment implements LoggerTask.LoggerTaskCallback, ImageTask.ImageTaskCallback {

    private TextView locationNotFoundTextView;
    private CodeScanner codeScanner;
    private View generalView;

    private static final int MY_CAMERA_REQUEST_CODE = 100;

    public QRFragment() {
    }

    static QRFragment newInstance() {
        return new QRFragment();
    }

    @Override
    public void onImageTaskCompleted(@NonNull Uri uri, @NonNull Bitmap thumbnail) {

    }

    @Override
    public void onImageTaskFailure(@NonNull String error) {

    }

    @Override
    public void onLoggerTaskCompleted(Location location) {

    }

    @Override
    public void onLoggerTaskFailure(int reason) {

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions( //Method of Fragment
                    new String[]{Manifest.permission.CAMERA},
                    MY_CAMERA_REQUEST_CODE
            );
        } else {
            Log.e("CAMERA", "PERMISSION GRANTED");
        }
        return inflater.inflate(R.layout.fragment_scan_barcode_from_camera, container, false);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (permissions[0].equals(Manifest.permission.CAMERA)
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.e("CAMERA","camera permission granted");
            } else {
                Log.e("CAMERA", "camera permission denied");
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        generalView = view;
        initScanner();
        initFlashButton(view);

    }


    private void initScanner(){
        CodeScannerView scannerView = generalView.findViewById(R.id.scanner_view);
        codeScanner = new CodeScanner(generalView.getContext(), scannerView);
        codeScanner.setCamera(CodeScanner.CAMERA_BACK);
        codeScanner.setAutoFocusMode(AutoFocusMode.CONTINUOUS);
        codeScanner.setFormats(CodeScanner.ALL_FORMATS);
        codeScanner.setScanMode(ScanMode.SINGLE);
        codeScanner.setAutoFocusEnabled(true);
        //codeScanner.isFlashEnabled()//todo:add
        codeScanner.setTouchFocusEnabled(false);
        //codeScanner.setDecodeCallback(this::handleScannedBarcode); //todo: add
        //codeScanner.setErrorCallback(this::showError); //todo: add
    }

    private void initFlashButton(View view) {

        FrameLayout flash_container = view.findViewById(R.id.layout_flash_container);
        flash_container.setOnClickListener(this::toggleFlash);

    }

    private void toggleFlash(View view){
        ImageView flash = generalView.findViewById(R.id.image_view_flash);
        flash.setActivated(!flash.isActivated());
        codeScanner.setFlashEnabled(!codeScanner.isFlashEnabled());
    }

}
