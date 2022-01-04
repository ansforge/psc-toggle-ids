package fr.ans.psc.toggle.model;

import fr.ans.psc.model.PsRef;

public class TogglePsRef extends PsRef {

    private int returnStatus = 100;

    public TogglePsRef(String[] items, PsIdType originIdType, PsIdType targetIdType) {
        super();
        setNationalIdRef(originIdType.value + items[0]);
        setNationalId(targetIdType.value + items[1]);
    }

    public int getReturnStatus() {
        return returnStatus;
    }

    public void setReturnStatus(int returnStatus) {
        this.returnStatus = returnStatus;
    }
}
