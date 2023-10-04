package com.example.clearcache;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private TextView clearedCacheText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button clearCacheButton = findViewById(R.id.clear_cache_button);
        clearedCacheText = findViewById(R.id.cleared_cache_text);

        clearCacheButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long clearedCacheSize = clearAppCache();
                String formattedSize = formatSize(clearedCacheSize);
                clearedCacheText.setText("Cleared cache size: " + formattedSize);
            }
        });
    }

    private long clearAppCache() {
        long clearedSize = 0;
        try {
            clearedSize += deleteDir(this.getCacheDir());
            clearedSize += deleteDir(this.getExternalCacheDir());
            clearedSize += clearOtherAppCaches();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return clearedSize;
    }

    private long deleteDir(File dir) {
        long clearedSize = 0;
        if (dir != null && dir.isDirectory()) {
            File[] children = dir.listFiles();
            for (File child : children) {
                clearedSize += child.length();
                clearedSize += deleteDir(child);
            }
        }
        if (dir != null) {
            dir.delete();
        }
        return clearedSize;
    }

    private String formatSize(long sizeInBytes) {
        DecimalFormat df = new DecimalFormat("#.##");
        double sizeInMB = (double) sizeInBytes / (1024 * 1024);
        return df.format(sizeInMB) + " MB";
    }

    private long clearOtherAppCaches() {
        long clearedSize = 0;
        PackageManager pm = getPackageManager();
        String[] packages = pm.getPackagesForUid(android.os.Process.myUid());
        if (packages != null) {
            for (String packageName : packages) {
                clearedSize += clearPackageCache(packageName);
            }
        }
        return clearedSize;
    }

    private long clearPackageCache(String packageName) {
        long clearedSize = 0;
        Context context;
        try {
            context = createPackageContext(packageName, 0);
            clearedSize += deleteDir(context.getCacheDir());
            clearedSize += deleteDir(context.getExternalCacheDir());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return clearedSize;
    }
}
