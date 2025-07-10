import { useQuery } from "@tanstack/react-query";
import { detectDisease, visualizeInference } from "../api/inferenceApi";
import { fetchDiseaseById } from "../api/diseaseApi";          // ✅ correct import


/**
 * Wraps disease detection & attention map.
 *
 * @param {string|null} inferenceId
 * @param {function(string)} onVisualizationReady  callback(uri)
 */
export function usePostDetection(inferenceId, onVisualizationReady) {
  // Detect disease
  const {
    data: prediction,
    isLoading: predicting,
    error: predictionError,
  } = useQuery({
    queryKey: ["diseasePrediction", inferenceId],
    queryFn: () => detectDisease(inferenceId),
    enabled: !!inferenceId,
    retry: false,
    staleTime: Infinity,
  });

  // Disease meta
  const {
    data: disease,
    isLoading: loadingDisease,
  } = useQuery({
    queryKey: ["diseaseDetails", prediction?.disease_id],
    queryFn: () => fetchDiseaseById(prediction.disease_id),
    enabled: !!prediction?.disease_id,
    staleTime: 3600_000,
    retry: false,
  });

  // Attention map
  const {
    data: viz,
    isLoading: visualizing,
  } = useQuery({
    queryKey: ["visualization", inferenceId],
    queryFn: async () => {
      const res = await visualizeInference(inferenceId);
      if (res) setTimeout(() =>
        onVisualizationReady?.(res.attention_map_url), 2000);
      return res;
    },
    enabled: !!inferenceId && !!prediction?.disease_id,
    staleTime: 3600_000,
    retry: false,
  });

  return {
    prediction,
    disease,
    visualizationUrl: viz?.attention_map_url,
    isHealthy: prediction?.status === 2 && !prediction?.disease_id,
    confidence: prediction?.confidence_level ? prediction.confidence_level * 100 : null,
    isLoading: predicting || loadingDisease || visualizing,
    error: predictionError,
  };
}
