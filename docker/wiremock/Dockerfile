FROM wiremock/wiremock:latest

# Create directories for mappings and files (optional, image already handles)
RUN mkdir -p /home/wiremock/mappings /home/wiremock/__files

# Copy stubs and responses into the container
COPY mappings/ /home/wiremock/mappings/
COPY __files/ /home/wiremock/__files/

# Expose WireMock default port 8080 (inside container)
EXPOSE 8080

# Start WireMock with the specified root directory and default port 8080
ENTRYPOINT ["/docker-entrypoint.sh"]
CMD ["--root-dir", "/home/wiremock"]