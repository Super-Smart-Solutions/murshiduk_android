
package com.saatco.murshadik.ui;

import com.saatco.murshadik.models.Disease;

public abstract class PestIdentificationState {

    public static final class Input extends PestIdentificationState {}

    public static final class Loading extends PestIdentificationState {
        public final String message;
        public Loading(String message) {
            this.message = message;
        }
    }

    public static final class Success extends PestIdentificationState {
        public final Disease disease;
        public final double confidence;
        public final String attentionMapUrl;

        public Success(Disease disease, double confidence, String attentionMapUrl) {
            this.disease = disease;
            this.confidence = confidence;
            this.attentionMapUrl = attentionMapUrl;
        }
    }

    public static final class Inconclusive extends PestIdentificationState {}

    public static final class Error extends PestIdentificationState {
        public final String message;
        public Error(String message) {
            this.message = message;
        }
    }
}