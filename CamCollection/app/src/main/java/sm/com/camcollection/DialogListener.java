package sm.com.camcollection;

import android.os.Bundle;

public interface DialogListener {
    public static final int ADD_RECORD_SUCCESS = 100;
    public static final int UPDATE_METADATA = 101;
    public static final int DELETE_ALL = 102;
    public static final String ENTITY_KEY = "entity_key";
    void onfinisheditdialog(int resultCode, Bundle data);
}
