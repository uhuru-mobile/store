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


import android.content.Context;
import android.util.Log;

import java.util.Vector;

// Controller class for AppDetails
// Permet de rendre possible le déploiement d'applications par le MDM.
public class AppDetailsCtrl {

    private static final String LOG_TAG = "uhuru-store";

    Downloader download;
    private Context mCtx;
    DB.Apk curapk;
    String repoaddress;

    // Constructor
    public AppDetailsCtrl(Context xCtx) {
        mCtx = xCtx;
    }

    // Method used to get the Apk Object which corresponds to the package name in parameter
    public boolean selectAPK(String packagename) {
        boolean res = false;
        try {
            DB db = DB.getDB();
            Vector<DB.App> appList = db.getApps(false);
            Integer count = 0;
            Integer total = appList.size();
            for (DB.App app : appList) {
                DB.Apk apk = app.getCurrentVersion();

                // if the package is found
                if(apk.apkName.compareTo(packagename) == 0) {
                    Log.i(LOG_TAG, "Found application :"+ packagename);
                    curapk = apk;
                    res = true;
                    break;
                }
            }
        } catch (Exception ex) {
            res = false;
            Log.e(LOG_TAG, "Failed to found APK - " + ex.getMessage());
        } finally {
            DB.releaseDB();
        }

        return res;
    }

    // Method used to get the repo address from DB
    public String getRepo() {

        String ra = null;
        try {
            DB db = DB.getDB();
            DB.Repo repo = db.getRepo(curapk.repo);

            if (repo != null) {
                Log.i(LOG_TAG, "We got a repo addr :" + repo.address);
                ra = repo.address;
            } else {
                Log.e(LOG_TAG, "Failed to get a repo addr");
            }
        } catch (Exception ex) {
            Log.e(LOG_TAG, "Failed to get repo address - " + ex.getMessage());
        } finally {
            Log.d(LOG_TAG, "DbRelease");
            DB.releaseDB();
        }

        return ra;
    }

    // Launching the download of the apk.
    public void download(DB.Apk apk, String repoaddress) {
        download = new Downloader(apk, repoaddress);
        download.start();
    }
}
