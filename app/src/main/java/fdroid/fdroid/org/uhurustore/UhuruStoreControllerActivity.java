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
import android.content.Intent;
import android.content.Context;
import java.util.Vector;

public class UhuruStoreControllerActivity extends Activity {

    private static final String LOG_TAG = "uhuru-store-controller";
    public static Context mCtx = null;

    public void onStart() {

        super.onStart();
        mCtx = this;

        String RepoAddr = "";
        String Token = "";
        String Message = "";
        String MarketKeyName = "";
        String SSLPublicKey = null;
        Intent returnIntent = null;

        Log.v(LOG_TAG,"UhuruStoreControllerActivity launched ");

        if(getIntent().getAction() != null && getIntent().getAction().equals("uhurustore.intent.action.AddRepo")) {

            RepoAddr = getIntent().getStringExtra("RepoAddr");
            Token  = getIntent().getStringExtra("Token");
            MarketKeyName = getIntent().getStringExtra("MarketKeyName");
            SSLPublicKey = getIntent().getStringExtra("SSLPublicKey");

            Log.v(LOG_TAG,"RepoAddr = " + RepoAddr );
            Log.v(LOG_TAG,"MarketKeyName = " + MarketKeyName );
            Log.v(LOG_TAG,"Token = " + Token);
            Log.v(LOG_TAG,"SSLPublicKey = " + SSLPublicKey);

            if(SSLPublicKey.equals("")) {
                SSLPublicKey = null;
            }

            try {
                DB db = DB.getDB();
                db.addRepo(RepoAddr, 10, SSLPublicKey, true);
            } finally {
                DB.releaseDB();
            }

            Log.d(LOG_TAG, "Repo successfully added. Sending report back...");

            // Envoi du rapport au MDM en repassant par le PackageInstaller
            Message = "OK - New market added for " + RepoAddr + " : " + MarketKeyName + ".";
            returnIntent = new Intent("uhuruinstaller.intent.action.ReportError");
            returnIntent.putExtra("Token", Token);
            returnIntent.putExtra("Message", Message);
            startActivity(returnIntent);

            Log.d(LOG_TAG, "Finishing UhuruStoreControllerActivity...");
            finish();
        } else if(getIntent().getAction() != null && getIntent().getAction().equals("uhurustore.intent.action.RmRepo")) {

            RepoAddr = getIntent().getStringExtra("RepoAddr");
            Token  = getIntent().getStringExtra("Token");

            Log.v(LOG_TAG,"RepoAddr = " + RepoAddr );
            Log.v(LOG_TAG,"Token = " + Token);

            // TODO: Tester l'existence de l'adresse dans la DB. Puis envoyer un message d'erreur si l'adresse n'est pas dans la liste.
            final Vector<String> rem_lst = new Vector<String>();
            rem_lst.add(RepoAddr);
            try {
                DB db = DB.getDB();
                db.removeServers(rem_lst);
            } finally {
                DB.releaseDB();
            }

            Log.d(LOG_TAG, "Repo successfully removed. Sending report back...");

            // Envoi du rapport au MDM en repassant par le PackageInstaller
            Message = "OK - Market " + RepoAddr + " has been successfully removed.";
            returnIntent = new Intent("uhuruinstaller.intent.action.ReportError");
            returnIntent.putExtra("Token", Token);
            returnIntent.putExtra("Message", Message);
            startActivity(returnIntent);

            Log.d(LOG_TAG, "Finishing UhuruStoreControllerActivity...");
            finish();
        }
    }

    public void onCreate() {}
}
