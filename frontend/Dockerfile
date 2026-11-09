FROM node:14

RUN sed -i -e 's|deb.debian.org|archive.debian.org|g' \
           -e 's|security.debian.org|archive.debian.org|g' \
           -e '/buster-updates/d' /etc/apt/sources.list \
    && apt-get -o Acquire::Check-Valid-Until=false update \
    && apt-get install -y python

RUN mkdir /var/www
RUN chmod o+w /var/www
COPY . /var/www/.

WORKDIR /var/www
RUN npm install nodemon -g --unsafe-perm
RUN npm install --quiet

ENTRYPOINT npm run dev:watch
