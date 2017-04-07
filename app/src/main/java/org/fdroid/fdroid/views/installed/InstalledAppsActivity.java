/*
 * Copyright (C) 2010-12  Ciaran Gultnieks, ciaran@ciarang.com
 * Copyright (C) 2009  Roberto Jacinto, roberto.jacinto@caixamagica.pt
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.fdroid.fdroid.views.installed;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;

import org.fdroid.fdroid.FDroidApp;
import org.fdroid.fdroid.R;
import org.fdroid.fdroid.data.App;
import org.fdroid.fdroid.data.AppProvider;
import org.fdroid.fdroid.data.Schema;
import org.fdroid.fdroid.views.apps.AppListItemController;

public class InstalledAppsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private InstalledAppListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ((FDroidApp) getApplication()).applyTheme(this);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.installed_apps_layout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.installed_apps__activity_title));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        adapter = new InstalledAppListAdapter(this);

        RecyclerView appList = (RecyclerView) findViewById(R.id.app_list);
        appList.setHasFixedSize(true);
        appList.setLayoutManager(new LinearLayoutManager(this));
        appList.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Starts a new or restarts an existing Loader in this manager
        getSupportLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                this,
                AppProvider.getInstalledUri(),
                Schema.AppMetadataTable.Cols.ALL,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        adapter.setApps(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.setApps(null);
    }

    static class InstalledAppListAdapter extends RecyclerView.Adapter<AppListItemController> {

        private final Activity activity;

        @Nullable
        private Cursor cursor;

        InstalledAppListAdapter(Activity activity) {
            this.activity = activity;
            setHasStableIds(true);
        }

        @Override
        public long getItemId(int position) {
            if (cursor == null) {
                return 0;
            }

            cursor.moveToPosition(position);
            return cursor.getLong(cursor.getColumnIndex(Schema.AppMetadataTable.Cols.ROW_ID));
        }

        @Override
        public AppListItemController onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = activity.getLayoutInflater().inflate(R.layout.installed_app_list_item, parent, false);
            return new AppListItemController(activity, view);
        }

        @Override
        public void onBindViewHolder(AppListItemController holder, int position) {
            if (cursor == null) {
                return;
            }

            cursor.moveToPosition(position);
            holder.bindModel(new App(cursor));
        }

        @Override
        public int getItemCount() {
            return cursor == null ? 0 : cursor.getCount();
        }

        public void setApps(@Nullable Cursor cursor) {
            this.cursor = cursor;
            notifyDataSetChanged();
        }
    }
}