package uoc.master.angel.dressme.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

/**
 * Created by angel on 12/04/2017.
 */

public class PermissionsUtil {

    public final int PERMISSION_ALL = 1;

    /**
     * Devuelve verdadero si todos los permisos que recibe en el array están concedidos
     * @param context
     * @param permissions
     * @return
     */
    public static boolean hasPermissions(Context context, String[] permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }
}
