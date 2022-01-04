package fr.ans.psc.toggle.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Getter
@Setter
public class ToggleReport {

    private int alreadyToggled;
    private int successful;
    private int failed;
    private int submitted;

    public void setReportCounters(Map<String, TogglePsRef> psRefMap) {
        submitted = psRefMap.size();
        alreadyToggled = (int) psRefMap.values().stream().filter(psRef -> psRef.getReturnStatus() == HttpStatus.CONFLICT.value()).count();
        successful = (int) psRefMap.values().stream().filter(psRef -> psRef.getReturnStatus() == HttpStatus.OK.value()).count();
        failed = submitted - (alreadyToggled + successful);
    }

    public String generateReportSummary() {
        return String.format("Opérations de bascule terminées.\n\n" +
                        "%s PsRefs soumis à bascule.\n" +
                        "%s PsRefs déjà basculés.\n" +
                        "%s PsRefs basculés avec succès.\n" +
                        "%s PsRefs n'ont pas pu être basculés.\n\n" +
                        "Vous trouverez la liste des opérations en pièce jointe",
                submitted, alreadyToggled, successful, failed);
    }
}
