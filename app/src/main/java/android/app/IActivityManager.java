package android.app;

// IActivityManager.java

import android.content.res.Configuration;
import android.os.RemoteException;

/**
 * @author Sodino E-mail:sodinoopen@hotmail.com
 * @version Time：2011-7-10 上午11:37:46
 */
public interface IActivityManager {
    Configuration getConfiguration() throws RemoteException;

    void updateConfiguration(Configuration paramConfiguration)
            throws RemoteException;
}