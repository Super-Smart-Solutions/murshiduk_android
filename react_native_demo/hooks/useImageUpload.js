import { useState } from "react";
import { useMutation } from "@tanstack/react-query";
import * as ImagePicker from "expo-image-picker";
import { uploadImage } from "../api/inferenceApi";

export function useImageUpload(plant) {
  const [previewUri, setPreviewUri] = useState(null);

  const mutation = useMutation({
    mutationFn: uploadImage,
    onSuccess: data => {
      // data.id is the `image_id`
      mutation.meta?.onUploaded?.(data.id, previewUri);
    },
  });

  const pick = async () => {
    const res = await ImagePicker.launchImageLibraryAsync({
      mediaTypes: ImagePicker.MediaTypeOptions.Images,
      quality: 0.9,
    });
    if (res.canceled) return;

    const file = res.assets[0];      // { uri, fileName, mimeType }
    setPreviewUri(file.uri);

    const name = `uploads/${plant.label.toLowerCase()}/${Date.now()}_${file.fileName ?? "image.jpg"}`;

    mutation.mutate({
      name,
      plantId: plant.value,
      imageFile: {
        uri: file.uri,
        name: file.fileName ?? "image.jpg",
        type: file.mimeType ?? "image/jpeg",
      },
    }, { meta: { onUploaded: mutation.meta?.onUploaded }});
  };

  return {
    pickImage: pick,
    imageId: mutation.data?.id ?? null,
    previewUri,
    isUploading: mutation.isPending,
    error: mutation.error,
  };
}
