FROM node:17-bullseye AS builder

WORKDIR /app
COPY package.json package-lock.json ./
COPY . .

ARG API_HOST
ENV API_HOST $API_HOST

RUN npm run build


FROM nginx:alpine
WORKDIR /app
COPY --from=builder ./app/build/ .
COPY --from=builder ./app/nginx.conf /nginx.conf.template

ARG API_HOST
ENV API_HOST $API_HOST

CMD ["/bin/sh" , "-c" , "envsubst '$HOST,$MODE' < /nginx.conf.template > /etc/nginx/nginx.conf && exec nginx -g 'daemon off;'"]
EXPOSE 4200
