package uk.ac.sanger.sccp.stan.service.label;

import com.google.common.base.MoreObjects;

import java.util.List;
import java.util.Objects;

/**
 * A collection of information that may be printed onto a labware label.
 * @author dr6
 */
public class LabwareLabelData {
    private final String barcode;
    private final List<LabelContent> contents;

    public LabwareLabelData(String barcode, List<LabelContent> contents) {
        this.barcode = barcode;
        this.contents = List.copyOf(contents);
    }

    public String getBarcode() {
        return this.barcode;
    }

    public List<LabelContent> getContents() {
        return this.contents;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LabwareLabelData that = (LabwareLabelData) o;
        return (Objects.equals(this.barcode, that.barcode)
                && Objects.equals(this.contents, that.contents));
    }

    @Override
    public int hashCode() {
        return Objects.hash(barcode, contents);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("barcode", barcode)
                .add("contents", contents)
                .toString();
    }

    public static class LabelContent {
        private final String donorName;
        private final String blockDesc;
        private final Integer replicate;
        private final Integer section;

        public LabelContent(String donorName, String blockDesc, Integer replicate, Integer section) {
            this.donorName = donorName;
            this.blockDesc = blockDesc;
            this.replicate = replicate;
            this.section = section;
        }

        public String getDonorName() {
            return this.donorName;
        }

        public String getBlockDesc() {
            return this.blockDesc;
        }

        public Integer getReplicate() {
            return this.replicate;
        }

        public Integer getSection() {
            return this.section;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            LabelContent that = (LabelContent) o;
            return (Objects.equals(this.donorName, that.donorName)
                    && Objects.equals(this.blockDesc, that.blockDesc)
                    && Objects.equals(this.replicate, that.replicate)
                    && Objects.equals(this.section, that.section));
        }

        @Override
        public int hashCode() {
            return Objects.hash(donorName, blockDesc, replicate, section);
        }

        @Override
        public String toString() {
            return MoreObjects.toStringHelper(this)
                    .add("donorName", donorName)
                    .add("blockDesc", blockDesc)
                    .add("replicate", replicate)
                    .add("section", section)
                    .toString();
        }
    }
}
