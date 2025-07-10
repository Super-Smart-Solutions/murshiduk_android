import { axiosInstance } from "./axiosInstance";

const PLANT_ENDPOINT = process.env.EXPO_PUBLIC_PLANT_ENDPOINT || "/plants";

/** Paginated list (defaults: page 1, 10 items) */
export const fetchPlants = async (page = 1, pageSize = 10) =>
  (await axiosInstance.get(PLANT_ENDPOINT, {
    params: { pageNumber: page, pageSize },
  })).data;

/** Single plant by id */
export const fetchPlantById = async id =>
  (await axiosInstance.get(`${PLANT_ENDPOINT}/${id}`)).data;

/** Search plant by name */
export const fetchPlantByName = async name =>
  (await axiosInstance.get(PLANT_ENDPOINT, { params: { name } })).data;
