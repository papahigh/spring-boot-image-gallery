import axios, { Response } from 'redaxios';

import type { ImageDetails, ImageSummary } from '~/types';

export interface PageRequest {
  page: number;
  size: number;
}

export interface PageResponse<T> {
  content: T[];
  page: Page;
}

export interface Page {
  size: number;
  number: number;
  totalElements: number;
  totalPages: number;
}

const BASE_URL = 'http://localhost:8080';

const axiosClient = axios.create({
  baseURL: BASE_URL,
  responseType: 'json',
  withCredentials: false,
});

export async function fetchImageSummaries(params: PageRequest) {
  const { data } = await axiosClient.get<PageResponse<ImageSummary>>('/gallery', { params });
  return data;
}

export async function fetchImageDetails(imageId: string) {
  const { data } = await axiosClient.get<ImageDetails>(`/gallery/${imageId}`);
  return data;
}

export async function uploadImage(formData: FormData) {
  const { data } = await axiosClient.post<ImageDetails>('/upload', formData);
  return data;
}

export async function downloadImage(image: ImageDetails) {
  const link = document.createElement('a');
  link.href = `${BASE_URL}/download/${image.originalFile.blobPath}`;
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
}

export function isStatusResponse(val: unknown): val is Response<unknown> {
  return !!(val && typeof val === 'object' && 'status' in val);
}

export function isNotFoundError(val: unknown) {
  return isStatusResponse(val) && val.status === 404;
}
