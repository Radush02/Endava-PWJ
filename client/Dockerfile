FROM node:18-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .

ARG API_URL
RUN npm run build && \
    find dist/client/browser -type f -name "*.js" -exec sed -i "s|__API_URL__|${API_URL}|g" {} +

FROM nginx:alpine
COPY --from=build /app/dist/client/browser /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf

RUN apk add --no-cache openssl && \
    mkdir -p /etc/nginx/certs && \
    openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
    -keyout /etc/nginx/certs/key.pem \
    -out /etc/nginx/certs/cert.pem \
    -subj "/CN=localhost"

EXPOSE 80 443
CMD ["nginx", "-g", "daemon off;"]