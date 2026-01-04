# syntax=docker/dockerfile:1

FROM node:lts-alpine AS build

WORKDIR /app

# do not use "COPY . ." as it can ruin the build cache by including a lot of
# irrelevant files that we may edit.
 
COPY src src
COPY assets assets
COPY public public
COPY *.json .
COPY *.js .
COPY *.ts .
COPY *.html .
COPY .env.production .

RUN --mount=type=cache,target=/root/.npm npm ci
RUN npm run build

FROM nginx:alpine AS final

COPY --from=build /app/dist /usr/share/nginx/html
COPY ./nginx.conf /etc/nginx/nginx.conf
COPY --chmod=755 ./start-nginx.sh /usr/local/bin/

ENTRYPOINT [ "start-nginx.sh" ]