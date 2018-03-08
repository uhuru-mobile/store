/*
 * Copyright (C) 2014 Hamon Valentin <vhamon@et.esiea-ouest.fr>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 */
package fdroid.fdroid.org.uhurustore;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.app.Notification;

@SuppressLint("NewApi")
public class UpkbackgroundTask extends Thread {

    private static final String LOG_TAG = "uhuru-store";
    private String mFilePath = "";
    private Context mCtx = null;
    private int REQUEST_INSTALL = 0;
    private boolean mIsFromMDM = false;
    private int mTokenId = -1;
    private String repoaddress = "";
    private Activity mActivity;

    public UpkbackgroundTask(Context xCtx, Activity activity, String apkPath, boolean isfromMDMAgent, String Token) {

        mIsFromMDM = isfromMDMAgent;
        mFilePath = apkPath;
        mCtx = xCtx;
        mActivity = activity;

        Log.i(LOG_TAG, "New UPKbackgroundTask");

        if(mIsFromMDM) {
            // Permet d'éviter d'eventuelles injections -- On veut s'assurer que c'est bien un entier
            mTokenId = Integer.parseInt(Token);
            Log.i(LOG_TAG, "Token : "+ mTokenId);
        }

        Log.i(LOG_TAG, "FilePath : "+ mFilePath);
    }

    public void run() {
        installApk(new File(mFilePath), mActivity);
        mActivity = null;
        return;
    }

    private void installApk(File file, Activity activity) {

        // Récupération du lien de téléchargement du ZIP de la ROM pour une MAJ Système
        // Non utilisé par l'installeur si c'est une installation d'APK
        AppDetailsCtrl appdetctrl = new AppDetailsCtrl(mCtx);
        try {
            Log.d(LOG_TAG, "Select APK from: " + file.getName());
            appdetctrl.selectAPK(file.getName());
        } catch (Exception ex) {
            Log.e(LOG_TAG, "Failed to select APK: " + ex.getMessage());
        }
        repoaddress = appdetctrl.getRepo();

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_INSTALL_PACKAGE);
        intent.setDataAndType(Uri.parse("file://" + file.getPath()),
                              "application/vnd.android.package-archive");
        intent.putExtra("isFromMDM", mIsFromMDM);
        intent.putExtra("UPKFilePath", file.getPath());
        intent.putExtra("repoaddress", repoaddress);
        intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);

        if(mIsFromMDM) {
            intent.putExtra("token_id", mTokenId);
        }
        activity.startActivityForResult(intent, REQUEST_INSTALL);

        if(!mIsFromMDM) {
            ((FDroidApp) mCtx).invalidateApps();
        }
    }
}
