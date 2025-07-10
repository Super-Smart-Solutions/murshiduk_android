import { useQuery } from "@tanstack/react-query";
import { fetchPlants } from "../api/plantApi";

export function usePlants(page = 1, pageSize = 50) {
  return useQuery({
    queryKey: ["plants", page, pageSize],
    queryFn: () => fetchPlants(page, pageSize),
    staleTime: 3600_000,   // 1 h cache
  });
}
