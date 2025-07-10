import { axiosInstance } from "./axiosInstance";

const DISEASE_ENDPOINT =
  process.env.EXPO_PUBLIC_DISEASE_ENDPOINT || "/diseases";

/** Get one disease by its  id */
export const fetchDiseaseById = async id =>
  (await axiosInstance.get(`${DISEASE_ENDPOINT}/${id}`)).data;

export const fetchDiseaseByName = async name =>
  (await axiosInstance.get(DISEASE_ENDPOINT, { params: { name } })).data;
