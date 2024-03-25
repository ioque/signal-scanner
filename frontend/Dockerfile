FROM node:17-bullseye AS builder

WORKDIR /app
COPY package.json package-lock.json ./
COPY . .

RUN npm install
RUN npm run build


FROM nginx:alpine

WORKDIR /app

COPY --from=builder ./app/build/ .
COPY --from=builder ./app/nginx.conf /nginx.conf.template

CMD ["/bin/sh" , "-c" , "envsubst '$API_HTTP_MODE,$API_HOST' < /nginx.conf.template > /etc/nginx/nginx.conf && exec nginx -g 'daemon off;'"]
EXPOSE 4200
