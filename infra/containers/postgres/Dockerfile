FROM debian:11-slim
RUN apt-get update
RUN apt-get -y install wget locales locales-all
RUN wget https://repo.postgrespro.ru/std-15/keys/pgpro-repo-add.sh
RUN sh pgpro-repo-add.sh
RUN apt-get install -y postgrespro-std-15-server

USER root
RUN /opt/pgpro/std-15/bin/pg-setup initdb --locale=ru_RU.UTF-8 --lc-collate=en_US.UTF-8
ENV PGDATA=/var/lib/pgpro/std-15/data
ENV APP=/opt/pgpro/std-15/bin
RUN sed -i 's/shared_buffers = [[:digit:]]*/shared_buffers = 1024/' $PGDATA/postgresql.conf
RUN echo 'host all all 0.0.0.0/0 trust' >> $PGDATA/pg_hba.conf
EXPOSE 5432
USER postgres

ENTRYPOINT $APP/postgres -i -D $PGDATA
