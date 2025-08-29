FROM node:16.15.1-alpine3.15

MAINTAINER tntech

WORKDIR /app


COPY deploy/ /app/

ENV NODE_VERSION=16.15.1


RUN yarn

CMD ["yarn", "start"]

EXPOSE 3000
