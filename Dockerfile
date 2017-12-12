FROM ubuntu:16.04
MAINTAINER Ville Piispa "ville.piispa@gmail.com"

RUN apt-get clean && apt-get update -y
RUN apt-get install locales
RUN locale-gen en_US.UTF-8
RUN update-locale LANG=en_US.UTF-8

RUN apt-get install -y postgresql-9.5 postgresql-client-9.5 postgresql-contrib-9.5

ADD src/main/sql/create_database.sql /create_database.sql
ADD hamsteri.dump /hamsteri.dump

USER postgres

RUN /etc/init.d/postgresql start &&\
    psql postgres -a -f /create_database.sql &&\
    echo "host all  all    0.0.0.0/0  md5" >> /etc/postgresql/9.5/main/pg_hba.conf &&\
    echo "listen_addresses='*'" >> /etc/postgresql/9.5/main/postgresql.conf &&\
    pg_restore -d hamsteri hamsteri.dump

EXPOSE 5432
VOLUME  ["/etc/postgresql", "/var/log/postgresql", "/var/lib/postgresql"]

CMD ["/usr/lib/postgresql/9.5/bin/postgres", "-D", "/var/lib/postgresql/9.5/main", "-c", "config_file=/etc/postgresql/9.5/main/postgresql.conf"]
