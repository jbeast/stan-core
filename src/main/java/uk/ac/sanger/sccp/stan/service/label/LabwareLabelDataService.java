package uk.ac.sanger.sccp.stan.service.label;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.sanger.sccp.stan.model.*;
import uk.ac.sanger.sccp.stan.repo.PlanActionRepo;
import uk.ac.sanger.sccp.stan.service.label.LabwareLabelData.LabelContent;

import java.util.Comparator;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * @author dr6
 */
@Service
public class LabwareLabelDataService {
    private final PlanActionRepo planActionRepo;

    @Autowired
    public LabwareLabelDataService(PlanActionRepo planActionRepo) {
        this.planActionRepo = planActionRepo;
    }

    public LabwareLabelData getLabelData(Labware labware) {
        List<LabelContent> content = labware.getSlots().stream()
                .flatMap(slot -> slot.getSamples().stream())
                .map(this::getContent)
                .collect(toList());
        if (content.isEmpty()) {
            content = getPlannedContent(labware);
        }
        return new LabwareLabelData(labware.getBarcode(), content);
    }

    public LabelContent getContent(Sample sample) {
        Tissue tissue = sample.getTissue();
        return new LabelContent(tissue.getDonor().getDonorName(),
                getTissueDesc(tissue), tissue.getReplicate(), sample.getSection());
    }

    public String getTissueDesc(Tissue tissue) {
        SpatialLocation sl = tissue.getSpatialLocation();
        return prefix(tissue.getDonor().getLifeStage()) + sl.getTissueType().getCode() + "-" + sl.getCode();
    }

    public String prefix(LifeStage lifeStage) {
        switch (lifeStage) {
            case fetal: return "F";
            case paediatric: return "P";
            default: return "";
        }
    }

    public List<LabelContent> getPlannedContent(Labware labware) {
        List<PlanAction> planActions = planActionRepo.findAllByDestinationLabwareId(labware.getId());
        return planActions.stream()
                .sorted(Comparator.comparing((PlanAction ac) -> ac.getDestination().getAddress())
                        .thenComparing(PlanAction::getId))
                .map(this::getContent)
                .collect(toList());
    }

    public LabelContent getContent(PlanAction planAction) {
        Integer section = planAction.getNewSection();
        if (section==null) {
            section = planAction.getSample().getSection();
        }
        return new LabelContent(
                planAction.getSample().getTissue().getDonor().getDonorName(),
                getTissueDesc(planAction.getSample().getTissue()),
                planAction.getSample().getTissue().getReplicate(),
                section
        );
    }
}
