/* eslint-disable */

// @ts-nocheck

// noinspection JSUnusedGlobalSymbols

// This file was automatically generated by TanStack Router.
// You should NOT make any changes in this file as it will be overwritten.
// Additionally, you should also exclude this file from your linter and/or formatter to prevent it from being checked or modified.

// Import Routes

import { Route as rootRoute } from './routes/__root'
import { Route as ViewImport } from './routes/view'
import { Route as UploadImport } from './routes/upload'
import { Route as IndexImport } from './routes/index'
import { Route as ViewImageIdImport } from './routes/view.$imageId'

// Create/Update Routes

const ViewRoute = ViewImport.update({
  id: '/view',
  path: '/view',
  getParentRoute: () => rootRoute,
} as any)

const UploadRoute = UploadImport.update({
  id: '/upload',
  path: '/upload',
  getParentRoute: () => rootRoute,
} as any)

const IndexRoute = IndexImport.update({
  id: '/',
  path: '/',
  getParentRoute: () => rootRoute,
} as any)

const ViewImageIdRoute = ViewImageIdImport.update({
  id: '/$imageId',
  path: '/$imageId',
  getParentRoute: () => ViewRoute,
} as any)

// Populate the FileRoutesByPath interface

declare module '@tanstack/react-router' {
  interface FileRoutesByPath {
    '/': {
      id: '/'
      path: '/'
      fullPath: '/'
      preLoaderRoute: typeof IndexImport
      parentRoute: typeof rootRoute
    }
    '/upload': {
      id: '/upload'
      path: '/upload'
      fullPath: '/upload'
      preLoaderRoute: typeof UploadImport
      parentRoute: typeof rootRoute
    }
    '/view': {
      id: '/view'
      path: '/view'
      fullPath: '/view'
      preLoaderRoute: typeof ViewImport
      parentRoute: typeof rootRoute
    }
    '/view/$imageId': {
      id: '/view/$imageId'
      path: '/$imageId'
      fullPath: '/view/$imageId'
      preLoaderRoute: typeof ViewImageIdImport
      parentRoute: typeof ViewImport
    }
  }
}

// Create and export the route tree

interface ViewRouteChildren {
  ViewImageIdRoute: typeof ViewImageIdRoute
}

const ViewRouteChildren: ViewRouteChildren = {
  ViewImageIdRoute: ViewImageIdRoute,
}

const ViewRouteWithChildren = ViewRoute._addFileChildren(ViewRouteChildren)

export interface FileRoutesByFullPath {
  '/': typeof IndexRoute
  '/upload': typeof UploadRoute
  '/view': typeof ViewRouteWithChildren
  '/view/$imageId': typeof ViewImageIdRoute
}

export interface FileRoutesByTo {
  '/': typeof IndexRoute
  '/upload': typeof UploadRoute
  '/view': typeof ViewRouteWithChildren
  '/view/$imageId': typeof ViewImageIdRoute
}

export interface FileRoutesById {
  __root__: typeof rootRoute
  '/': typeof IndexRoute
  '/upload': typeof UploadRoute
  '/view': typeof ViewRouteWithChildren
  '/view/$imageId': typeof ViewImageIdRoute
}

export interface FileRouteTypes {
  fileRoutesByFullPath: FileRoutesByFullPath
  fullPaths: '/' | '/upload' | '/view' | '/view/$imageId'
  fileRoutesByTo: FileRoutesByTo
  to: '/' | '/upload' | '/view' | '/view/$imageId'
  id: '__root__' | '/' | '/upload' | '/view' | '/view/$imageId'
  fileRoutesById: FileRoutesById
}

export interface RootRouteChildren {
  IndexRoute: typeof IndexRoute
  UploadRoute: typeof UploadRoute
  ViewRoute: typeof ViewRouteWithChildren
}

const rootRouteChildren: RootRouteChildren = {
  IndexRoute: IndexRoute,
  UploadRoute: UploadRoute,
  ViewRoute: ViewRouteWithChildren,
}

export const routeTree = rootRoute
  ._addFileChildren(rootRouteChildren)
  ._addFileTypes<FileRouteTypes>()

/* ROUTE_MANIFEST_START
{
  "routes": {
    "__root__": {
      "filePath": "__root.tsx",
      "children": [
        "/",
        "/upload",
        "/view"
      ]
    },
    "/": {
      "filePath": "index.tsx"
    },
    "/upload": {
      "filePath": "upload.tsx"
    },
    "/view": {
      "filePath": "view.tsx",
      "children": [
        "/view/$imageId"
      ]
    },
    "/view/$imageId": {
      "filePath": "view.$imageId.tsx",
      "parent": "/view"
    }
  }
}
ROUTE_MANIFEST_END */
