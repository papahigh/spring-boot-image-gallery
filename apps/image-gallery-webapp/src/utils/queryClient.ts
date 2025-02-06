import { QueryClient, queryOptions, useMutation } from '@tanstack/react-query';
import { fetchImageDetails, fetchImageSummaries, PageRequest, uploadImage } from './axiosClient';

export const queryClient = new QueryClient();

export const imagesQueryOptions = (request: PageRequest) =>
  queryOptions({
    queryKey: ['gallery', request],
    queryFn: () => fetchImageSummaries(request),
  });

export const imageQueryOptions = (imageId: string) =>
  queryOptions({
    queryKey: ['gallery', imageId],
    queryFn: () => fetchImageDetails(imageId),
  });

export const useImageUpload = () => {
  return useMutation({
    mutationKey: ['gallery', 'upload'],
    mutationFn: uploadImage,
    onSuccess: () => queryClient.invalidateQueries(),
  });
};
