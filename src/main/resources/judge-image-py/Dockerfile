FROM python:3.11.14
WORKDIR /work
COPY compile.sh /compile.sh
COPY run.sh /run.sh
RUN chmod +x /compile.sh /run.sh
ENTRYPOINT ["/run.sh"]
