package com.example.sber_ai.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServerResponse {
    @JsonProperty("megadetector_predict")
    private MegadetectorPredict megadetectorPredict;

    @JsonProperty("species_predict")
    private SpeciesPredict speciesPredict;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ServerResponse response = (ServerResponse) o;
        return Objects.equals(response.megadetectorPredict, megadetectorPredict) && Objects.equals(response.speciesPredict, speciesPredict);
    }

    @Override
    public int hashCode() {
        return Objects.hash(megadetectorPredict, speciesPredict);
    }

    @Override
    public String toString() {
        return "Server response{" +
                "megadetector_predict=" + megadetectorPredict +
                ", species_predict=" + speciesPredict +
                '}';
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MegadetectorPredict {
        private List<String> labels;
        private List<Double> scores;
        private List<List<Integer>> bboxes;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MegadetectorPredict that = (MegadetectorPredict) o;
            return Objects.equals(labels, that.labels) &&
                    Objects.equals(scores, that.scores) &&
                    Objects.equals(bboxes, that.bboxes);
        }

        @Override
        public int hashCode() {
            return Objects.hash(labels, scores, bboxes);
        }

        @Override
        public String toString() {
            return "MegadetectorPredict{" +
                    "labels=" + labels +
                    ", scores=" + scores +
                    ", bboxes=" + bboxes +
                    '}';
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SpeciesPredict {
        private List<String> labels;
        private List<Double> scores;
        private List<List<Integer>> bboxes;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            SpeciesPredict that = (SpeciesPredict) o;
            return Objects.equals(labels, that.labels) &&
                    Objects.equals(scores, that.scores) &&
                    Objects.equals(bboxes, that.bboxes);
        }

        @Override
        public int hashCode() {
            return Objects.hash(labels, scores, bboxes);
        }

        @Override
        public String toString() {
            return "SpeciesPredict {\n" +
                    "labels=" + labels +
                    ",\n scores=" + scores +
                    ",\n bboxes=\n" + bboxes +
                    '}';
        }
    }

}