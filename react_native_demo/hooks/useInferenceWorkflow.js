import { useEffect, useState } from "react";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { startInference, validateInference } from "../api/inferenceApi";

export function useInferenceWorkflow(imageId) {
  const qc = useQueryClient();
  const [inferenceId, setInferenceId] = useState(null);
  const [isFinal, setIsFinal] = useState(false);

  const start = useMutation({
    mutationFn: startInference,
    onSuccess: d => {
      setInferenceId(d.id);
      validate.mutate(d.id);
    },
  });

  const validate = useMutation({
    mutationFn: validateInference,
    onSuccess: d => setIsFinal(d.status === 1),
  });

  useEffect(() => {
    if (imageId && !inferenceId) start.mutate(imageId);
  }, [imageId]);

  const reset = () => {
    qc.invalidateQueries({ queryKey: ["inference"] });
    setInferenceId(null);
    setIsFinal(false);
  };

  return {
    inferenceId,
    isFinal,
    isLoading: start.isPending || validate.isPending,
    error: start.error || validate.error,
    reset,
  };
}
