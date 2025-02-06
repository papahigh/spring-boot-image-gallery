FROM node:23-alpine AS build

WORKDIR /build
COPY . .

RUN npm ci
RUN npm run build

FROM nginx:1.27.4-alpine AS serve

COPY --from=build /build/dist/ /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf

EXPOSE 3001

CMD ["nginx", "-g", "daemon off;"]
