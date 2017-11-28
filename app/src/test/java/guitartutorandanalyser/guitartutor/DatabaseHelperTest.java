package guitartutorandanalyser.guitartutor;

import android.app.Activity;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;
import android.support.annotation.StringDef;
import android.support.annotation.StyleRes;
import android.util.Log;
import android.view.Display;


import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;

public class DatabaseHelperTest {


    DatabaseHelper dbh;

    @Mock
    Looper looper = Looper.getMainLooper();


    Activity activity;

    @Before
    public void setUp() throws Exception {

        MockitoAnnotations.initMocks(this);

        activity = Robolectric.setupActivity(GuitarTutorMain.class);
        dbh = new DatabaseHelper(activity);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void createDataBase() throws Exception {

    }

    @Test
    public void checkDataBase() throws Exception {


    }

    @Test
    public void openDataBase() throws Exception {

    }

    @Test
    public void close() throws Exception {

    }

    @Test
    public void updateDatabaseRecord() throws Exception {

    }

    @Test
    public void fetchHomeworkById() throws Exception {

    }

    @Test
    public void onCreate() throws Exception {

    }

    @Test
    public void onUpgrade() throws Exception {

    }

    @Test
    public void reachColumns() {
        Assert.assertNotNull(DatabaseHelper.Column.COMPLETED);
        Assert.assertNotNull(DatabaseHelper.Column.BEATS);
        Assert.assertNotNull(DatabaseHelper.Column.BPM);
        Assert.assertNotNull(DatabaseHelper.Column.ID);
        Assert.assertNotNull(DatabaseHelper.Column.MAP);
        Assert.assertNotNull(DatabaseHelper.Column.NAME);
        Assert.assertNotNull(DatabaseHelper.Column.RECORDDATE);
        Assert.assertNotNull(DatabaseHelper.Column.RECORDPOINT);
        Assert.assertNotNull(DatabaseHelper.Column.SONGID);
        Assert.assertNotNull(DatabaseHelper.Column.TABID);
        Assert.assertNotNull(DatabaseHelper.Column.TYPE);

    }

}