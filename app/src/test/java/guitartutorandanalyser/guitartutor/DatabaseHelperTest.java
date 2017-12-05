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
import android.test.AndroidTestCase;
import android.test.mock.MockContext;
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
import static org.mockito.Mockito.mock;

public class DatabaseHelperTest extends AndroidTestCase {


    DatabaseHelper dbh;




    @Before
    public void setUp() throws Exception {

        //MockitoAnnotations.initMocks(this);
/*
        Context context = mock(MockContext.class);
        Activity a = Mockito.mock(GuitarTutorMain.class);
       // activity = Robolectric.setupActivity(GuitarTutorMain.class);
        dbh = new DatabaseHelper(getContext());*/
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void createDataBase() throws Exception {

    }


    @Test
    public void reachColumns() {


    }

}