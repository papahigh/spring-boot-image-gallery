// Generated using typescript-generator version 3.2.1263 on 2025-02-06 17:54:10.

export interface Blob {
  id: string;
  createdAt: Date;
  updatedAt: Date;
  contentType: string;
  fileName: string;
  fileSize: number;
  blobPath: string;
  externalUrl: string;
}

export interface Classification {
  id: string;
  className: string;
  probability: number;
}

export interface ImageBundle {
  avif: string | null;
  jpeg: string | null;
  webp: string | null;
}

export interface ImageDetails {
  id: string;
  fileName: string;
  location: Location | null;
  createdAt: Date;
  updatedAt: Date;
  thumbnail: ImageBundle;
  fullSize: ImageBundle;
  originalFile: Blob;
  metadata: MetaTag[];
  classes: Classification[];
}

export interface ImageSummary {
  id: string;
  fileName: string;
  location: Location | null;
  createdAt: Date;
  updatedAt: Date;
  thumbnail: ImageBundle;
}

export interface Location {
  lat: number | null;
  lon: number | null;
  timestamp: Date | null;
}

export interface MetaTag {
  id: string;
  directory: string;
  tagName: string;
  tagValue: string;
}
