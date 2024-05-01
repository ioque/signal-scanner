FROM opensearchproject/logstash-oss-with-opensearch-output-plugin:latest
COPY containers/logstash/pipeline /usr/share/logstash/pipeline
COPY containers/logstash/pipelines.yml /usr/share/logstash/config/pipelines.yml

EXPOSE 5044
EXPOSE 5046
