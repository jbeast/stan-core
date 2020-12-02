package uk.ac.sanger.sccp.stan.service.label.print;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.sanger.sccp.stan.model.*;
import uk.ac.sanger.sccp.stan.repo.LabwareRepo;
import uk.ac.sanger.sccp.stan.service.label.*;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

/**
 * @author dr6
 */
@Service
public class LabelPrintService {
    private final LabwareLabelDataService labwareLabelDataService;
    private final SprintClient sprintClient;
    private final LabwareRepo labwareRepo;

    @Autowired
    public LabelPrintService(LabwareLabelDataService labwareLabelDataService, SprintClient sprintClient,
                             LabwareRepo labwareRepo) {
        this.labwareLabelDataService = labwareLabelDataService;
        this.sprintClient = sprintClient;
        this.labwareRepo = labwareRepo;
    }

    public void printLabwareBarcodes(User user, String printerName, List<String> barcodes) throws IOException {
        if (barcodes.isEmpty()) {
            throw new IllegalArgumentException("No labware barcodes supplied to print.");
        }
        List<Labware> labware = labwareRepo.getByBarcodeIn(barcodes);
        printLabware(user, printerName, labware);
    }

    public void printLabware(User user, String printerName, List<Labware> labware) throws IOException {
        if (labware.isEmpty()) {
            throw new IllegalArgumentException("No labware supplied to print.");
        }
        List<LabwareLabelData> labelData = labware.stream()
                .map(labwareLabelDataService::getLabelData)
                .collect(toList());
        Set<LabelType> labelTypes = labware.stream()
                .map(lw -> lw.getLabwareType().getLabelType())
                .collect(toSet());
        if (labelTypes.contains(null)) {
            throw new IllegalArgumentException("Cannot print label for labware without a label type.");
        }
        if (labelTypes.size() > 1) {
            throw new IllegalArgumentException("Cannot perform a print request incorporating multiple different label types.");
        }
        LabelType labelType = labelTypes.iterator().next();
        LabelPrintRequest request = new LabelPrintRequest(labelType, labelData);
        print(printerName, request);
        recordPrint(user, labware);
    }

    public void print(String printerName, LabelPrintRequest request) throws IOException {
        sprintClient.print(printerName, request);
    }

    @Transactional
    public void recordPrint(User user, List<Labware> labware) {
        // TODO
    }
}
