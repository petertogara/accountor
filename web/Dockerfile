FROM ubuntu:latest
LABEL authors="petertogara"

# Stage 1: Build the React application
FROM node:20-alpine AS build-stage

WORKDIR /app

COPY package*.json ./

# Install dependencies, allowing for older peer dependency resolution to avoid ERESOLVE conflicts
RUN npm install --legacy-peer-deps

COPY . .

RUN npm run build

# ---
# Stage 2: Serve the application with Nginx
FROM nginx:1.21.4-alpine AS production-stage

# Remove default Nginx configuration
RUN rm /etc/nginx/conf.d/default.conf

COPY nginx.conf /etc/nginx/conf.d/default.conf

COPY --from=build-stage /app/dist /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]