package com.afollestad.udpdiscoverysample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.udpdiscovery.Discovery;
import com.afollestad.udpdiscovery.Entity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MainActivity extends AppCompatActivity {

    private Unbinder unbinder;
    private MainAdapter adapter;

    @BindView(R.id.list) RecyclerView list;
    @BindView(R.id.empty) TextView empty;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        unbinder = ButterKnife.bind(this);
        adapter = new MainAdapter(empty);

        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(adapter);
    }

    @Override protected void onResume() {
        super.onResume();
        adapter.clear();
        Discovery.instance(this)
                .discover(entity -> adapter.add(entity),
                        this::handleError)
                .respond(this::shouldRespondToRequest, this::handleError);
    }

    private boolean shouldRespondToRequest(Entity entity) {
        // You could have custom logic here. Return false to ignore discovery request.
        return true;
    }

    private void handleError(Throwable t) {
        new MaterialDialog.Builder(this)
                .title(R.string.error)
                .content(t.getMessage())
                .positiveText(android.R.string.ok)
                .show();
    }

    @Override protected void onPause() {
        super.onPause();
        Discovery.destroy();

        if (isFinishing()) {
            unbinder.unbind();
            unbinder = null;
            adapter = null;
        }
    }

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.refresh) {
            adapter.clear();
            Discovery.instance(this).refresh();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
