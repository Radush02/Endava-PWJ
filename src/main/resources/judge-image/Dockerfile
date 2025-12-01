FROM gcc:13
WORKDIR /work
COPY compile.sh /compile.sh
COPY run.sh /run.sh
RUN chmod +x /compile.sh /run.sh
ENTRYPOINT ["/run.sh"]