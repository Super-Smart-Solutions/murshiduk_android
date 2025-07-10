import { axiosInstance } from "./axiosInstance";
const INFERENCE_ENDPOINT = "/inferences";

export const getInferences = async ({ size = 10, page = 1 }) =>
  (await axiosInstance.get(INFERENCE_ENDPOINT, {
    params: { pageSize: size, pageNumber: page },
  })).data;

export const startInference = async imageId =>
  (await axiosInstance.post(`${INFERENCE_ENDPOINT}?image_id=${imageId}`)).data;

export const validateInference = async id =>
  (await axiosInstance.post(`${INFERENCE_ENDPOINT}/${id}/validate`)).data;

export const detectDisease = async id =>
  (await axiosInstance.post(`${INFERENCE_ENDPOINT}/${id}/detect`)).data;

export const visualizeInference = async id =>
  (await axiosInstance.post(`${INFERENCE_ENDPOINT}/${id}/attention`)).data;

export const uploadImage = async ({ name, plantId, imageFile }) => {
  const form = new FormData();
  form.append("name", name);
  form.append("farm_id", "1");            // static for now
  form.append("plant_id", plantId);
  form.append("annotated", "false");
  form.append("image_file", imageFile);   // { uri, name, type }
  return (await axiosInstance.post("/uploads", form, {
    headers: { "Content-Type": "multipart/form-data" },
  })).data;
};
