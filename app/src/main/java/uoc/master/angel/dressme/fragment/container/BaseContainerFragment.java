package uoc.master.angel.dressme.fragment.container;



import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import uoc.master.angel.dressme.R;

/**
 * Created by angel on 06/04/2017.
 * Esta clase es necesaria para poder hacer transiciones entre fragmentos en cada pestaña
 * del FragmentTasbHost. FragmentTabHost no proporciona un layout en cada pestaña sobre
 * el que establecer las transacciones de fragmentos, solo tiene uno general y, si se utiliza
 * con las transacciones, mezcla los contenidos de todas las pestañas al visualizarlo.
 * Lo que se hará será cargar en cada pestaña un fragmento que extienda esta clase y que
 * inicialmente cargue el layout generico de container_fragment.xml. Tras ello, hará la transaccion
 * al fragmento con contenido real. Estos fragmentos podrán realizar, entoces transacciones
 * sobre container_fragment, recurriendo a los métodos heredados de esta clase, mediante el
 * método getParentFragment()
 *
 */
public class BaseContainerFragment extends Fragment {

    public void replaceFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.replace(R.id.container_framelayout, fragment);
        transaction.commit();
        getChildFragmentManager().executePendingTransactions();
    }

    public boolean popFragment() {
       // Log.e("test", "pop fragment: " + getChildFragmentManager().getBackStackEntryCount());
        boolean isPop = false;
        if (getChildFragmentManager().getBackStackEntryCount() > 0) {
            isPop = true;
            getChildFragmentManager().popBackStack();
        }
        return isPop;
    }

}
