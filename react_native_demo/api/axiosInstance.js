import axios from "axios";
import * as SecureStore from "expo-secure-store";

export const axiosInstance = axios.create({
  baseURL: process.env.EXPO_PUBLIC_API_URL || "https://staging.plant-backend.ss-solution.org",
  timeout: 15_000,
});

// Attach JWT + optional API-key to every request
axiosInstance.interceptors.request.use(async cfg => {
  const token = await SecureStore.getItemAsync("jwt");        // or AsyncStorage
  if (token) cfg.headers.Authorization = `Bearer ${token}`;
  if (process.env.EXPO_PUBLIC_API_KEY)
    cfg.headers["X-API-KEY"] = process.env.EXPO_PUBLIC_API_KEY;
  return cfg;
});
