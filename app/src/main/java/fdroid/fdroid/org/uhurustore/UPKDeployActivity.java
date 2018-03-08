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

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

public class UPKDeployActivity extends Activity {

    private static final String LOG_TAG = "uhuru-store";

    //adb shell am start -a android.intent.action.MAIN -n
    // com.XXX.xxx/com.XXX.xxx.Main --es STRING_PAR_NAME stringParameterValue

    // am start -a android.intent.action.MAIN -n org.fdroid.fdroid/org.fdroid.fdroid.UPKDeployActivity --es UPKfilePath An.stop-1.5.upk
    // --es UPKfilePAth PATHUPK

    @SuppressLint("SdCardPath")
    public void onStart() {

        super.onStart();

        String UPKfilePath = "";
        String Token = "";
        boolean isFromMDM = false;
        Log.v(LOG_TAG,"UPKDeploy Activity launched -- from MDM");
        if(getIntent().getAction() != null && getIntent().getAction().equals("android.intent.action.MAIN")) {

            UPKfilePath = getIntent().getStringExtra("UPKfilePath");
            Token  = getIntent().getStringExtra("Token");
            isFromMDM = getIntent().getExtras().getBoolean("isFromMDM");

            UpkbackgroundTask task = new UpkbackgroundTask(this, this, UPKfilePath, isFromMDM, Token);
            task.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    public void onCreate() {}
}
