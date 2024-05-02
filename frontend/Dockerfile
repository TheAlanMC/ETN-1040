FROM --platform=linux/x86_64 nginx:1.17-alpine
#FROM nginx:1.17-alpine

EXPOSE 80

VOLUME /tmp

ARG DIST_DIR=dist/frontend/browser
COPY ${DIST_DIR} /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf


