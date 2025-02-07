# spring-boot-image-gallery

This project delivers a framework for image processing
and application demonstrating its usage.

## Features

- Image Classification 
- Image Matadata extraction
- General image manipulation using ImageMagick

## Build docker images

```shell
skaffold build -p dev
```

## Run locally 


```shell
kubectl kustomize --enable-helm manifests/env | kubectl apply -f -
```

```shell
skaffold run -p dev
```