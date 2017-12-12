package guitartutorandanalyser.guitartutor;



import android.test.AndroidTestCase;

import org.junit.Test;


public class DatabaseHelperTest extends AndroidTestCase {



    @Test
    public void checkColumns() throws Exception {
        assertNotNull(DatabaseHelper.Column.COMPLETED);
        assertNotNull(DatabaseHelper.Column.TYPE);
        assertNotNull(DatabaseHelper.Column.NAME);
        assertNotNull(DatabaseHelper.Column.ID);
        assertNotNull(DatabaseHelper.Column.TABID);
        assertNotNull(DatabaseHelper.Column.SONGID);
        assertNotNull(DatabaseHelper.Column.RECORDPOINT);
        assertNotNull(DatabaseHelper.Column.RECORDDATE);
    }


    @Test
    public void checkTable() {
        assertEquals("homeworks",DatabaseHelper.TABLE_HOMEWORKS);
    }

}