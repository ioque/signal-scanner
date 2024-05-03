FROM opensearchproject/logstash-oss-with-opensearch-output-plugin:latest
COPY infra/containers/logstash/pipeline /usr/share/logstash/pipeline
COPY infra/containers/logstash/pipelines.yml /usr/share/logstash/config/pipelines.yml

EXPOSE 5044
EXPOSE 5046
