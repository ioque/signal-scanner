FROM node:17-bullseye AS builder

WORKDIR /app
COPY package.json package-lock.json ./
COPY . .

ARG REACT_APP_API_URL
ENV REACT_APP_API_URL $REACT_APP_API_URL

RUN npm install
RUN npm run build


FROM nginx:alpine

WORKDIR /app

COPY --from=builder ./app/build/ .
COPY --from=builder ./app/nginx.conf /etc/nginx/nginx.conf

CMD ["/bin/sh" , "-c", "exec nginx -g 'daemon off;'"]
EXPOSE 4200
