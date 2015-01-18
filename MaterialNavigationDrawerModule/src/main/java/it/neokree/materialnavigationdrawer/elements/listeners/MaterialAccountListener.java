package it.neokree.materialnavigationdrawer.elements.listeners;

import it.neokree.materialnavigationdrawer.elements.MaterialAccount;

/**
 * Created by neokree on 11/12/14.
 */
public interface MaterialAccountListener {

    public void onAccountOpening(MaterialAccount account);

    public void onChangeAccount(MaterialAccount newAccount);

}
